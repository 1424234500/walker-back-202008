# walker
	一边工作	一边学习	一边编写
	
使用Maven多模块项目结构分离

# 模块划分
## walker-core
	核心模块 简单java项目
### common 通用工具 待修改划分
### core 核心组件
* annotation 自定义注解实现Test db

* cache 抽象缓存模块 并实现了cache Map的浏览数据结构 用于浏览器的缓存监控
  * Map 实现 并实现了url模式存取 eg: map1.list[2].map3.key1 = v1
  * Redis 实现 只支持了string-string string-string[]的这两种实现
  * Ehcache 实现
  
* database 数据库模块 原生jdbc工具类 List<Map> 形式
  * Pool 连接池接口
  * PoolC3p0Impl c3p0实现
  * RedisMgr redis连接池案例工具

* scheduler 定时器模块 负责定时器任务管理 使用quartz实现

* service 远程服务模块 
	* service 用于暴露的通用接口api
	* serviceImpl 实现类
	* webservice webservice的提供和调用案例
	* rmi java rmi实现的提供和调用案例
	* dubbo dubbo实现的提供和调用案例 使用了 zookeeper和redis注册中心 dubbo-admin-2.5.7.war监控中心


## walker-socket
	socket模块 简单java项目 使用原生socket和Netty框架实现即时通信
### socket 网络组件
* client 模拟客户端 
  * ClientUI java swing GUI图形化客户端模拟
  * ClientTest10NoUI 模拟多用户并发压测
  
* server_0 原生socket 旧Netty实现的服务端
* server_1 使用Netty重新构造的服务端
  * job 任务调度 常用于统计监控数据
  * netty.handler Netty处理器
  * plugin 业务处理插件 配置化
  * session 会话管理
  * Msg 统一传递消息类

## walker-web
### service 业务service处理

### web 网络服务servlet提供
* controller 控制器 
  * BaseController 基本抽象类 可继承快捷实现单表配置化增删查改
  * FileController 文件控制 提供文件的上传下载修改和共享文件夹的浏览
  * ClassController 类反射控制 提供package的浏览和实例化调用
  * TableController 抽象化实现表的增删查改 即把表名也参数化 结合ddl级sql语句 实现数据库的远程控制
  * Page 分页参数类
  * Context 结合ThreadLocal实现请求处理上下文
  
* dao 数据存取 分别以hibernate和mybatis实现了通用型List<Map>结构的数据查询和修改

* event 
  * intercept 拦截器 登录拦截 日志拦截 环绕监控
  * listener 系统启动监听OnLoad
  * task 定时任务调度器
* mode 基本数据模型

* service 服务层 封装业务逻辑块 


