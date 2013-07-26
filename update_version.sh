#!/bin/bash

############################
#
# 更新版本信息到源码中
#
############################



vC="`sed -n  '/android:versionCode=.*/s/.*versionCode="\([0-9]*\)".*/\1/p' AndroidManifest.xml`"
vN="`sed -n  '/android:versionName=.*/s/.*versionName="\([^"]*\)".*/\1/p' AndroidManifest.xml`"
vD="`date +\"%Y%m%d\"`"

echo vC=$vC
echo vN=$vN
echo vD=$vD



sed -i \
	-e '/VERSION_CODE.*=.*;/s/=\s*[0-9]*\([^0-9]\)/= '$vC'\1/g' \
	-e '/VERSION_NAME.*=.*;/s/=.*\".*\"\([^\"]\)/= \"'$vN'\"\1/g' \
	-e '/VERSION_DATE.*=.*;/s/=.*\".*\"\([^\"]\)/= \"'$vD'\"\1/g' \
 	src/com/zz/sdk/ZZSDKConfig.java
