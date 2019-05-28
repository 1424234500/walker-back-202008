
###打包
mvn clean package -Dmaven.test.skip=true

###部署 复制
/walker-socket/target/*
/walker-socket/target/lib/*
/walker-socket/target/conf/*

###启动
./start.sh
./stop.sh

###日志
./log.sh


