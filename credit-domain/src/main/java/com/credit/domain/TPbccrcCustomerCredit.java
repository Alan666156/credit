package com.credit.domain;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 央行征信信用报告规则判断结果
 * @author fuhongxing 
 */
public class TPbccrcCustomerCredit extends AbstractEntity {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private Long reportId; //报告id

    private Date createDate = new Date(); //创建时间

    private String creator;// 创建人

    private String result;//规则判断结果
   
    private String type;//是否符合无综合信用条件(0是/1否)

    private String status;//信用是否不良(0信用良好/1信用不良)
    
    private BigDecimal creditLimitMoney; //贷记卡负债总额度
    
    private BigDecimal alreadyUseMoney; //贷记卡负债已使用总额度
    
    private Integer threeMonthQueryCount;//近三个月查询次数
    
    private Integer oneMonthQueryCount;	//近一个月查询次数
    
    private String memo;//备注
    
    private Integer creditCard;//人民币贷记卡总和(包含正常、逾期、销户)
    
    private Integer zCreditCard;//人民币准贷记卡总和
    
    private Integer overdueCreditCard;//逾期人民币贷记卡总和(包含逾期、销户)
    
    private Integer overdueCreditCardMonth;//逾期人民币贷记卡月份数总和(包含逾期、销户)
    
    private Integer overdueCreditCardNinety;//90天逾期人民币贷记卡月份数总和(包含逾期、销户)
    
    private Integer overdueZcreditCard;//逾期人民币准贷记卡总和(包含逾期、销户)
    
    private Integer overdueZcreditCardMonth;//逾期人民币准贷记卡月份数总和(包含逾期、销户)
    
    private Integer overdueZcreditCardNinety;//90天逾期人民币准贷记卡月份数总和(包含逾期、销户)
    
    private Integer loanCount;//贷款总数(不包含担保贷款、包含当前已结清)
    
    private Integer overdueLoanCount;//逾期贷款总数(不包含担保、包含当前已结清)
    
    private Integer overdueLoanMonth;//贷款逾期月份数总和(不包含担保贷款、包含当前已结清)
    
    private Integer overdueLoanNinety;//90天以上逾期贷款账户月份数总和(不包含担保贷款、包含当前已结清)
    
	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Integer getThreeMonthQueryCount() {
		return threeMonthQueryCount;
	}

	public void setThreeMonthQueryCount(Integer threeMonthQueryCount) {
		this.threeMonthQueryCount = threeMonthQueryCount;
	}

	public Integer getOneMonthQueryCount() {
		return oneMonthQueryCount;
	}

	public void setOneMonthQueryCount(Integer oneMonthQueryCount) {
		this.oneMonthQueryCount = oneMonthQueryCount;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(Integer creditCard) {
		this.creditCard = creditCard;
	}

	public Integer getzCreditCard() {
		return zCreditCard;
	}

	public void setzCreditCard(Integer zCreditCard) {
		this.zCreditCard = zCreditCard;
	}

	public Integer getOverdueCreditCard() {
		return overdueCreditCard;
	}

	public void setOverdueCreditCard(Integer overdueCreditCard) {
		this.overdueCreditCard = overdueCreditCard;
	}

	public Integer getOverdueCreditCardMonth() {
		return overdueCreditCardMonth;
	}

	public void setOverdueCreditCardMonth(Integer overdueCreditCardMonth) {
		this.overdueCreditCardMonth = overdueCreditCardMonth;
	}

	public Integer getOverdueCreditCardNinety() {
		return overdueCreditCardNinety;
	}

	public void setOverdueCreditCardNinety(Integer overdueCreditCardNinety) {
		this.overdueCreditCardNinety = overdueCreditCardNinety;
	}

	public Integer getOverdueZcreditCard() {
		return overdueZcreditCard;
	}

	public void setOverdueZcreditCard(Integer overdueZcreditCard) {
		this.overdueZcreditCard = overdueZcreditCard;
	}

	public Integer getOverdueZcreditCardMonth() {
		return overdueZcreditCardMonth;
	}

	public void setOverdueZcreditCardMonth(Integer overdueZcreditCardMonth) {
		this.overdueZcreditCardMonth = overdueZcreditCardMonth;
	}

	public Integer getOverdueZcreditCardNinety() {
		return overdueZcreditCardNinety;
	}

	public void setOverdueZcreditCardNinety(Integer overdueZcreditCardNinety) {
		this.overdueZcreditCardNinety = overdueZcreditCardNinety;
	}

	public Integer getLoanCount() {
		return loanCount;
	}

	public void setLoanCount(Integer loanCount) {
		this.loanCount = loanCount;
	}

	public Integer getOverdueLoanCount() {
		return overdueLoanCount;
	}

	public void setOverdueLoanCount(Integer overdueLoanCount) {
		this.overdueLoanCount = overdueLoanCount;
	}

	public Integer getOverdueLoanMonth() {
		return overdueLoanMonth;
	}

	public void setOverdueLoanMonth(Integer overdueLoanMonth) {
		this.overdueLoanMonth = overdueLoanMonth;
	}

	public Integer getOverdueLoanNinety() {
		return overdueLoanNinety;
	}

	public void setOverdueLoanNinety(Integer overdueLoanNinety) {
		this.overdueLoanNinety = overdueLoanNinety;
	}
    
}