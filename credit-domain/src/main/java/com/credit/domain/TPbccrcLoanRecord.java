package com.credit.domain;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 贷款记录
 * @author Alan Fu
 */
public class TPbccrcLoanRecord extends AbstractEntity{
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
    /**放款金额*/
    private BigDecimal grantMoney;
    /**币种*/
    private String currencyType;
    /**贷款种类*/
    private String creditType;
    /**截止时间(年月)*/
    private Date deadlineDate;
    /**到期时间*/
    private Date expireDate;
    /**结清时间*/
    private Date settlementDate;
    /**逾期额度*/
    private BigDecimal overdueMoney;
    /**最近5年内有几个月处于逾期状态*/
    private Long overdueMonth=0L;
    /**最近5年内有多少个月逾期超过90天*/
    private Long overdueMonthNum=0L;
    /**是否结清*/
    private String settlement;
    /**是否呆账*/
    private String bad;
    /**解析类型*/
    private String type;
    /**序号*/
    private Long recordNum;
    /**余额*/
    private BigDecimal balance;
    /**贷款类型(个人住房贷款、其他贷款)*/
    private String loanType;
    /**发放机构类型(001：银行，002：信用合作社/信用合作联社/信用合作社联合社/信用社联合社，003：网商银行，004：公司机构)*/
    private String orgType;
    /**内容*/
    private String content;

	public Long getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(Long recordNum) {
		this.recordNum = recordNum;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBad() {
		return bad;
	}

	public void setBad(String bad) {
		this.bad = bad;
	}

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

	public BigDecimal getGrantMoney() {
		return grantMoney;
	}

	public void setGrantMoney(BigDecimal grantMoney) {
		this.grantMoney = grantMoney;
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

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public BigDecimal getOverdueMoney() {
		return overdueMoney;
	}

	public void setOverdueMoney(BigDecimal overdueMoney) {
		this.overdueMoney = overdueMoney;
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

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getSettlement() {
		return settlement;
	}

	public void setSettlement(String settlement) {
		this.settlement = settlement;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
    
	
}