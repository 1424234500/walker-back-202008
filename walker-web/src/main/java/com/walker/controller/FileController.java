package com.walker.controller;

import com.walker.Response;
import com.walker.common.util.*;
import com.walker.config.Config;
import com.walker.core.database.SqlUtil;
import com.walker.dao.JdbcDao;
import com.walker.service.FileService;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/file"})
public class FileController  {
    private org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    static int cacheSize = 4096;

    @Autowired
    @Qualifier("fileService")
    private FileService fileService;

    @Autowired
    JdbcDao jdbcDao;

    @ApiOperation(value = "统计socket", notes = "")
    @ResponseBody
    @RequestMapping(value = "/fileCols.do", method = RequestMethod.GET)
    public Response fileCols() {
        return Response.makeTrue("", FileUtil.getFileMap());
    }

    @ApiOperation(value = "统计socket", notes = "")
    @ResponseBody
    @RequestMapping(value = "/fileDirUpload.do", method = RequestMethod.GET)
    public Response fileDirUpload() {
        return Response.makeTrue("",  Config.getUploadDir());
    }

    @RequestMapping({"/fileDir.do"})
    public void fileDir(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String dir = request.getParameter("dir");
        String name = request.getParameter("name");
        String newdir = request.getParameter("newdir");
        if (!Tools.notNull(new Object[]{dir})) {
            dir = Config.getUploadDir();
        }

        if (Tools.notNull(new Object[]{newdir, name})) {
            FileUtil.mkdir(dir + File.separator + name);
        }

        int type = FileUtil.check(dir);
        if (type == 1) {
            log.info(FileUtil.ls(dir).toString());
        } else if (type == 0) {
            this.download(request, response);
        }

    }

    @ApiOperation(value = "文件上传下载分页查询", notes = "")
    @ResponseBody
    @RequestMapping(value = "/list.do", method = RequestMethod.GET, produces = "application/json")
    public Response add(
            @RequestParam(value = "id", required = false, defaultValue = "") String id,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "timefrom", required = false, defaultValue = "") String timefrom,
            @RequestParam(value = "timeto", required = false, defaultValue = "") String timeto,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        List<String> params = new ArrayList();
        String sql = "select ID,(select count(*) from FILE_DOWN_UP where FILEID=f.ID and TYPE='down') COUNT,NAME,UPUSERID,TYPE,file_size(filesize) FILESIZE,to_char(UPTIME," + SqlUtil.getTimeFormatL() + ") UPTIME, CHANGETIME,ABOUT from FILEINFO f where 1=1 ";
        if (Tools.notNull(new Object[]{id})) {
            sql = sql + " and ID like ? ";
            params.add("%" + id + "%");
        }

        if (Tools.notNull(new Object[]{name})) {
            sql = sql + " and NAME like ? ";
            params.add("%" + name + "%");
        }

        if (Tools.notNull(new Object[]{timefrom})) {
            sql = sql + " and UPTIME >= " + SqlUtil.to_dateL();
            params.add(timefrom);
        }

        if (Tools.notNull(new Object[]{timeto})) {
            sql = sql + " and UPTIME <= " + SqlUtil.to_dateL();
            params.add(timeto);
        }

