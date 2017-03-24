package com.credit.domain;
/**
 * 报告内容
 * @author Alan Fu
 */
public class TPbccrcReportContent extends AbstractEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long reportId;//征信报告id

    private String content;//前台传入的征信报告html内容
    
    private String name;//用户姓名
    
    private String idCard;//身份证
    
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
    
}