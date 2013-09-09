SDK开发文档
==========

----

导入相关库（library）
------------------

路径：***demo\libs***

- android-support-v4.jar
- commons-codec-1.6.jar
- UPPayAssistEx.jar（银联库）
- zzsdk-lib.jar (sdk库)


配置工程AndroidManifest.xml
-------------------------


1. 在工程 _AndroidManifest.xml_ 的 `<application>` 段内配置项目ID，格式如下：

        <meta-data android:name="PROJECT_ID"
                   android:value="P10009" /> 

    **<font color="red">项目ID</font>由SDK方提供**

2. 确保工程AndroidManifest.xml具有以下几项Permission权限：

        <!-- 权限列表 -->
        <uses-permission android:name="android.permission.INTERNET" />
        <uses-permission android:name="android.permission.READ_PHONE_STATE" />
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
        <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
        <uses-permission android:name="android.permission.RESTART_PACKAGES" />
        <!—— 需要短信支付才配置 --> 
        <uses-permission android:name="android.permission.SEND_SMS" />

3. 在工程AndroidManifest.xml的<application >段内配置以下内容:

        <activity
            android:name="com.zz.sdk.activity.LoginActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:launchMode="singleTask"
            android:screenOrientation="landscape"
            android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
            android:windowSoftInputMode="adjustPan" >
        </activity>
        <activity
            android:name="com.zz.sdk.activity.ChargeActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:excludeFromRecents="true"
            android:launchMode="singleTask"
            android:screenOrientation="landscape"
            android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen" >
        </activity>
        <activity android:name=".activity.PayOnlineActivity"
            android:exported="false"
            android:screenOrientation="landscape"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen">
        </activity>

    **注：**  
    android:screenOrientation 为横竖屏配置项，  
	+ `landscape`为横屏，
	+ `portrait`为竖屏，  
	
	也可以不设置，将根据游戏状态适应。
    
4. 在工程 _AndroidManifest.xml_ 的 `<manifest>` 段内，设置适应所有分辨率，需要在 `<manifest>` 段内配置以下内容：

        <supports-screens
    		android:smallScreens="true"
    		android:normalScreens="true"
    		android:largeScreens="true"
    		android:resizeable="true"
    		android:anyDensity="true" />



接口使用说明(Interface)
--------------------

### 相关类(Classes)

- 接口类：

    **com.zz.sdk.SDKManager**

    + getInstance(Context)
    + getVersionCode()
    + getVersionDesc()
    + recycle()
    + getLoginName()
    + isLogined()
    + setConfigInfo(boolean, boolean, boolean)
    + showLoginView(Handler, int)
    + showPaymentView(Handler, int, String, String, String, String, int, boolean, String)


- 类型类: 描述业务操作类型，如`登录`或`充值`等；
    
    **com.zz.sdk.MSG_TYPE**
    + LOGIN : 业务: 登录
    + PAYMENT : 业务: 支付

- 状态类：

    **com.zz.sdk.MSG_STATUS**
    
    + SUCCESS : 操作成功
    + FAILED : 操作失败
    + CANCEL : 操作取消
    + EXIT_SDK : 从 SDK 返回,即结束此次业务

- 登录数据类：

    **com.zz.sdk.LoginCallbackInfo.java**

- 充值数据类：

    **com.zz.sdk.PaymentCallbackInfo**



### 设置配置信息(Configure)

**<font color="red">游戏方必须在每次登录游戏前调用此函数。</font>**

定义：

	public void setConfigInfo(boolean isOnlineGame, boolean isDisplayLoginTip, boolean isDisplayLoginfail);

示例：

    // 获取实例
    mSDKManager = SDKManager.getInstance(context);
    // 设置配置信息
    mSDKManager.setConfigInfo(isOnlineGame, isDisplayLoginSuccessTip, isDisplayLoginFailTip);


说明：

+ isOnlineGame:  
    单机设置为false, 网游设置为true,凡没有登录注册界面的游戏统一划为单机
+ isDisplayLoginSuccessTip  
    是否显示登录成功toast,
+ isDisplayLoginFailTip  
    是否显示登录失败toast


### 登录接口(Login)

