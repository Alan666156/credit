package com.credit.common;


import java.util.Arrays;
import java.util.List;

/**
 * 定义常量信息
 * 
 * @author Ivan
 *
 */
public class WebConstants {
	
	/**  机构查询记录	 */
	public static final String QUERY_RECORD_TYPE_ORG = "1";
	/**  个人查询记录	 */
	public static final String QUERY_RECORD_TYPE_SELF = "2";
	/**  发生过逾期的贷记卡账户明细记录	 */
	public static final String CREDIT_RECORD_TYPE_CREDIT_CARD_OVERDUE = "1";
	/**  从未逾期过的贷记卡及透支未超过60天的准贷记卡账户明细记录	 */
	public static final String CREDIT_RECORD_TYPE_CREDIT_CARD_NORMAL = "2";
	/**  发生过逾期的购房贷款账户明细记录	 */
	public static final String CREDIT_RECORD_TYPE_HOUSING_LOAN_OVERDUE = "11";
	/**  从未逾期过的购房贷款账户明细记录	 */
	public static final String CREDIT_RECORD_TYPE_HOUSING_LOAN_NORMAL = "12";
	/**  发生过逾期的其他贷款账户明细记录	 */
	public static final String CREDIT_RECORD_TYPE_OTHER_LOAN_OVERDUE = "21";
	/**  从未逾期过的其他贷款账户明细记录	 */
	public static final String CREDIT_RECORD_TYPE_OTHER_LOAN_NORMAL = "22";
	/**  为他人担保信息	 */
	public static final String CREDIT_RECORD_TYPE_GUARANTEE = "31";
	/**  资产处置信息	 */
	public static final String CREDIT_RECORD_TYPE_ASSET = "41";
	/**  保证人代偿信息	 */
	public static final String CREDIT_RECORD_TYPE_REPAYMENT = "42";
	/**逾期标识*/
	public static final String OVERDUE="0";
	
	/**未逾期标识*/
	public static final String NORMAL_OVERDUE="1";
	/**成功状态码*/
	public static final String SUCCESS_CODE="000000";
	/**华征失败*/
	public static final String HZ_FAIL_CODE="10";
	
	/**
	 * 结果
	 */
	public enum STATUS{
		SUCCESS, WARNING, FAIL
	}
		/**字段解读类型-信用卡*/
	public static final String REPORT_MODEL_CREDIT_CARD="1";
	
	/**字段解读类型-贷款*/
	public static final String REPORT_MODEL_LOAN="2";
	public static final List<String> CREDIT_TYPE = Arrays.asList("农户贷款", "个人住房贷款", "个人经营性贷款", "个人住房公积金贷款", "个人消费贷款" ,"个人汽车贷款","其他贷款");
	/**贷款类型*/
	public enum CREDIT{
		农户贷款, 个人住房贷款, 个人住房公积金贷款, 个人经营性贷款, 个人消费贷款, 个人汽车贷款, 个人助学贷款, 其他贷款, 购房贷款, 贷记卡, 准贷记卡
	}
	
	/**机构类型*/
	public enum ORGTYPE{
		银行("001", "银行"),	
		信用社("002", "信用社"),	
		网商银行("003", "网上银行"),
		公司机构("004", "公司机构"),
		汽车金融公司("005", "汽车金融");
		private ORGTYPE(String code, String value) {
			this.code = code;
			this.value = value;
		}
		private String value;
		/** code*/
		private String code;
		
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}	
	}
	
	/**人民币*/
	public static final String CURRENCY_TYPE= "人民币";
	
	public static final String HOUSE_LOAN= "个人商用房（包括商住两用）贷款";
	
	public enum BANK{
		浙江网商银行股份有限公司,深圳前海微众银行股份有限公司
	}
}
