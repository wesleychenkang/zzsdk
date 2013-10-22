package com.zz.sdk.entity.result;

public class ResultRegister extends ResultLogin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1584296429216404270L;

	// 0成功|1失败|2用户名已经存在
	final String errMsg[] = { "成功", "失败", "用户名已经存在" };

	@Override
	public String getErrDesc() {
		return getErrDesc(errMsg, 0);
	}
}
