#!/bin/bash
###########################################
#do
#一些常用简单功能脚本
#配置启动脚本命令cmd
#配置日志文件路径logfile
#配置pid grep参数greparg
#配置说明about

#部署路径
dir_proj=`pwd -LP`     #/walker/walker-socket
#项目名
name_proj=${dir_proj##*/}  #walker-socket
echo "部署路径 dir_proj ${dir_proj}"
echo "项目名 name_proj ${name_proj}"

##-----------------------------------------
jarf="${name_proj}-0.0.1.jar"
echo "jar file $jarf"
cmd="java -jar ${jarf}"
logfile='/home/walker/logs/socket.log'
#shutdown the process by the grep pids by the cmd name  Warning ! the space
greparg=${jarf}
about="
Ctrl the server start/stop/log/pid/help.    \n
Usage: 
./server.sh [ start | stop | restart | log | pid | help ] [other args]   \n
    \t  start   \t  start server with log/system.log    \n
    \t  stop    \t  stop server kill pid    \n
    \t  restart \t  stop & start    \n
    \t  log \t  tailf log/*.log \n
    \t  pid \t  ps -elf | grep server   \n
    \t  help    \t  show this   \n
"


var=${logfile%/*} 
[ ! -d ${var} ] && mkdir -p ${var}

taillog='tail -n 200 -f '"$logfile"
#如何将变量中的值取出来作为绝对字符串'' 所以暂用直接获取pids
pids="ps -ef | grep "$greparg" | grep -v grep | cut -c 9-15"
#通过ps管道删除接收
# ps -ef | grep $greparg | grep -v grep | cut -c 9-15 | xargs kill -9
  
##------------------------------------------
function start(){
    ids=`eval $pids`
    if [[ "$ids" != "" ]]
    then
        pid
    else
        tcmd="nohup $cmd  &"	# > $logfile 启动日志不存储 交由log4j自动存入文件
        line
        echo $tcmd
        eval $tcmd
        pid
        log
    fi
}
function stop(){    
    ids=`eval $pids`
    tcmd="kill -9 $ids"
    line
    echo $tcmd
    eval $tcmd
    pid
}
function restart(){
    ids=`eval $pids`
    if [[ "$ids" != "" ]]
    then
        stop
    else
        pid
    fi
    start
}

function log(){
    line
    echo $taillog
    eval $taillog
}
function pid(){
    # echo $pids
    ids=`eval $pids`
    if [[ "$ids" != "" ]]
    then
        echo 'Have been started, Pids:'
        echo $ids
    else
        echo 'Stoped ! '
    fi
}
function help(){
    line
    echo -e ${about}
    line
}
function line(){
    echo "---------------------------------"
}

function do_main(){
    echo
    ##########################do something yourself
    do_init $@
    echo
}

function do_init(){
    method=$1
    if [[ "$method" != "" ]]
    then
        rootParams=($@)
        params=(${rootParams[@]:1})
        $method ${params[@]}
    else
        help 
    fi
} 


#start
do_main $@




