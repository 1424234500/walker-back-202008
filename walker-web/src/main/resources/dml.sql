
--'script' must not be null or empty
select 1 from dual;

--初始化 配置数据 以包路径.类名.函数名.命名空间, 若公用则 交集取短命名
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.service.impl.doBaseData', '造数间隔s', '1', '1970-01-01 00:00:00', '3600');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.service.impl.doAction', '地域同步间隔s', '1', '1970-01-01 00:00:00', '36000');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.service.impl.PushServiceJpushImpl.secret', 'jpush secret', '1', '1970-01-01 00:00:00', 'a5dba9ffe772656db0e7149d');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.service.impl.PushServiceJpushImpl.appkey', 'jpush appkey', '1', '1970-01-01 00:00:00', '557bf3f8c230ec7cdefb0e06');

INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.job.make.dura.millsec', '模拟访问url持续时间', '1', '1970-01-01 00:00:00', '120000');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.job.make.eachthread.sleep.millsec', '模拟访问url线程间隔', '1', '1970-01-01 00:00:00', '100');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.job.make.threadsize', '模拟访问url线程数', '1', '1970-01-01 00:00:00', '2');



INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.intercept.RateLimitInterceptor.default.count', '限流次数每秒默认,url粒度', '1', '1970-01-01 00:00:00', '2');
INSERT INTO `walker`.`W_SYS_CONFIG` (`ID`, `ABOUT`, `S_FLAG`, `S_MTIME`, `VALUE`) VALUES ('com.walker.intercept.RateLimitInterceptor.default.warmupPeriod', '限流次数预热默认s', '1', '1970-01-01 00:00:00', '3');





