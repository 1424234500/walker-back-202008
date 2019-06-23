package com.walker.web.controller;
 

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.walker.common.util.FileUtil;
import com.walker.common.util.MapListUtil;
import com.walker.common.util.Page;
import com.walker.common.util.Tools;
import com.walker.core.database.SqlUtil;
import com.walker.core.mode.Watch;
import com.walker.service.FileService;
import com.walker.web.RequestUtil; 


@Controller
@RequestMapping("/file")
public class FileControll extends BaseControll{
	public FileControll() {
		super(FileControll.class, "");
	}

	private static Logger log = Logger.getLogger(FileControll.class); 
	@Autowired
	@Qualifier("fileService") 
	protected FileService fileService;
	
	static int cacheSize = 4096;
	
	@RequestMapping("/fileCols.do")
	public void fileCols(HttpServletRequest request, HttpServletResponse response){
		echo(FileUtil.getFileMap());
	}
	@RequestMapping("/fileDirUpload.do")
	public void fileDirUpload(HttpServletRequest request, HttpServletResponse response){
		echo(Context.getUploadDir());
	}
	@RequestMapping("/fileDir.do")
	public void fileDir(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String dir = request.getParameter("dir");
		String name = request.getParameter("name");
		String newdir = request.getParameter("newdir");
		

		if( ! Tools.notNull(dir)){
			dir = Context.getUploadDir();
		}
		if( Tools.notNull(newdir, name)){
			FileUtil.mkdir(dir + File.separator + name);
		}
		int type = FileUtil.check(dir);
		if(type == 1){ //若是文件夹
			echo( FileUtil.ls(dir));
		}else if(type == 0){ //文件
			this.download(request, response);
		}
	} 
	
	
	@RequestMapping("/list.do")
	public void list(HttpServletRequest request, HttpServletResponse response){ 
		String id = request.getParameter("ID");
		String name = request.getParameter("NAME");
		String timefrom = request.getParameter("TIMEFROM");
		String timeto = request.getParameter("TIMETO");
 
		Page page = Page.getPage(request);

		List<String> params = new ArrayList<String>();
		String sql = "select ID,(select count(*) from FILE_DOWN_UP where FILEID=f.ID and TYPE='down') COUNT,NAME,UPUSERID,TYPE,file_size(filesize) FILESIZE,to_char(UPTIME," + SqlUtil.getTimeFormatL() + ") UPTIME, CHANGETIME,ABOUT from FILEINFO f where 1=1 ";
		if(Tools.notNull(id)){
			sql += " and ID like ? ";
			params.add("%" + id + "%");
		} 
		if(Tools.notNull(name)){
			sql += " and NAME like ? ";
			params.add("%" + name + "%");
		}
		if(Tools.notNull(timefrom)){
			sql += " and UPTIME >= " + SqlUtil.to_dateL();
			params.add(timefrom);
		}
		if(Tools.notNull(timeto)){
			sql += " and UPTIME <= " + SqlUtil.to_dateL();
			params.add( timeto);
		} 
	    List<Map<String, Object>> res = baseService.findPage(page, sql, params.toArray() );
	    log(res, page);
	    echo( res, page);
	} 
	

	
	@RequestMapping("/delete.do")
	public void delete(HttpServletRequest request, HttpServletResponse response){ 
		String path = request.getParameter("PATH");  
		int count = 0;
		String info = "";
		if(Tools.notNull(path)){
			if(path.startsWith(Context.getUploadDir())){
//				count = baseService.executeSql("delete from FILEINFO where path=?", path);
				FileUtil.delete(path);
			}else{
				info = "无修改权限" + path;
			}
		}else{
			info = "路径为null";
		}
		
		echo(info.length()==0, info, count);
	}
	@RequestMapping("/update.do")
	public void update(HttpServletRequest request, HttpServletResponse response){ 
		String path = request.getParameter("PATH");  //新路径 
		String oldPath = request.getParameter("OLDPATH");   //全路径 path/file
		String oldName = request.getParameter("OLDNAME");  
		String name = request.getParameter("NAME");  //新名字
		if(name== null || name.length() == 0) {
			echo(false, "重命名不能为空");
		}
		int count = 0;
		String info = "";
		if(Tools.notNull(path)){
			if((path+File.separator).startsWith(Context.getUploadDir()) 
					&& (oldPath).startsWith(Context.getUploadDir())
					&& Tools.notNull(oldPath, path)){
				FileUtil.mv(oldPath, path + File.separator + name);
			}else{
				info = "无修改权限" + path;
			}
		}else{
			info = "路径为null";
		}
		echo(info.length()==0, info, count);
	}
	
	@RequestMapping("/updatetable.do")
	public void updateTable(HttpServletRequest request, HttpServletResponse response){ 
		String id = request.getParameter("PATH"); 
		String about = request.getParameter("ABOUT"); 
	    
		int count = baseService.executeSql("update FILEINFO set ABOUT=? where PATH=? ", about, id);
		Map res = MapListUtil.getMap().put("res", count).build();
		echo( res);	
	}

