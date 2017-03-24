package com.credit.domain;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 信用卡信息
 * @author Alan Fu
 */
//@Entity
//@Table(name="TPbccrc_Credit_Card_Record")
public class TPbccrcCreditCardRecord extends AbstractEntity{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**征信报告id*/
    private Long reportId;
    /**放款时间*/
    private Date grantMoneyDate;
    /**放款单位*/
    private String grantOrganizations;
    /**币种*/
    private String currencyType;
    /**贷款种类(贷记卡、准贷记卡)*/
    private String creditType;
    /**截止时间(年月)*/
    private Date deadlineDate;
    /**信用额度*/
    private BigDecimal creditLimitMoney;
    /**已使用额度*/
    private BigDecimal alreadyUseMoney;
    /**逾期额度*/
    private BigDecimal overdueMoney;
    /**是否呆账*/
    private String bad;
    /**最近5年内有几个月处于逾期状态*/
    private Long overdueMonth=0L;
    /**最近5年内有多少个月逾期超过90天*/
    private Long overdueMonthNum=0L;
    /**准贷记卡透支超过60天的月数*/
    private Long creditCardSixty=0L;
    /**准贷记卡透支超过90天的月数*/
    private Long creditCardNinety=0L;
    /**是否激活*/
    private String active;
    /**是否销户*/
    private String closeAccount;
    /**透支余额*/
    private BigDecimal overdraftBalance;
    /**解析类型*/
    private String type;
    
    /**序号*/
    private Long recordNum;
    
    /**内容*/
    private String content;

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Date getGrantMoneyDate() {
		return grantMoneyDate;
	}

	public void setGrantMoneyDate(Date grantMoneyDate) {
		this.grantMoneyDate = grantMoneyDate;
	}

	public String getGrantOrganizations() {
		return grantOrganizations;
	}

	public void setGrantOrganizations(String grantOrganizations) {
		this.grantOrganizations = grantOrganizations;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public String getCreditType() {
		return creditType;
	}

	public void setCreditType(String creditType) {
		this.creditType = creditType;
	}

	public Date getDeadlineDate() {
		return deadlineDate;
	}

	public void setDeadlineDate(Date deadlineDate) {
		this.deadlineDate = deadlineDate;
	}

	public BigDecimal getCreditLimitMoney() {
		return creditLimitMoney;
	}

	public void setCreditLimitMoney(BigDecimal creditLimitMoney) {
		this.creditLimitMoney = creditLimitMoney;
	}

	public BigDecimal getAlreadyUseMoney() {
		return alreadyUseMoney;
	}

	public void setAlreadyUseMoney(BigDecimal alreadyUseMoney) {
		this.alreadyUseMoney = alreadyUseMoney;
	}

	public BigDecimal getOverdueMoney() {
		return overdueMoney;
	}

	public void setOverdueMoney(BigDecimal overdueMoney) {
		this.overdueMoney = overdueMoney;
	}

	public String getBad() {
		return bad;
	}

	public void setBad(String bad) {
		this.bad = bad;
	}

	public Long getOverdueMonth() {
		return overdueMonth;
	}

	public void setOverdueMonth(Long overdueMonth) {
		this.overdueMonth = overdueMonth;
	}

	public Long getOverdueMonthNum() {
		return overdueMonthNum;
	}

	public void setOverdueMonthNum(Long overdueMonthNum) {
		this.overdueMonthNum = overdueMonthNum;
	}

	public Long getCreditCardSixty() {
		return creditCardSixty;
	}

	public void setCreditCardSixty(Long creditCardSixty) {
		this.creditCardSixty = creditCardSixty;
	}

	public Long getCreditCardNinety() {
		return creditCardNinety;
	}

	public void setCreditCardNinety(Long creditCardNinety) {
		this.creditCardNinety = creditCardNinety;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getCloseAccount() {
		return closeAccount;
	}

	public void setCloseAccount(String closeAccount) {
		this.closeAccount = closeAccount;
	}

	public BigDecimal getOverdraftBalance() {
		return overdraftBalance;
	}

	public void setOverdraftBalance(BigDecimal overdraftBalance) {
		this.overdraftBalance = overdraftBalance;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(Long recordNum) {
		this.recordNum = recordNum;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}