package com.credit.domain;
/**
 * 报告查询记录
 * @author Alan Fu
 */
public class TPbccrcQueryRecord extends AbstractEntity{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long reportId;//征信报告id

    private String queryDate;//查询日期

    private String type;//类型(1为机构查询,2为个人查询)

    private String queryNo;//编号

    private String operator;//查询操作员

    private String queryReason;//查询原因

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate == null ? null : queryDate.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getQueryNo() {
        return queryNo;
    }

    public void setQueryNo(String queryNo) {
        this.queryNo = queryNo == null ? null : queryNo.trim();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public String getQueryReason() {
        return queryReason;
    }

    public void setQueryReason(String queryReason) {
        this.queryReason = queryReason == null ? null : queryReason.trim();
    }
}