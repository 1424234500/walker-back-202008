
--测试表
CREATE TABLE  IF NOT EXISTS  student (id VARCHAR(40), name varchar(200), time varchar(30));

--文件映射表
CREATE TABLE  IF NOT EXISTS  fileinfo (id VARCHAR(40), uptiime varchar(30), name varchar(200), filesize varchar(10), type varchar(20), path varchar(400), changetime varchar(30), about varchar(512), upuserid varchar(40) );

--接口访问统计控制
CREATE TABLE  IF NOT EXISTS  log_time (id VARCHAR(40), url varchar(200), count varchar(10), time varchar(30), costtime varchar(10) );
--接口访问记录
CREATE TABLE  IF NOT EXISTS  log_info (id VARCHAR(40), time varchar(30), userid varchar(40), url varchar(200), ip varchar(32), mac varchar(64), port varchar(10), about varchar(512) );

