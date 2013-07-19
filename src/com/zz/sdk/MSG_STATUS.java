package com.zz.sdk;

/** 业务状态 */
public final class MSG_STATUS {
	private MSG_STATUS() {

	}

	/** 操作成功 */
	public final static int SUCCESS = 0;
	/** 操作失败 */
	public final static int FAILED = 1;
	/** 操作取消 */
	public final static int CANCEL = 2;
	/** 从 SDK 返回,即结束此次业务 */
	public final static int EXIT_SDK = 3;
}
