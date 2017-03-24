package com.credit.service;

import com.credit.domain.TPbccrcReport;
import com.credit.domain.TPbccrcReportContent;
import com.credit.domain.TPbccrcReportQuerylog;

public interface PbccrcService {
	
  public void savePbccrcReport(TPbccrcReport report, String htmlContent);
	
	public TPbccrcReport getPbccrcReport(Long reportId);
	
	public TPbccrcReport getPbccrcReport(String customerName, String customerIdCard);
	
	public TPbccrcReportContent getPbccrcReportContent(Long reportId);
	
	public int updateReportQueryLogById(Long id,Long reportId);
	
	public TPbccrcReportQuerylog getPbccrcReportQueryLogByCustomerIdCardAndCustomerName(String customerName, String customerIdCard,Long reportId);
	
	public void savePbccrcReportCardAndLoan(TPbccrcReport report, String htmlContent, boolean flag);
	/**
	 * 异步保存字段解读信用卡信息和贷款信息
	 * @param report
	 * @param htmlContent
	 */
	public void syncSavePbccrcReportCardAndLoan(TPbccrcReport report,String htmlContent);
	
}
