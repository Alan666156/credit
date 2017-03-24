package com.credit.vo;


import java.io.Serializable;
import java.text.MessageFormat;

import com.credit.common.ResponseEnum;

/**
 * 
 * 响应数据封装
 * 
 * @author Ivan
 *
 */
public class ResponseInfo implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -4112845183658483869L;

    /** 响应状态码 **/
    private String resCode;
    /** 响应内容 **/
    private String resMsg;
    /** 流程编号 业务编号 **/
    private String flowId;
    /**报告ID*/
    private Long reportId;
    
    private Object data;
    
    
    public ResponseInfo() {

    }

    public ResponseInfo(ResponseEnum responseEnum,Object... arguments) {
		this.resCode = responseEnum.getCode();
		this.resMsg = MessageFormat.format(responseEnum.getDesc(), arguments);
    }

    public ResponseInfo(ResponseEnum responseEnum, String flowId) {
		this.resCode = responseEnum.getCode();
		this.resMsg = responseEnum.getDesc();
		this.flowId = flowId;
    }

    public ResponseInfo(String resCode, String resMsg) {
		this.resCode = resCode;
		this.resMsg = resMsg;
    }
    
    public ResponseInfo(String resCode, String resMsg,Object data) {
      this.resCode = resCode;
      this.resMsg = resMsg;
      this.data = data;
    }

    public ResponseInfo(String resCode, String resMsg, String flowId) {
		this.resCode = resCode;
		this.resMsg = resMsg;
		this.flowId = flowId;
    }
    
	public String getResCode() {
    	return resCode;
    }

    public void setResCode(String resCode) {
    	this.resCode = resCode;
    }

    public String getResMsg() {
    	return resMsg;
    }

    public void setResMsg(String resMsg) {
    	this.resMsg = resMsg;
    }

    public String getFlowId() {
    	return flowId;
    }

    public void setFlowId(String flowId) {
    	this.flowId = flowId;
    }

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }
 
	
}
