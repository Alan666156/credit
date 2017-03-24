package com.credit.service;

import com.credit.domain.TPbccrcReport;

public interface IReportAnalyzeService {
	/**根据报告html生成报告主表信息*/
	public  TPbccrcReport getPbccrcContent(String htmlContent);
	/**拆分信用卡和贷款信息*/
	public  TPbccrcReport splitCardAndLoan(TPbccrcReport report ,String htmlContent);
}
