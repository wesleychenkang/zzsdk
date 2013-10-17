#!/bin/bash


## 通过编译方式更新 assets 目录下的图片数据

################################################
#
#   Author: Jason(xtulnx@126.com)
#   Date:  2013年9月24日
#   
################################################


function usage() 
{
    cat <<EOF
帮助说明:

	$0 图片目录 [文件列表通配] 

  如：

	$0 compile assets/zz_res/drawable/
	$0 help
	$0 clean
	
EOF

    if [ -z "$be_verbose" ]; then
        exit 1
    fi

    cat <<EOF

通过编译方式更新 assets 目录下的图片数据。

不支持子目录遍历。

默认只处理  .9.png 格式图片

因 cygwin 的路径问题，这里必须指定 ANDROID_JAR，如: 

    export ANDROID_JAR=d:\\\\tools\\\\sdk-x86\\\\sdk\\\\platforms\\\\android-18\\\\android.jar

EOF

    exit 1
}

# 创建工程目录
function abrc_setup()
{
   if [ $# -ne 1 ]; then
        echo "Invalid arguments for setup"
        usage
    fi

    PROJECT_DIR=$1
    shift

    mkdir --parents $PROJECT_DIR/res/drawable
    mkdir --parents $PROJECT_DIR/res/drawable-mdpi
    mkdir --parents $PROJECT_DIR/res/drawable-hdpi
    mkdir --parents $PROJECT_DIR/res/raw
    mkdir --parents $PROJECT_DIR/res/layout
    
    if [ -e $PROJECT_DIR/AndroidManifest.xml ]; then
        if [ "$do_overwrite" != "1" ]; then
            read -p "Overwrite $PROJECT_DIR/AndroidManifest.xml [y/n]?"
            if [ "$REPLY" != "y" ]; then
                echo "$PROJECT_DIR/AndroidManifest.xml was left unchanged"
                exit 0
            fi
        fi
    fi
        
        cat >$PROJECT_DIR/AndroidManifest.xml <<EOF
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
      package="org.abrc.scaffolding"
      android:versionCode="1"
      android:versionName="1.0">
</manifest> 
EOF
 
}

# 编译 依赖
#  AAPT  aapt路径
#  ANDROID_JAR android.jar 路径
#  PROJECT_DIR 工程目录
#  RESOURCE_ZIP 临时资源包
function zz_compile()
{
   $AAPT package -f -M $PROJECT_DIR/AndroidManifest.xml -F $RESOURCE_ZIP -S $PROJECT_DIR/res -I $ANDROID_JAR

    # Exit on failure
    if [ $? -ne 0 ]; then exit 1; fi
}

function zz_find_aapt()
{
    # Figure out where the Android SDK is located.  Looking for 'adb' in $PATH to find out.
    ANDROID_SDK=`which adb 2>&1`
    if [ $? -ne 0 ]; then 
        echo "Unable to determine location of Android SDK.  Is adb in your PATH?"
        exit 1
    fi
    ANDROID_SDK=`dirname $ANDROID_SDK`
    ANDROID_SDK=`dirname $ANDROID_SDK`

    AAPT=`which aapt`
    if [ ! -e "$AAPT" ]; then
        # 查找 aapt 
        AAPT=$PLATFORM_SDK/tools/aapt
        if [ ! -e "$AAPT" ]; then
            echo "检查 build-tools 下的 aapt"
            AAPT=$ANDROID_SDK/build-tools/`ls -1 $ANDROID_SDK/build-tools/ | sort -r | head -n 1`/aapt
            if [ ! -e "$AAPT" ]; then
                echo "搜索 aapt" 
                AAPT=`find  $ANDROID_SDK/build-tools -name "aapt*" | head -1`
                if [ -z "$AAPT" ]; then
                    echo "Can not find aapt"
                    exit 1
                fi
            fi
        fi
    fi
}

function zz_find_androidJar()
{
	if [ ! -z "$ANDROID_SDK" ]; then
		PLATFORM_SDK=$ANDROID_SDK/platforms/`ls -1 $ANDROID_SDK/platforms | sort -n -r -t - -k 2| head -n 1`
		if [ "${PLATFORM_SDK:0:9}" = "/cygdrive" -o ! -e "${PLATFORM_SDK}/android.jar" ]; then
			echo "bad path: $PLATFORM_SDK";
		else 
			ANDROID_JAR=${PLATFORM_SDK}/android.jar
			echo "找到 $ANDROID_JAR"
		fi
	fi
		


    if [ -z "$ANDROID_JAR" ]; then
        echo "必须指定 ANDROID_JAR "
        be_verbose=1
        usage
    fi
}

function zz_check_compile()
{
    # 查找 aapt 设置 AAPT
    zz_find_aapt

    # 查找 android.jar 的位置
    zz_find_androidJar

    if [ ! -e $p ]; then
        abrc_setup $p
    fi

    f=$2
    [ -z "$f" ] && f=*.9.png

	if [ -z "`which mktemp`" ]; then
		RESOURCE_ZIP=$work/resources-XXXXXXXXXX.zip
	else
		RESOURCE_ZIP=`mktemp -u $work/resources-XXXXXXXXXX`.zip
	fi
    
    [ -z "$RES_PREFIX" ] && RES_PREFIX=res/drawable

    rm $p/${RES_PREFIX}/* -f
    cp `pwd`/$1/$f $p/${RES_PREFIX} 

    [ -z "$PROJECT_DIR" ] && PROJECT_DIR=$p
    zz_compile 

    # Unzip the named resource files.
    if [ "$do_overwrite" = "1" ]; then
        OFLAG=-o
    else
        unset OFLAG
    fi
    
    OUTPUT_DIR=$work/out
    unzip $OFLAG -d $OUTPUT_DIR $RESOURCE_ZIP ${RES_PREFIX}/$f
    rm -rf $RESOURCE_ZIP

    cp $OUTPUT_DIR/$RES_PREFIX/* `pwd`/$1/
}

function main()
{
    if [ $# -lt 1 ]; then
        usage
    fi

    # Parse options.
    be_verbose=
    # Switch on the named operation
    operation=$1
    shift

    # 脚本目录
    pw=`dirname $0`

    # 临时工程目录
    work=$pw/.tmp
    p=$work/update_assets.proj

    if [ "$operation" = "compile" ]; then
        zz_check_compile $*
    elif [ "$operation" = "setup" ]; then
        abrc_setup $p
    elif [ "$operation" = "clean" ]; then
        rm $work -rf
    elif [ "$operation" = "usage" ]; then
        usage
    elif [ "$operation" = "help" ]; then
        be_verbose=1
        usage
    else
        echo "Unknown operation: $operation"
        usage
    fi
}

main $*