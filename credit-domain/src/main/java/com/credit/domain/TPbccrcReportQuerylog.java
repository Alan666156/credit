package com.credit.domain;


/**
 * 
 * @author Alan Fu
 */
public class TPbccrcReportQuerylog extends AbstractEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String customerName;//客户姓名

    private String customerIdCard;//客户身份证号

    private Long reportId;//人行征信报告id

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName == null ? null : customerName.trim();
    }

    public String getCustomerIdCard() {
        return customerIdCard;
    }

    public void setCustomerIdCard(String customerIdCard) {
        this.customerIdCard = customerIdCard == null ? null : customerIdCard.trim();
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }
}