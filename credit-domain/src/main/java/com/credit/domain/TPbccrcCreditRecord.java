package com.credit.domain;

/**
 * 报告记录信息
 * @author Alan Fu
 */
//@Entity
//@Table(name="TPbccrc_Credit_Card_Record")
public class TPbccrcCreditRecord extends AbstractEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**征信报告id*/
    private Long reportId;
    /**类型*/
    private String type;
    /**内容*/
    private String content;
    /**序号*/
    private Long recordNum;
    /**征信报告id*/
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }
    /**类型*/
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }
    /**报告内容*/
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
    
	public Long getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(Long recordNum) {
		this.recordNum = recordNum;
	}

	/**
	 * 币种
	 * @author fuhongxing 
	 */
	public enum CurrencyType {
		人民币, 美元, 欧元, 日元;
		
	}
	
	/**
	 * 贷款种类
	 * @author fuhongxing 
	 */
	public enum CreditType{
		/**贷记卡*/
		贷记卡, 
		
		/**准贷记卡*/
		准贷记卡,
		
		/** 农户贷款 */
		农户贷款, 
		
		/** 个人住房贷款 */
		个人住房贷款, 
		
		/** 个人汽车贷款 */
		个人汽车贷款, 
		
		/** 个人经营性贷款 */
		个人经营性贷款,
		
		/** 其他贷款 */
		其他贷款;
	}
	
}