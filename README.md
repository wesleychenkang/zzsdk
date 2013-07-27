 ZZSDK 说明
==========

----
    Author: Jason
    Date: 2013-07-24

----


规则(rule)
---------

### 回调消息规则(msg-rule):

* android.os.Message

        .what = 消息类型,由游戏方传入
        .arg1 = 事件类型, 如登录\充值\社交...
        .arg2 = 事件状态, 如 成功\失败\取消\ ...
        .obj  = 事件数据, 

* onDestory
    1. 发送 EXIT_SDK 的通知;
    2. 清除本次操作的缓存


### JavaDoc

> -encoding utf-8 -charset utf-
	



### 日志规则(log-rule)

* BuildConfig.DEBUG
    * 类型: static final boolean
    * 来源: 这是由编译控制的, 如果是 debug 模式或发布成可调试的应用则此值为 true, 可在编译层控制代码生成.
    * 用法:

            if (BuildConfig.DEBUG) { Logger.d("I am Here!"); }

* Logger

    * 用途: 统一输出控制开关



### 版本规则(version-rule)

* 发布

    


* 动态更新



### 模块规则(module-rule)

基本定义：

    各模块如`登录`、`充值`、`社交`等，单独打包成 classes.dex，使用 DexClassLoader 来动态加载。
    
框架定义：
* 各模块入口统一接口，如 implements com.zz.sdk.IModule
* 初始化模块时传入 环境参数（host:Activity, env:HashMap, ...）
* 



----


模块(module)
-----------


### 登录(login)


模式:

* 横屏
* 竖屏


#### 普通登录(login-normal)



#### 奇虎登录(login-Qihoo360SDK)
	




### 支付(payment)


窗体:

> ChargeActivity

流程: 

1. .start:
    * 封装 Intent 的参数,并启动 ChargeActivity
2. .onCreate:
    * 读取参数
    * 创建进度框(dialog = DialogUtil.showProgress)等待 PayListTask 下载支付列表
    * 注册短信的广播接收(registerReceiver(smsSentReceiver, intentFilter);)
    * PayListTask 中上传 (serverId, imsi) 获取支付列表



### 社交
