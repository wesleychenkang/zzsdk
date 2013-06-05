#!/bin/sh


#################################################
#
#  打包发布用的经混淆的库包（jar）
#
#    Author: nxliao(xtulnx@126.com)
#    Date:   2013-05-29
# 
#################################################

s=classes
t=zzsdk-lib.jar
j=${s}_dex2jar.jar
D2J=dex2jar.sh

ant clean
ant release
cd bin
${D2J} ${s}.dex
zip -d ${j} "*" -x "com/zz/sdk*" -x "META-INF/*"
zip -d ${j} "com/zz/sdk/activity/MainActivity.class"
cd ..
zip -u ./bin/${j} assets/* assets/zz_res/* 
cp ./bin/${j} ${t}