        List<Map<String, Object>> res = this.jdbcDao.findPage(page, sql, params.toArray());
        this.log(res, page);
        return Response.makePage("", page, res);
    }

    @ApiOperation(value = "删除文件", notes = "")
    @ResponseBody
    @RequestMapping(value = "/delete.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "path", required = true, defaultValue = "") String path
    ) {
        int count = 0;
        String info = "";
        if (Tools.notNull(new Object[]{path})) {
            if (path.startsWith(Config.getUploadDir())) {
                FileUtil.delete(path);
            } else {
                info = "无修改权限" + path;
            }
        } else {
            info = "路径为null";
        }
        return Response.make(info.length() == 0, info, count);
    }

    @ApiOperation(value = "修改文件", notes = "")
    @ResponseBody
    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "path", required = true, defaultValue = "") String path,
            @RequestParam(value = "oldPath", required = false, defaultValue = "") String oldPath,
            @RequestParam(value = "oldName", required = false, defaultValue = "") String oldName,
            @RequestParam(value = "name", required = true, defaultValue = "") String name
    ) {
        if (name == null || name.length() == 0) {
            name = FileUtil.getFileName(oldPath);
        }

        int count = 0;
        String info = "";
        if (Tools.notNull(new Object[]{path})) {
            if ((path + File.separator).startsWith(Config.getUploadDir()) && oldPath.startsWith(Config.getUploadDir()) && Tools.notNull(new Object[]{oldPath, path})) {
                FileUtil.mv(oldPath, path + File.separator + name);
            } else {
                info = "无修改权限" + path;
            }
        } else {
            info = "路径为null";
        }

        return Response.make(info.length() == 0, info, count);
    }

    @RequestMapping({"/download.do"})
    public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Watch w = new Watch(new Object[]{"download"});
        String path = request.getParameter("path");
        String key = request.getParameter("key");
        if (key.length() == 0) {
            key = request.getParameter("id");
        }

        path = new String(path.getBytes("iso-8859-1"), "utf-8");
        Boolean res = false;
        String info = "";
        if (key.length() > 0) {
            Map<String, Object> map = this.jdbcDao.findOne("select * from FILEINFO where ID=?", new Object[]{key});
            path = (String)MapListUtil.getMap(map, "PATH", "");
        }

        if (path.length() <= 0 && key.length() > 0) {
            path = Config.getUploadDir() + File.separator + key;
        }

        if (path.length() > 0) {
            int type = FileUtil.check(path);
            if (type == 1) {
                info = path + " 是文件夹";
            } else if (type == 0) {
                info = path + " 存在";
                res = true;
            } else {
                info = path + " 不存在";
            }
        }

        if (!res) {
            log.error(info);
        } else {
            String name = FileUtil.getFileName(path);
//            RequestUtil.setHeaderDownFile(request, response, name);
            ServletOutputStream os = null;

            try {
                os = response.getOutputStream();
                int length = FileUtil.readFile(path, os);
//                RequestUtil.setDownFileLength(response, length);
                w.res(length);
                log.info(w.toPrettyString());
            } catch (Exception var14) {
                w.exceptionWithThrow(var14);
                log.info(w.toPrettyString());
            } finally {
                if (os != null) {
                    os.close();
                }

            }
        }

    }

    @RequestMapping(
            value = {"/upload.do"},
            method = {RequestMethod.POST}
    )
    public void upload(HttpServletRequest request, PrintWriter pw) throws IOException {
        MultipartHttpServletRequest mreq = (MultipartHttpServletRequest)request;
        MultipartFile file = mreq.getFile("file");
        String uppath = request.getParameter("path");
        String key = request.getParameter("key");
        if (uppath == null || uppath.length() == 0) {
            uppath = Config.getUploadDir();
        }

        if (uppath.indexOf(Config.getUploadDir()) != 0) {
            log.error("无修改权限" + uppath);
        } else {
            Watch w = new Watch(new Object[]{"upload"});
            String name = file.getOriginalFilename();
            String dir = Tools.notNull(new Object[]{uppath}) ? uppath : Config.getUploadDir();
            String type = FileUtil.getFileType(name);
            String pathTemp = dir + File.separator + "temp" + File.separator + name;
            FileUtil.mkdir(dir + File.separator + "temp");
            ServletInputStream is = null;

            try {
                try {
                    is = request.getInputStream();
                    FileUtil.saveFile(is, pathTemp, false);
                    if (key.length() == 0) {
                        key = "k_" + FileUtil.checksumCrc32(new File(pathTemp));
                    }

                    String path = dir + File.separator + key + "." + type;
                    int sta = this.fileService.saveUpload(key, "user", name, path, "");
                    FileUtil.mv(pathTemp, path);
                    if (sta != 0 && sta != 1) {
                        log.error("异常" + sta, path + " " + w.toString());
                    } else {
                        this.fileService.saveUpOrDown(key, "up", w.getTimeAll() + "");
                        log.error( "" + sta, key);
                    }
                } finally {
                    if (is != null) {
                        is.close();
                    }

                }
            } catch (Exception var19) {
                w.exceptionWithThrow(var19);
            }

        }
    }

    @RequestMapping({"/downloadRe"})
    public ResponseEntity<byte[]> downloadRe(HttpServletRequest request) throws IOException {
        Watch w = new Watch(new Object[]{"downloadRe"});
        String path = request.getParameter("path");
        String key = request.getParameter("key");
        if (key.length() == 0) {
            key = request.getParameter("id");
        }

        path = new String(path.getBytes("iso-8859-1"), "utf-8");
        Boolean res = false;
        String info = "";
        if (key.length() > 0) {
            Map<String, Object> map = this.jdbcDao.findOne("select * from FILEINFO where ID=?", new Object[]{key});
            path = (String)MapListUtil.getMap(map, "PATH", "");
        }

        if (path.length() <= 0 && key.length() > 0) {
            path = Config.getUploadDir() + File.separator + key;
        }

        if (path.length() > 0) {
            int type = FileUtil.check(path);
            if (type == 1) {
                info = path + " 是文件夹";
            } else if (type == 0) {
                info = path + " 存在";
                res = true;
            } else {
                info = path + " 不存在";
            }
        }

        w.put("path", new Object[]{path});
        w.put("info", new Object[]{info});
        if (!res) {
            w.res();
            return ResponseEntity.notFound().eTag(res.toString()).build();
        } else {
            String name = FileUtil.getFileName(path);
            File file = new File(path);
            byte[] body = null;
            FileInputStream is = null;

            try {
                is = new FileInputStream(file);
                body = new byte[is.available()];
                is.read(body);
            } catch (Exception var16) {
                w.exception(var16);
            } finally {
                if (is != null) {
                    is.close();
                }

            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attchement;filename=" + name);
            String userbrowser = request.getHeader("UserSocket-Agent");
            w.put(userbrowser);
//            headers.add("Content-Disposition", RequestUtil.getDispo(userbrowser, name));
            ResponseEntity<byte[]> entity = new ResponseEntity(body, headers, HttpStatus.OK);
            w.res();
            return entity;
        }
    }

    @RequestMapping({"uploadCmf"})
    public void uploadCmf(@RequestParam("file") CommonsMultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Watch w = new Watch(new Object[]{"uploadCmf"});
        String name = file.getOriginalFilename();
        String key = request.getParameter("key");
        String dir = Config.getUploadDir();
        String pathTemp = dir + File.separator + "temp" + File.separator + name;
        FileUtil.mkdir(dir + File.separator + "temp");

        try {
            File newFile = new File(pathTemp);
            file.transferTo(newFile);
            String type = FileUtil.getFileType(name);
            if (key.length() == 0) {
                key = "k_" + FileUtil.checksumCrc32(new File(pathTemp)) + "." + type;
            }

            String path = dir + File.separator + key;
            int sta = this.fileService.saveUpload(key, "user", name, path, "");
            FileUtil.mv(pathTemp, path);
            if (sta != 0 && sta != 1) {
                log.error("异常" + sta, path + " " + w.toString());
            } else {
                this.fileService.saveUpOrDown(key, "up", w.getTimeAll() + "");
                log.error( "" + sta, key);
            }
        } catch (Exception var14) {
            w.exceptionWithThrow(var14 );
        }

    }

    public void log(Object... objs) {
        log.info(Tools.objects2string(objs));
    }
}
