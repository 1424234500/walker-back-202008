

---
---S_MTIME  VARCHAR2(32)	 long/字符串ymdhms 修改时间 自动设置	YYYY-MM-DD HH24:MM-SS:SSS
---S_ATIME  VARCHAR2(32)	 long/字符串ymdhms 添加时间 自动设置	YYYY-MM-DD HH24:MM-SS:SSS
---S_FLAG   VARCHAR2(2)      0/1     假删除 配合S_MTIME增量查询
---




drop table W_SHARDING_MSG_0;
drop table W_SHARDING_MSG_1;


drop table W_SHARDING_MSG_USER_0;
drop table W_SHARDING_MSG_USER_1;
drop table W_SHARDING_MSG_USER_2;
drop table W_SHARDING_MSG_USER_3;


drop table W_MAN_0;
drop table W_MAN_1;
drop table W_MAN_2;
drop table W_MAN_3;




-- 用户 数据库 分表
-- CREATE DATABASE IF NOT EXISTS walker default charset utf8 COLLATE utf8_general_ci;
-- GRANT ALL PRIVILEGES ON *.* TO 'walker'@'%' IDENTIFIED BY 'qwer' WITH GRANT OPTION;




---
--tomcat web系统监控 统计
---
--文件映射表
DROP TABLE W_FILEINFO;
CREATE TABLE  IF NOT EXISTS  W_FILEINFO (ID VARCHAR(40), UPTIME VARCHAR(30), name VARCHAR(200), FILESIZE VARCHAR(10), TYPE VARCHAR(20), PATH VARCHAR(400), CHANGETIME VARCHAR(30), ABOUT VARCHAR(512), UPUSERID VARCHAR(40) );
TRUNCATE TABLE W_FILEINFO;
DROP TABLE W_FILE_DOWN_UP;
CREATE TABLE  IF NOT EXISTS  W_FILE_DOWN_UP (ID VARCHAR(40), FILEID VARCHAR(40),TYPE VARCHAR(40), COSTTIME VARCHAR(10), time VARCHAR(30) );
TRUNCATE TABLE W_FILE_DOWN_UP;

--接口访问统计控制
DROP TABLE W_LOG_TIME;
CREATE TABLE  IF NOT EXISTS  W_LOG_TIME (IPPORT VARCHAR(40), ID VARCHAR(40), URL VARCHAR(200), COUNT VARCHAR(10), time VARCHAR(30), COSTTIME VARCHAR(10) );
TRUNCATE TABLE W_LOG_TIME;
--接口访问记录
DROP TABLE W_LOG_INFO;
CREATE TABLE  IF NOT EXISTS  W_LOG_INFO (ID VARCHAR(40), time VARCHAR(30), USERID VARCHAR(40), URL VARCHAR(200), IP VARCHAR(32), MAC VARCHAR(64), PORT VARCHAR(10), ABOUT VARCHAR(512) );
TRUNCATE TABLE W_LOG_INFO;




