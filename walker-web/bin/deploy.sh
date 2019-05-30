#!/bin/bash
###########################################


#!/bin/bash
###########################################
#do
#一些常用简单功能脚本
#配置启动脚本命令cmd
#配置日志文件路径logfile
#./do start <help><stop><show><log><restart>
about="
Ctrl deploy all || just src jar.    \n
Usage: 
./deploy.sh [ all | default ] [other args]   \n
    \t  all   \t  upload all jar lib conf    \n
    \t  default \t  upload all jar except lib   \n
    \t  just    \t  default only upload src jar    \n
    \t  help    \t  show this   \n
"
#压缩 
#[ 备份服务端 ] 
#上传
#解压覆盖
#重启
server='walker@39.106.111.11'
root=`pwd -LP`
tofile_name=release.tar.gz
todir='/home/walker/socket'

fromfile_temp=~/temp/${tofile_name}

function just(){
	tar -czvf ${fromfile_temp}  *.jar
	upAndTar
}
function all(){
	tar -czvf ${fromfile_temp}  * 
	upAndTar
}
function default(){
	rm lib -r
	tar -czvf ${fromfile_temp}  * 
	upAndTar
}
function upAndTar(){
	ssh ${server} " [ ! -d ${todir} ] && mkdir -p ${todir}"
	scp -p ${fromfile_temp}  ${server}:${todir}/${tofile_name}
	ssh ${server} "mkdir ${todir} ;  cd ${todir} && tar -xvf ${tofile_name} && ./server.sh restart "        
	
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
        default
    fi
} 


#start
do_main $@




