package com.credit.domain;

import java.util.Date;
/**
 * 报告上传历史记录
 * @author Alan Fu
 */
public class TPbccrcCustomerHistory extends AbstractEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private Long reportId;//征信报告id
   
    private Date queryDate;//查询时间
    
    private String customerIdCard;//客户身份证号
    
    private String customerName;//客户姓名
    
    private String operator;//操作人
    
    private String reportState;//查询报告状态


    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Date getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(Date queryDate) {
        this.queryDate = queryDate;
    }

    public String getCustomerIdCard() {
        return customerIdCard;
    }

    public void setCustomerIdCard(String customerIdCard) {
        this.customerIdCard = customerIdCard == null ? null : customerIdCard.trim();
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName == null ? null : customerName.trim();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public String getReportState() {
        return reportState;
    }

    public void setReportState(String reportState) {
        this.reportState = reportState == null ? null : reportState.trim();
    }
}