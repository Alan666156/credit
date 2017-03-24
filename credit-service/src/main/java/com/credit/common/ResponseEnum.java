package com.credit.common;


/**
 * 返回响应状态枚举
 * @author fuhongxing
 *
 */
public enum ResponseEnum {
	
	/** 格式校验枚举 **/
	VALIDATE_ISNULL("100001","{0} 数据项为空!"),
	VALIDATE_FORMAT("100002","{0} 数据项格式有误!"),
	FUNC_ID_NOT_EXISTS("200001","功能号：{0} 未定义!"),
	
	/** 系统枚举 **/
	SYS_SUCCESS("000000","正常"),
	SYS_FAILD("900000","系统忙"),
	SYS_EXIST("900000","已经存在，请重新输入"),
	SYS_ErrorActionCode("900001","没有操作权限"),
	SYS_SessionOutActionCode("900002","会话超时"),
	SYS_WARN("000001","可忽略异常"),
	
	/** 异常信息完全自定义 **/
	FULL_MSG("800000","{0}"),
	/** 征信报告历史记录查询 **/
	CUSTOMER_HISTORY_NOT_EXIST("C00001","无查询历史记录"),
	/**征信报告已过期**/
	PBCCRC_REPORT_EXPIRED("P00001","该客户的征信报告已过期"),
	PBCCRC_REPORT_NOT_EXIST("P00002","该客户的征信报告不存在"),
	PBCCRC_REPORT_DATA_ERROR("P00003","该客户的征信报告数据存在问题，请联系系统管理员"),
	PBCCRC_REPORT_ANALYSE_ERROR("P00004","该客户的征信报告解析存在问题，请联系系统管理员"),
	PBCCRC_REPORT_MESSAGE_ERROR("P00005","该客户的征信报告与所查询客户信息不符"),
	PBCCRC_REPORT_QUERYLOG_ERROR("P00006","未获取报告，请通过手机客户端获取"),
	PBCCRC_REPORT_QUERYLOG_EXIST("P00007","7天内不允许重复上传报告"),
	PBCCRC_REPORT_IDCARD_ERROR("P00008","身份证与姓名不匹配"),
	PBCCRC_REPORT_ERROR("P00009","报告上传异常"),
	/** 员工模块枚举 **/
	EMPLOYEE_PASSWORD_WRONG("000001","工号不存在或密码错误!"),
	EMPLOYEE_PASSWORD_NOT_EQ("000002", "两次密码输入不一致！"),
	EMPLOYEE_ILLEGAL_PWD("000003", "密码不符合规则，密码必须是6位-10位字母和数字的组合"),
	
	/** 员工Email为空*/
	EMAIL_NOT_EXIST("000001", "该用户邮箱未填写！请联系后线支持室更新用户信息，填写邮箱。"),
	
	/** 营业网点模块枚举 **/
	ORGANIZATION_OUT_LEVEL("000001","超出层级范围"),
	ORGANIZATION_CANNOT_DELETE("000002","存在子结点无法删除"),
	
	/**债权导出模块   **/
	LOANEXTERNALDEBT_FAIL("000001","选择的部分债权已被分配到其它批次，请重新选择！"),
	
	/** 文件上传枚举 **/
//	ERROR_TIME_RANGE("000001","生成报盘前后的15分钟内不允许进行管理营业部调整，请稍后再试"),
	FILE_EMPTY_FILE("F000001","导入的文件为空，拒绝导入！"),
	FILE_SIZE_OUT_RANGE("F000002","导入的文件过大，文件大小不能超过 {0}！"),
	FILE_ERROR_TYPE("F000003","导入的文件类型错误，拒绝导入！"),
	FILE_ERROR_PARSE("F000004","解析文件出错：{0}！"),
	FILE_ERROR_WRITE("F000005","写入文件出错：{0}！"),
	FILE_ERROR_FORMAT_CONTENT("F000006","文件内容必须从第{0}行开始！"),
	FILE_NO_FILE("F000007", "所需文件未生成或已失效！"),
	
	/**	 * 实时划扣信息	 */
	REALTIMEDEDUCTPRE_SUCCESS("000000","操作成功"),
	REALTIMEDEDUCTPRE_FAILD_ERROR("000001","输入用户名和身份证不匹配"),
	REALTIMEDEDUCTPRE_FAILD_OperateERROR("000002","操作失败"),
	
	/** 第三方HTTP接口 **/
	HTTP_RESPONSE_ERROR("H000001","第三方接口{0}异常：{1}"),
	
	CREATEOFFERINFO_AMOUNT_ZERO("C000001","债权报盘金额为0，报盘文件未创建"),
	
	FILE_REPORT_SUCCESS("0000","操作成功！"),
	FILE_REPORT_UPLOAD_ERROR("0001","上传文件失败！"),
	FILE_REPORT_SAVE_ERROR("0002","记录保存失败！"),
	FILE_REPORT_PARAM_ERROR("0003","参数错误！"),
	FILE_REPORT_QUERY_EMPTY("0004","未获取到该用户报表，请确认该用户是否有报表记录！"),
	FILE_REPORT_ERROR("0005","系统忙"),;
	
	private final String code;
	private final String desc;

	private ResponseEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
	
	
	
	
}
