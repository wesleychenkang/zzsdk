#!/bin/sh

###########################################
#
#  删除发布时多余的文件，如需要混淆的代码等
#
#  Author: nxliao(xtulnx@126.com)
#  Date:   2013-05-29
#
###########################################

rm assets/* src/* -rf 

cp zzsdk-lib.jar libs/

git checkout src/com/zz/sdk/activity/MainActivity.java

ant clean

ant debug

cp bin/*-debug.apk .

p="zzsdk-demo.`date +'%Y%m%H'`/"
l="AndroidManifest.xml libs src project.properties res assets .classpath .project"
t=zzsdk-demo.tar.gz

tar -czvf ${t} --transform='s,^,'"${p}"',' --show-transformed ${l} 

# resume
rm libs/zzsdk-lib.jar

git checkout assets src 

