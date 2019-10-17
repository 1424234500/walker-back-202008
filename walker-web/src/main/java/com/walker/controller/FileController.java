package com.walker.controller;

import com.walker.Response;
import com.walker.common.util.*;
import com.walker.config.Config;
import com.walker.config.Context;
import com.walker.dao.JdbcDao;
import com.walker.mode.FileIndex;
import com.walker.service.FileIndexService;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/file")
public class FileController {
    private org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    static int cacheSize = 4096;

    @Autowired
    @Qualifier("fileIndexService")
    private FileIndexService fileIndexService;

    @Autowired
    JdbcDao jdbcDao;


    @ApiOperation(value = "post 保存 更新/添加 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_ATIME", required = false, defaultValue = "") String sAtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "0") String sFlag,
            @RequestParam(value = "NAME", required = false, defaultValue = "") String name,
            @RequestParam(value = "INFO", required = false, defaultValue = "") String info,
            @RequestParam(value = "LENGTH", required = false, defaultValue = "") String length,
            @RequestParam(value = "PATH", required = false, defaultValue = "") String path,
            @RequestParam(value = "EXT", required = false, defaultValue = "") String ext,
            @RequestParam(value = "OWNER", required = false, defaultValue = "") String owner
    ) {
        FileIndex file = new FileIndex();
        file.setID(id);
        file.setS_MTIME(TimeUtil.getTimeYmdHms());
        file.setS_ATIME(sAtime.length() > 0 ? sAtime : TimeUtil.getTimeYmdHmss());
        file.setS_FLAG(sFlag.equalsIgnoreCase("1") ? "1" : "0");
        file.setNAME(name);
        file.setINFO(info);
        file.setLENGTH(length);
        file.setPATH(path);
        file.setEXT(ext);
        file.setOWNER(owner);

        List<FileIndex> res = fileIndexService.saveAll(Arrays.asList(file));
        return Response.makeTrue("", res);
    }

    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "ids", required = false, defaultValue = "") String ids
    ) {
        String info = "delete ids:" + ids;
        Object res = fileIndexService.deleteAll(Arrays.asList(ids.split(",")));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "get 获取", notes = "")
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.GET)
    public Response get(
            @RequestParam(value = "id", required = true) String id
    ) {
        String info = "get id:" + id;
        FileIndex model = fileIndexService.get(new FileIndex().setID(id));
        return Response.makeTrue(info, model);
    }

    @ApiOperation(value = "get findPage 分页查询", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_ATIME", required = false, defaultValue = "") String sAtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "0") String sFlag,
            @RequestParam(value = "NAME", required = false, defaultValue = "") String name,
            @RequestParam(value = "INFO", required = false, defaultValue = "") String info,
            @RequestParam(value = "LENGTH", required = false, defaultValue = "") String length,
            @RequestParam(value = "PATH", required = false, defaultValue = "") String path,
            @RequestParam(value = "EXT", required = false, defaultValue = "") String ext,
            @RequestParam(value = "OWNER", required = false, defaultValue = "") String owner,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        FileIndex file = new FileIndex();
        file.setID(id);
        file.setS_MTIME(sMtime);
        file.setS_ATIME(sAtime);
        file.setS_FLAG(sFlag);
        file.setNAME(name);
        file.setINFO(info);
        file.setLENGTH(length);
        file.setPATH(path);
        file.setEXT(ext);
        file.setOWNER(owner);
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        List<FileIndex> list = fileIndexService.finds(file, page);
        page.setNum(fileIndexService.count(file));
        return Response.makePage("", page, list);
    }


    @ApiOperation(value = "统计socket", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getColsMap.do", method = RequestMethod.GET)
    public Response getColsMap() {
        Map<String, String> colMap = FileUtil.getFileMap();
        Map<String, Object> res = new HashMap<>();
        res.put("colMap", colMap);
        res.put("colKey", colMap.keySet().toArray()[0]);
        return Response.makeTrue("file", res);
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
        FileIndex obj = new FileIndex().setID(id).setNAME(name);
        List<FileIndex> res = fileIndexService.finds(obj, page);
        return Response.makePage("", page, res);
    }

    @ApiOperation(value = "删除文件", notes = "")
    @ResponseBody
    @RequestMapping(value = "/delete.do", method = RequestMethod.GET)
    public Response delete(
            @RequestParam(value = "path", required = true, defaultValue = "") String path
    ) {
        int count = 0;
        String info = "";
        if (path.length() > 0) {
            if (path.startsWith(Config.getUploadDir())) {
                count += FileUtil.delete(path) ? 1 : 0;
                info = "影响数量" + count;
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
    public Response update(
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
        if (path.length() > 0) {
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

    @ApiOperation(value = "下载文件 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/download.do")
    public ResponseEntity<byte[]> download(
            @RequestParam(value = "key", required = false, defaultValue = "") String key,
            @RequestParam(value = "path", required = false, defaultValue = "") String path
    ) throws IOException {
        Watch w = new Watch(new Object[]{"download"});
        Boolean res = false;
        path = path.startsWith(Config.getDownloadDir()) ? path : Config.getUploadDir() + File.separator + path;
        path = new String(path.getBytes("iso-8859-1"), "utf-8");
        String info = "";
        FileIndex fileIndex = null;
        if (key.length() > 0) {
            fileIndex = fileIndexService.get(new FileIndex().setID(key));
            path = fileIndex == null ? path : fileIndex.getPATH();
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
        } else {
            info = "path is null ?";
        }

        w.put("path", new Object[]{path});
        w.put("info", new Object[]{info});
        if (!res) {
            w.res();
            return ResponseEntity.notFound().eTag(res.toString()).build();
        } else {
            String name = fileIndex == null ? FileUtil.getFileName(path) : fileIndex.getNAME();
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
            ResponseEntity<byte[]> entity = new ResponseEntity(body, headers, HttpStatus.OK);
            w.res(log);
            return entity;
        }
    }


    @ApiOperation(value = "上传文件", notes = "")
    @ResponseBody
    @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
    public Response upload(
            @RequestParam(value = "file", required = true) CommonsMultipartFile file,
            @RequestParam(value = "key", required = false, defaultValue = "") String key,
            @RequestParam(value = "dir", required = false, defaultValue = "") String dir
    ) {
        Watch w = new Watch(new Object[]{"upload"});
        String name = file.getOriginalFilename();
        String type = FileUtil.getFileType(name);

        String uploadDir = Config.getUploadDir();
        //按文件后缀名存储 日期存储
        String saveDir = dir.length() > 0 ? dir : (type.length() > 0 ? type + File.separator + TimeUtil.getTimeYmd() : TimeUtil.getTimeYmd());
        String pathTemp = uploadDir + File.separator + "temp";
        String pathTo = uploadDir + File.separator + saveDir;
        FileUtil.mkdir(pathTemp);
        FileUtil.mkdir(pathTo);

        try {
            File tempFile = new File(pathTemp + File.separator + name);
            file.transferTo(tempFile);
            if (key.length() == 0) {
                key = "k_" + FileUtil.checksumCrc32(tempFile);
            }
            String path = pathTo + File.separator + key;
            FileIndex fileIndex = fileIndexService.get(new FileIndex().setID(key));
            if (fileIndex == null) {
                w.put("新上传文件");
                FileUtil.mv(tempFile.getAbsolutePath(), path);
                fileIndex = new FileIndex();
                fileIndex.setID(key)
                        .setPATH(path)
                        .setNAME(name)
                        .setEXT(type)
                        .setINFO("upload")
                        .setOWNER("000")
                        .setS_ATIME(TimeUtil.getTimeYmdHms())
                        .setS_MTIME(TimeUtil.getTimeYmdHms())
                        .setLENGTH(tempFile.length() + "")
                ;
            } else {
                w.put("更新文件");
//                FileUtil.delete(tempFile.getAbsolutePath());
                fileIndex
                        .setINFO("更新")
                        .setS_MTIME(TimeUtil.getTimeYmdHms())
                ;
            }

            fileIndexService.saveAll(Arrays.asList(fileIndex));

            return Response.makeTrue(w.toPrettyString(), fileIndex);
        } catch (Exception var14) {
            w.exceptionWithThrow(var14);
            return Response.makeFalse(var14.toString());
        }
    }


    @ApiOperation(value = "文件目录", notes = "")
    @ResponseBody
    @RequestMapping(value = "/dir.do", method = RequestMethod.GET)
    public Response dir(
            @RequestParam(value = "dir", required = false, defaultValue = "") String dir
    ) {
        dir = dir.length() > 0 ? dir : Context.getUploadDir();
        if (FileUtil.check(dir) == 1) {
            List<?> list = FileUtil.ls(dir);
            Map<String, Object> res = new HashMap<>();
            res.put("list", res);
            res.put("dir", dir);
            return Response.makeTrue("", res);
        } else {
            return Response.makeFalse("dir 不是一个有效的文件夹" + dir);
        }
    }
}
