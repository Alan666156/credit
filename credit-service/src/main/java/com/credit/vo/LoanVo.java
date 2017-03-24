package com.credit.vo;


import java.math.BigDecimal;
/**
 * 贷款额度信息(征审贷款自动填充接口使用)
 * @author fuhongxing
 */
public class LoanVo {
	
	private Integer repayPeriods;//期数
	private Integer alreadyRepayPeriods;//已还期数
	private BigDecimal grantMoney;//额度
	private BigDecimal debtMoney; //负债
	private Long no;	//序号

	
	public Integer getRepayPeriods() {
		return repayPeriods;
	}
	public void setRepayPeriods(Integer repayPeriods) {
		this.repayPeriods = repayPeriods;
	}
	public Integer getAlreadyRepayPeriods() {
		return alreadyRepayPeriods;
	}
	public void setAlreadyRepayPeriods(Integer alreadyRepayPeriods) {
		this.alreadyRepayPeriods = alreadyRepayPeriods;
	}
	public BigDecimal getGrantMoney() {
		return grantMoney;
	}
	public void setGrantMoney(BigDecimal grantMoney) {
		this.grantMoney = grantMoney;
	}
	public BigDecimal getDebtMoney() {
		return debtMoney;
	}
	public void setDebtMoney(BigDecimal debtMoney) {
		this.debtMoney = debtMoney;
	}
	public Long getNo() {
		return no;
	}
	public void setNo(Long no) {
		this.no = no;
	}
	
}
