---
---S_MTIME		修改时间	自动设置	YYYY-MM-DD HH24:MM-SS:SSS
---S_FLAG			假删除 配合S_MTIME增量查询
---


--测试表
--DROP TABLE STUDENT;
CREATE TABLE  IF NOT EXISTS  STUDENT (ID VARCHAR(40), NAME VARCHAR(200), S_MTIME VARCHAR(30), S_FLAG VARCHAR(4));

--文件映射表
CREATE TABLE  IF NOT EXISTS  FILEINFO (ID VARCHAR(40), UPTIME VARCHAR(30), NAME VARCHAR(200), FILESIZE VARCHAR(10), TYPE VARCHAR(20), PATH VARCHAR(400), CHANGETIME VARCHAR(30), ABOUT VARCHAR(512), UPUSERID VARCHAR(40) );
--[INFO][17:42:37] INSERT INTO FILEINFO(ID,                   UPTIME, NAME,FILESIZE,  TYPE,PATH,CHANGETIME               ,ABOUT,UPUSERID ) VALUES ('294818687017769_KLTCT8', SYSDATE,'2019-04-03 17-16-08 的屏幕截图.PNG'    ,'0'      ,  'PNG'   ,'/HOME/WALKER/TOMCAT/2019-04-03 17-16-08 的屏幕截图.PNG'    , TO_DATE('2019-05-24 17:42:37', 'YYYY-MM-DD HH24:MI:SS') ,'', ''   )  [0:0:0:0:0:0:0:1:44740][COM.WALKER.WEB.DAO.HIBERNATE.IMPL.BASEDAOIMPL.OUT(BASEDAOIMPL.JAVA:26)][HTTP-NIO-8080-EXEC-4]
--上传下载记录 + "(ID, FILEID, TYPE, COSTTIME, TIME),LANGUTIL.GETGENERATEID(), FILEID, TYPE, DETATIME, TIMEUTIL.GETTIMEYMDHMSS());
CREATE TABLE  IF NOT EXISTS  FILE_DOWN_UP (ID VARCHAR(40), FILEID VARCHAR(40),TYPE VARCHAR(40), COSTTIME VARCHAR(10), TIME VARCHAR(30) );

--接口访问统计控制
CREATE TABLE  IF NOT EXISTS  LOG_TIME (ID VARCHAR(40), URL VARCHAR(200), COUNT VARCHAR(10), TIME VARCHAR(30), COSTTIME VARCHAR(10) );
--接口访问记录
CREATE TABLE  IF NOT EXISTS  LOG_INFO (ID VARCHAR(40), TIME VARCHAR(30), USERID VARCHAR(40), URL VARCHAR(200), IP VARCHAR(32), MAC VARCHAR(64), PORT VARCHAR(10), ABOUT VARCHAR(512) );

 
--消息 分表ID 消息id - 消息json串
--DROP TABLE W_MSG_0;
--DROP TABLE W_MSG_1;
CREATE TABLE  IF NOT EXISTS  W_MSG_0 (ID VARCHAR(40), TEXT TEXT);
CREATE TABLE  IF NOT EXISTS  W_MSG_1 (ID VARCHAR(40), TEXT TEXT);
--TRUNCATE TABLE W_MSG_0;
--TRUNCATE TABLE W_MSG_1;

--消息映射用户 分表ID 用户会话a:b - 消息id - 时间
--DROP TABLE W_MSG_USER_0;
--DROP TABLE W_MSG_USER_1;
--DROP TABLE W_MSG_USER_2;
--DROP TABLE W_MSG_USER_3;
CREATE TABLE  IF NOT EXISTS  W_MSG_USER_0 (ID VARCHAR(200), USER_FROM VARCHAR(40), USER_TO VARCHAR(40), MSG_ID VARCHAR(40), TIME VARCHAR(20) );
CREATE TABLE  IF NOT EXISTS  W_MSG_USER_1 (ID VARCHAR(200), USER_FROM VARCHAR(40), USER_TO VARCHAR(40), MSG_ID VARCHAR(40), TIME VARCHAR(20) );
CREATE TABLE  IF NOT EXISTS  W_MSG_USER_2 (ID VARCHAR(200), USER_FROM VARCHAR(40), USER_TO VARCHAR(40), MSG_ID VARCHAR(40), TIME VARCHAR(20) );
CREATE TABLE  IF NOT EXISTS  W_MSG_USER_3 (ID VARCHAR(200), USER_FROM VARCHAR(40), USER_TO VARCHAR(40), MSG_ID VARCHAR(40), TIME VARCHAR(20) );
--TRUNCATE TABLE W_MSG_USER_0;
--TRUNCATE TABLE W_MSG_USER_1;
--TRUNCATE TABLE W_MSG_USER_2;
--TRUNCATE TABLE W_MSG_USER_3;


