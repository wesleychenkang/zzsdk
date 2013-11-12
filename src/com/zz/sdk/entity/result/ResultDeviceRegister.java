package com.zz.sdk.entity.result;

/**
 * DeviceRegister（注册） dreg.lg
 * <ul>输入
 * <li>projectId</li>
 * <li>deviceNum</li>
 * </ul>
 * <ul>输出
 * <li>0成功|1(或空)失败|2设备号已经存在</li></ul>
 */
public class ResultDeviceRegister extends BaseResult {

	/** 0成功|1失败 */
	protected final static String ErrMsg[] = {"成功", "失败", "设备号已经存在"};

	/**
	 * 返回出错描述
	 */
	public String getErrDesc() {
		return getErrDesc(ErrMsg, 0);
	}
}
