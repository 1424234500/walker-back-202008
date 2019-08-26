package com.walker.service.impl;

import com.walker.common.util.*;
import com.walker.dao.JdbcDao;
import com.walker.service.FileService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Transactional
@Service("fileService")
public class FileServiceImpl implements FileService,Serializable {
	private static final long serialVersionUID = 8304941820771045214L;
	private static Logger log = Logger.getLogger("File");
	
	
    @Autowired
    protected JdbcDao jdbcDao;
    //info:
//文件管理 W_FILEINFO: id,name,upuserid,filesize,type,path,uptime,createtime,changetime,about

    
	@Override
	public void initDirs() {
		log.info("** 初始化项目相关文件夹");
		FileUtil.mkdir( com.walker.event.Context.getUploadDir());
		log.info("**! 初始化项目相关文件夹");
	}
    
	@Override
	public void saveScan() {
		//删除表中中不存在文件的记录 删除失效文件
		//添加分页循环处理
		
		int count = jdbcDao.count("select * from W_FILEINFO");
		int once = Context.getDbOnce();
		Page pageBean = new Page(once, count);
		int page = pageBean.getPagenum();
		while(page > 0){
			List<Map<String, Object>> list = jdbcDao.findPage("select * from W_FILEINFO", page--, once);
			
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < list.size(); i++){
				String path = MapListUtil.getList(list, i, "PATH");
				File file = new File(path);
				if(!file.exists() || file.isDirectory() ){ //删除不存在的 或者文件夹
					sb.append("'").append(path).append("'").append(",");
				}
			} 
			if(sb.length() > 0){
				sb.setLength(sb.length() - 1);
				jdbcDao.executeSql("delete from W_FILEINFO where PATH in (" + sb.toString() + ") ");
			}

		}
		//添加其它文件到表中 策略变更 不再扫描文件加入数据库
		/*
		 	List<File> lf = FileUtil.showDirAsync(Context.getScanDirs(), new Fun<File>() {
			@Override
			public Object make(File obj) {
				if(obj.isFile()){
					int flag = 0;
					for(int i = 0; i < list.size(); i++){
						if( obj.getPath().equals(MapListUtil.getList(list, i, "PATH"))){
							flag = 1;	//已存在该文件的记录
							break;
						} 
					}
					if(flag == 0){
						String name = obj.getName();
						String filesize = ""+obj.length();
						String type = FileUtil.getFileType(name);
						String path = obj.getPath();
						String changetime = Tools.formatL(new Date(obj.lastModified()));
						String about = path;
						jdbcDao.executeSql("insert into W_FILEINFO"
								+ "(id,                   uptime, name,filesize,  type,path,changetime               ,about ) values "
								+ "(SEQ_W_FILEINFO.Nextval, sysdate,?    ,?      ,  ?   ,?    ,"+ SqlHelp.to_dateL() +",?   ) "
								                                 ,name ,filesize ,type,path,changetime               ,about    );
						log.info("添加文件记录：" + name);
					}
				}
				return obj;
			}
		});  
		*/

	}
	public Map<String,Object> findFile(String key, String path){
		return jdbcDao.findOne("select * from W_FILEINFO where ID=? or PATH=? ", key, path);
	}
	public void deleteFile(String key, String path) {
		jdbcDao.executeSql("delete from W_FILEINFO where ID=? or PATH=? ", key, path);
	}


	@Override
	public int saveUpload(String key, String id, String name, String path, String about) {
		File file = new File(path);
		String filesize = ""+file.length();
		String type = FileUtil.getFileType(name);
		String changetime = Tools.formatL(new Date(file.lastModified()));
		about = Tools.cutString(about, 500);
//		String key = jdbcDao.getString("select SEQ_W_FILEINFO.Nextval from dual");
//		String key = LangUtil.getGenerateId();
		int res = -1;
		Map<String,Object> map = findFile(key, path);
		if(map != null) {
			String pathDel = String.valueOf(map.get("PATH"));
			boolean resDel = FileUtil.delete(pathDel);
			deleteFile(key, path);
			log.info("该文件已经存在 相同key " + map + " " + path + " delete " + pathDel + " 删除文件结果:" + resDel);
			res ++;
		}
		res += jdbcDao.executeSql("insert into W_FILEINFO"
			+ "(ID,                   UPTIME, NAME,FILESIZE,  TYPE,PATH,CHANGETIME               ,ABOUT,UPUSERID ) values "
			+ "(?, ?,?    ,?      ,  ?   ,?    ,?,?, ?   ) "
			                                 , key, TimeUtil.getTimeYmdHmss(),name,filesize ,type,path,TimeUtil.getTimeYmdHmss()               ,about, id   )
				;
		return res;
	}

	@Override
	public int saveUpOrDown(String fileId, String type, String detaTime) {
        // id,fileid,type(up/down),costtime(ms),time
		return jdbcDao.executeSql("insert into W_FILE_DOWN_UP"
				+ "(ID, FILEID, TYPE, COSTTIME, TIME)"
				+" values "
				+" (?, ?, ?, ?, ?) "
				,LangUtil.getGenerateId(), fileId, type, detaTime, TimeUtil.getTimeYmdHmss());
		
		
		
	}
    
 

}