	@RequestMapping("/get.do")
	public void get(HttpServletRequest request, HttpServletResponse response){ 
		String path = request.getParameter("PATH");  
		Map map = FileUtil.getFileMap(path);
		echo( map);	
	}
	 /**  
     * 文件下载
     * path 若有则按照path下载
     * key  则按照key 映射path下载
     * 
     */  
    @RequestMapping("/download.do")  
    public void download(HttpServletRequest request,HttpServletResponse response) throws Exception{  
    	Watch w = new Watch("download");
    	String path = getValue(request, "PATH");
    	String key = getValue(request, "KEY");
    	if(key.length() == 0) {
    		key = getValue(request, "ID");
    	}
//		String path1 = new String(path.getBytes("iso-8859-1"), "gbk");
//		String path3 = URLDecoder.decode(path, "utf-8");
//		String path4 = URLDecoder.decode(path);
		path = new String(path.getBytes("iso-8859-1"), "utf-8");
		Boolean res = false;
		String info = "";
		if(key.length() > 0){ //key 映射 path 方式
			Map<String, Object> map = baseService.findOne("select * from FILEINFO where ID=?", key);
			path = MapListUtil.getMap(map, "ID", "");
		}
		if(path.length() <= 0 && key.length() > 0) {
			path = Context.getUploadDir() + File.separator + key;
		}
		if(path.length() > 0){ //处理path分析文件
			int type = FileUtil.check(path);
			if(type == 1){
				info = path + " 是文件夹";
			}else if(type == 0){
				info = path + " 存在";
				res = true;
			}else{
				info = path + " 不存在";
			}
		}
		if(!res){
			echo(res, info);
		}else {
			String name = FileUtil.getFileName(path);
	        RequestUtil.setHeaderDownFile(request, response, name);
	        OutputStream os = null;
	        try{
	        	os = response.getOutputStream();
	        	int length = FileUtil.readFile(path, os);
	 	        echo (length > 0, ""+length, w.toString());
	        }catch(Exception e) {
	        	w.exceptionWithThrow(e, log);
	        }  finally {
	        	if(os != null)
	        		os.close();
	        }
		}
    }  
    
    /**
     * 上传文件
     * 存入文件系统 path路径
     * 
     * 生产key 存入数据库映射路径和key 以md5为key 文件去重
     * 序列 返回key
     * 
     */
	@RequestMapping(value="/upload.do",method=RequestMethod.POST)
    public void upload(HttpServletRequest request,  PrintWriter pw) throws IOException{
        MultipartHttpServletRequest mreq = (MultipartHttpServletRequest)request;
        MultipartFile file = mreq.getFile("file");
        String uppath = request.getParameter("path");
        if(uppath == null || uppath.length() == 0) {
        	uppath = Context.getUploadDir();
        }
		if(uppath.indexOf(Context.getUploadDir()) != 0){
			echo(false, "无修改权限" + uppath);
			return;
		}
        
		Watch w = new Watch("upload");
        String name = file.getOriginalFilename();
        String dir = Tools.notNull(uppath) ? uppath : Context.getUploadDir();
		String type = FileUtil.getFileType(name);
        String pathTemp = dir + File.separator + "temp" + File.separator + name;
        FileUtil.mkdir(dir + File.separator + "temp");
        InputStream is = null;
        try {
	        try{
	        	is = request.getInputStream();
	            FileUtil.saveFile(is, pathTemp, false);
	            
	            //获取文件识别码
	            String key = "k_" + FileUtil.checksumCrc32(new File(pathTemp));
	            String path = dir + File.separator + key + "." + type;
//	            保存数据库文件 并自动删除重复id path
	            int sta = fileService.saveUpload(key, getUser().getId(), name, path, ""); 
	            //移动文件到目标路径
	            FileUtil.mv(pathTemp, path);
	            if(sta == 0 || sta == 1) {	
		            fileService.saveUpOrDown(key, "up", w.getTimeAll()+"");
		            echo(true, "" + sta, key);
	            }else {
		            echo(false, "异常" + sta, path + " " + w.toString());
	            }
	        }finally {
	        	if(is != null)
	        		is.close();	
	        }
        }catch (Exception e) {
        	w.exceptionWithThrow(e, log);
        } 

    }
	
	/*
     * 采用file.Transto 来保存上传的文件
     */
    @RequestMapping("uploadCmf")
    public void uploadCmf(@RequestParam("file") CommonsMultipartFile file,HttpServletRequest request,HttpServletResponse response) throws Exception{  
    	Watch w = new Watch("upload");
        String name = file.getOriginalFilename();
        String dir = Context.getUploadDir();
        String pathTemp = dir + File.separator + "temp" + File.separator + name;
        FileUtil.mkdir(dir + File.separator + "temp");
        try {
            File newFile=new File(pathTemp);
            //通过CommonsMultipartFile的方法直接写文件（注意这个时候）
            file.transferTo(newFile);
        	
    		String type = FileUtil.getFileType(name);
            //获取文件识别码
            String key = "k_" + FileUtil.checksumCrc32(new File(pathTemp)) + "." + type;
            String path = dir + File.separator + key;
            //保存数据库文件 并自动删除重复id path
            int sta = fileService.saveUpload(key, getUser().getId(), name, path, ""); 
            //移动文件到目标路径
            FileUtil.mv(pathTemp, path);
            if(sta == 0 || sta == 1) {	
	            fileService.saveUpOrDown(key, "up", w.getTimeAll()+"");
	            echo(true, "" + sta, key);
            }else {
	            echo(false, "异常" + sta, path + " " + w.toString());
            }
        }catch (Exception e) {
        	w.exceptionWithThrow(e, log);
        } 
    }
	
	@Override
	public void log(Object... objs) {
		 log.info(Tools.objects2string(objs));
	}
    
}