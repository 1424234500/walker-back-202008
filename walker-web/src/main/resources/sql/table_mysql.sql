
--测试表
CREATE TABLE  IF NOT EXISTS  student (id VARCHAR(40), name varchar(200), time varchar(30));

--文件映射表
--int res = baseDao.executeSql("insert into fileinfo"
--				+ "(id,                   uptime, name,filesize,  type,path,changetime               ,about,upuserid ) values "
--				+ "(?, sysdate,?    ,?      ,  ?   ,?    ,"+ SqlHelp.to_dateL() +",?, ?   ) "
--				                                 , key, name ,filesize ,type,path,changetime               ,about, id   );
CREATE TABLE  IF NOT EXISTS  fileinfo (id VARCHAR(40), uptiime varchar(30), name varchar(200), filesize varchar(10), type varchar(20), path varchar(400), changetime varchar(30), about varchar(512), upuserid varchar(40) );

--接口访问统计控制
-- baseDao.executeSql("insert into log_time"
--							+ "(id, url, count, time, costtime) "
--							+ "values"
--							+ "(?, ?, ?, sysdate, ?) "
--							, LangUtil.getGenerateId(), map.get("url") + ".do", map.get("count"), map.get("costtime") 
CREATE TABLE  IF NOT EXISTS  log_time (id VARCHAR(40), url varchar(200), count varchar(10), time varchar(30), costtime varchar(10) );




