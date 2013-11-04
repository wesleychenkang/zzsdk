package com.zz.sdk.entity.result;

/**
 * ZyCoinPayRequest(卓越币支付请求） /pzy.lg
 * <ul>
 * 参数：
 * <li>loginName
 * <li>gameRole
 * <li>serverId
 * <li>projectId
 * <li>amount
 * <li>requestId
 * <li>productId
 * </ul>
 * <ul>
 * 输出:
 * <li>0成功|1失败|-2余额不足
 * </ul>
 * 
 * @author nxliao
 * 
 */
public class ResultRequestZZCoin extends ResultRequest {

	private static final long serialVersionUID = -4215573212577706831L;

	static final String ErrMsg[] = new String[] { "余额不足", null, "成功", "失败" };

	@Override
	public String getErrDesc() {
		return getErrDesc(ErrMsg, -2);
	}
}
