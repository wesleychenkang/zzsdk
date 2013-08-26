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

    每次脚本编译发布时，根据 `AndroidManifest.xml` 中的 versionCode 和 versionName，以及编译日期 `date +"%Y%m%d"` 来更新 `src/com/zz/sdk/ZZSDKConfig.java` 中的 
    * VERSION_CODE
    * VERSION_NAME
    * VERSION_DATE


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
	


#### 逗趣登录(login-Douqu)

##### 第一次修改
2013年8月22日，开始从逗趣导入用户：即在 ZZSDK 客户端可以登录逗趣的用户。 

规则：



##### 第二次修改
2013年8月23日，公司的领导们决定换回我们在21号设计的方案：即由用户选择账户类型（逗趣账户或卓越账户）。

在登录界面，添加 RadioGroup 给用户选择登录类型：“老用户”和“卓越通行证”。

规则：  
 1. 用户名：
    * 登录用户名(Application.loginName)：卓越账户，用于支付、登录日志等；
    * 游戏用户名(Application.gameUserName)：游戏账户，与loginName关联
        * 在 `loginName` 是逗趣账户时， `gameUserName` 则是该账户的 `userId` ;
 2. 读取历史用户——
     1. zz数据库(3)
     2. cmge数据库(1)
     3. zz的SD卡(4)
     4. cmge的SD卡(2)
 3. 输入用户名，如 jsaon，如果用户选择“老用户”，则拼成 jason.cmge，在登录时用以判断类型并选择方式；
 4. 登录时，如果用户名是 *.cmge，则按　逗趣方式登录，否则按卓越方式登录；
    * 逗趣登录：用户名+密码
        * 登录成功后，获得用户名(account:c)和用户ID(userid:d)；
        * 使用 "${userid}.cmge" 构成新的用户名，加上密码，向卓越服务器注册，或者登录；
        * 成功后，将 "新用户名|密码|原用户名"，储存到SD卡及用户数据库中；
        * 返回用户ID及登录状态；
 5. 登录　逗趣　成功时，向卓越注册并记录在本地及SD卡中；
 6. 更新登录用户名和游戏用户名，并将游戏用户名返回给调用者（游戏方）；



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
