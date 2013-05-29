#!/bin/sh

s=classes
t=../zzsd-lib.jar

ant clean
ant release
cd bin
dex2jar.sh ${s}.dex
zip -d ${s}_dex2jar.jar "*" -x "com/zz/sdk*" -x "META-INF/*"
zip -d ${s}_dex2jar.jar "com/zz/sdk/activity/MainActivity.class"
cp ${s}_dex2jar.jar ${t}
cd -
