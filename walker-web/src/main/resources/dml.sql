
--'script' must not be null or empty
select 1 from dual;

--初始化 配置数据 以包路径.类名.函数名.命名空间, 若公用则 交集取短命名
--造数 同步数据配置
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.service.impl.doBaseData', '造数间隔s', '1', '1970-01-01 00:00:00', '3600');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.service.impl.doAction', '地域同步间隔s', '1', '1970-01-01 00:00:00', '36000');
--造数配置
--delete from `walker`.`W_SYS_CONFIG`  where ID in ('com.walker.job.make.dura.millsec', 'com.walker.job.make.eachthread.sleep.millsec', 'com.walker.job.make.threadsize');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.job.make.dura.millsec', '模拟访问url持续时间', '1', '1970-01-01 00:00:00', '3000');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.job.make.eachthread.sleep.millsec', '模拟访问url线程间隔', '1', '1970-01-01 00:00:00', '1000');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.job.make.threadsize', '模拟访问url线程数', '1', '1970-01-01 00:00:00', '0');


--推送提醒
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.service.impl.PushServiceJpushImpl.secret', 'jpush secret', '1', '1970-01-01 00:00:00', 'a5dba9ffe772656db0e7149d');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.service.impl.PushServiceJpushImpl.appkey', 'jpush appkey', '1', '1970-01-01 00:00:00', '557bf3f8c230ec7cdefb0e06');


--限流配置 默认
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('ratelimit', '限流次数all', '1', '1970-01-01 00:00:00', '9999');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.intercept.RateLimitInterceptor.default.count', '限流次数每秒默认,url粒度', '1', '1970-01-01 00:00:00', '200');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.intercept.RateLimitInterceptor.default.warmupPeriod', '限流次数预热默认s', '1', '1970-01-01 00:00:00', '3');
--限流配置 指定url
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('/comm/getColsMap.do', '限流次数接口1 s', '1', '1970-01-01 00:00:00', '4');





--初始化默认业务数据
--初始化 默认用户 管理员 领导 普通用户

--初始化 默认角色 管理员 领导 普通用户

--初始化 默认部门 总控   总行 研发部