定义：

    com.zz.sdk.SDKManager.showLoginView(Handler callbackHandler, int what);
    
示例：

    mSDKManager = SDKManager.getInstance(this)
    mSDKManager.showLoginView(mHandler, MSG_LOGIN);

说明：

- callbackHandler: 登录结果回调处理的Handler。  
    其中，回调消息（ _android.os.Message_ ）说明：
    + .what: 消息值，由用户传入
    + .arg1: 此次业务类型，对于登录操作，此值固定为 `MSG_TYPE.LOGIN` 。
    + .arg2: 此次操作的结果，参考 `MSG_STATUS` ，见下表。
    + .obj: 登录信息，`LoginCallbackInfo`。 

        <table border="1" cellpadding="3" cellspacing="0" ><tr bgcolor="b5d6e6"  align="center"><td>.arg2（结果）</td><td>.obj（信息）</td><td>描述</td></tr>
<tr><td> MSG_STATUS.SUCCESS</td><td>LoginCallbackInfo</td><td>登录成功，可获取用户信息</td></tr>
<tr><td>MSG_STATUS.CANCEL</td><td>null</td><td>用户取消登录</td></tr>
<tr><td>MSG_STATUS.EXIT_SDK</td><td>null</td><td>登录结束，注：不论结果如何，游戏一定会收到此消息，用于表示SDK的登录界面退出。</td></tr>
        </table>

+ what: 消息值

### 充值接口(Payment)

定义：

    void com.zz.sdk.SDKManager.showPaymentView((Handler callbackHandler, int what,
			final String gameServerID, final String serverName,
			final String roleId, final String gameRole, final int amount,
			final boolean isCloseWindow, final String callBackInfo);
			
说明：

- callbackHandler: 支付结果通知　Handle  
    其中，回调消息（ _android.os.Message_ ）说明：
    + .arg1: 此次业务类型，对于登录操作，此值固定为 `MSG_TYPE.PAYMENT` 。
    + .arg2: 此次操作的结果，参考 `MSG_STATUS` ，见下表。
    + .obj: 登录信息，`PaymentCallbackInfo`

        <table border="1" cellpadding="3" cellspacing="0" ><tr bgcolor="b5d6e6"  align="center"><td>.arg2（结果）</td><td>.obj（信息）</td><td>描述</td></tr>
<tr><td>MSG_STATUS.SUCCESS</td><td>PaymentCallbackInfo</td><td>登录成功，可获取支付金额方式等</td></tr>
<tr><td>MSG_STATUS.FAILED</td><td>null</td><td>支付失败，无其它信息</td></tr>
<tr><td>MSG_STATUS.CANCEL</td><td>null</td><td>支付取消，无其它信息</td></tr>
<tr><td>MSG_STATUS.EXIT_SDK</td><td>null</td><td>此次业务结束，注：<font color="red">不论结果如何，游戏一定会收到此消息，用于表示SDK的登录界面退出。</font></td></tr>
        </table>
        
- what: 支付结果消息号
- gameServerID: 游戏服务器ID
- serverName: 游戏服务器名称
- roleId: 角色ID
- gameRole: 角色名称
- amount: 金额
    + 单位：分。如 300 表示 3元. 
    + 此处金额设计给固定价格的物品购买用，可以为 0，为 0 即为充值模式
- isCloseWindow: 是否充值完成后关闭平台	
    + true 是
    + false 否 （充值模式采取这种）
- callBackInfo: 厂家自定义参数	厂家自定义参数

### 其它接口(Other)

* 获取当前平台登录用户名

        // 返回当前登录用户，没有登录返回空
        String com.zz.sdk.SDKManager.getLoginName();
        
* 获取是否已经登录

        // 返回当前是否已经登录，未登录则返回 false
        boolean com.zz.sdk.SDKManager.isLogined();
        
* 释放 SDK 平台资源  
    在退出游戏时调用，用于清理 SDK 资源
    
        void com.zz.sdk.SDKManager.recycle();

### 充值结果同步(Synchro-Charge)

详见服务端同步文档。

## 运行环境

1. 目前该sdk只支持Android 2.1或以上版本的手机系统
2. 手机必须要有网络