#!/bin/bash
###########################################
#配置目标服务器
#配置目标路径
#按需配置上传内容

#java web war包 定制化 解压war 管理lib 压缩上传！！！！！！！！！！！

#打包路径
root=`pwd -LP`  #/walker/walker-socket/release/
#项目路径
dir_proj=${root%/*}     #/walker/walker-web
#项目名
name_proj=${dir_proj##*/}  #walker-web
#[ 项目父级路径 ]
dir_proj_parent=${dir_proj%/*}  #/walker
#临时文件名
tofile_name="${name_proj}.tar.gz"
#临时文件全路径
fromfile_temp=~/temp/${tofile_name}
#目标服务器路径
server='walker@39.106.111.11'
todir="/home/walker/${name_proj}"

echo "打包路径 pwd ${root} "
echo "项目名 name_proj ${name_proj}"
echo "项目路径 dir_proj ${dir_proj}"
echo "临时文件全路径 fromfile_temp ${fromfile_temp}"
echo "目标服务器路径 todir ${server} ${todir}"


about="
Ctrl deploy all || just src jar.    \n
Usage: 
./deploy.sh [ all | default ] [other args]   \n
    \t  just    \t  WEB-INF without lib/*    \n
    \t  walker    \t  only upload walker-* jar    \n
    \t  conf \t  upload all except lib/*  \n
    \t  all   \t  upload all jar lib conf    \n
    \t  help    \t  show this   \n
"
#压缩 
#[ 备份服务端 ] 
#上传
#解压覆盖
#重启

unzip -o *.*ar	#解压war包
rm *.*ar	#删除war包

function just(){
	rm WEB-INF/lib -r
	tar -czvf ${fromfile_temp}  WEB-INF
	upAndTar
}
function walker(){
	tar -czvf${fromfile_temp} WEB-INF/lib/walker*
	upAndTar
}

function conf(){
	rm WEB-INF/lib -r
	tar -czvf ${fromfile_temp}  * 
	upAndTar
}

function all(){
	tar -czvf ${fromfile_temp}  * 
	upAndTar
}
function upAndTar(){
	ssh ${server}  " [ ! -d ${todir} ] && mkdir -p ${todir}"
	scp -p ${fromfile_temp}  ${server}:${todir}/${tofile_name}
	ssh ${server}  " cd ${todir} && tar -xvf ${tofile_name}  " 
	
	
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
        just
    fi
} 


#start
do_main $@





