---
---s_mtime		修改时间	自动设置	yyyy-MM-dd HH24:mm-ss:sss
---s_flag			假删除 配合s_mtime增量查询
---


--测试表
--drop table student;
CREATE TABLE  IF NOT EXISTS  student (id VARCHAR(40), name varchar(200), s_mtime varchar(30), s_flag varchar(4));

--文件映射表
CREATE TABLE  IF NOT EXISTS  fileinfo (id VARCHAR(40), uptime varchar(30), name varchar(200), filesize varchar(10), type varchar(20), path varchar(400), changetime varchar(30), about varchar(512), upuserid varchar(40) );
--[INFO][17:42:37] insert into fileinfo(id,                   uptime, name,filesize,  type,path,changetime               ,about,upuserid ) values ('294818687017769_kLTCt8', sysdate,'2019-04-03 17-16-08 的屏幕截图.png'    ,'0'      ,  'png'   ,'/home/walker/tomcat/2019-04-03 17-16-08 的屏幕截图.png'    , to_date('2019-05-24 17:42:37', 'yyyy-mm-dd hh24:mi:ss') ,'', ''   )  [0:0:0:0:0:0:0:1:44740][com.walker.web.dao.hibernate.impl.BaseDaoImpl.out(BaseDaoImpl.java:26)][http-nio-8080-exec-4]
--上传下载记录 + "(id, fileid, type, costtime, time),LangUtil.getGenerateId(), fileId, type, detaTime, TimeUtil.getTimeYmdHmss());
CREATE TABLE  IF NOT EXISTS  file_down_up (id VARCHAR(40), fileid varchar(40),type varchar(40), costtime varchar(10), time varchar(30) );

--接口访问统计控制
CREATE TABLE  IF NOT EXISTS  log_time (id VARCHAR(40), url varchar(200), count varchar(10), time varchar(30), costtime varchar(10) );
--接口访问记录
CREATE TABLE  IF NOT EXISTS  log_info (id VARCHAR(40), time varchar(30), userid varchar(40), url varchar(200), ip varchar(32), mac varchar(64), port varchar(10), about varchar(512) );

 

