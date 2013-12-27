package com.zz.sdk.entity.result;

/**
 * VerifyCM(防沉迷)
 * <p/>
 * 0更改成功|1用户不存在|2密码错误|3之前验证过
 */
public class ResultAntiaddiction extends ResultLogin {

	public boolean isSuccess() {
		return super.isSuccess() || getCodeNumber() == 3;
	}

	// 0成功|1用户不存在|2密码错误
	private final static String errMsg[] = {"更改成功", "用户不存在", "密码错误", "之前验证过"};

	@Override
	public String getErrDesc() {
		return getErrDesc(errMsg, 0);
	}
}
