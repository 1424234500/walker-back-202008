


###mvn 指令依赖环境变量 需要和 配置的pom里面的相同  否则使用eclipse自带install也可
###打包
mvn clean package -Dmaven.test.skip=true

###部署 复制
/walker-socket/target/*
/walker-socket/target/lib/*
/walker-socket/target/bin/*
/walker-socket/target/conf/*

###启动



