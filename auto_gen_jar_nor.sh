#!/bin/sh


#################################################
#
#  打包发布用的经混淆的库包（jar）
#
#    Author: nxliao(xtulnx@126.com)
#    Date:   2013-05-29
#    HIS:  2013-07-19 Normal
#################################################

s=classes
t=zzsdk-lib.jar
j=${s}_dex2jar.jar
test  -z "$D2J" && D2J=dex2jar.sh
test -z "$Z" && Z=zip

sed -i '/SUPPORT_360SDK = .*;/s/\(.*= \)\w*;/\1false;/' src/com/zz/sdk/ZZSDKConfig.java

ant clean
ant release


cd bin
${D2J} ${s}.dex
$Z -d ${j} "*" -x "com/zz/sdk*" -x "META-INF/*"
$Z -d ${j} "com/zz/sdk/demo/MainActivity.class"
cd ..
$Z -ur ./bin/${j} assets/zz_res/* assets/UPPayPluginEx.apk
cp ./bin/${j} ${t}

git checkout src/com/zz/sdk/ZZSDKConfig.java