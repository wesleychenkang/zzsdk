#!/bin/sh

###########################################
#
#  删除发布时多余的文件，如需要混淆的代码等
#
#  Author: nxliao(xtulnx@126.com)
#  Date:   2013-05-29
#
###########################################

lib=zzsdk-lib.jar

if [ ! -e ${lib} ]; then
 echo "\n\t必须先编译生成${lib}\n"; 
 exit -1;
fi

rm assets/* src/* -rf 

cp ${lib} libs/

git checkout src/com/zz/sdk/demo/MainActivity.java

ant clean

ant debug

cp bin/*-debug.apk .  # 生成demo应用

p="zzsdk-demo.`date +'%Y%m%d'`/"
l="AndroidManifest.xml libs src project.properties res assets .classpath .project"
t=zzsdk-demo.tar.gz

# 打包库
tar -czvf ${t} --transform='s,^,'"${p}"',' --show-transformed ${l} 

# resume
rm libs/${lib}

git checkout assets src 

