package com.credit.service;

import java.util.Map;

import com.credit.domain.TPbccrcReport;
import com.credit.domain.TPbccrcReportQuerylog;

public interface ITPbccrcReportQuerylogService {
	/**
	 * 查询用户信息
	 * @param map
	 * @return
	 */
	public TPbccrcReportQuerylog findCustomerInfo(Map<String, Object> map);
	public void savePbccrcReportCardAndLoan(TPbccrcReport report, String htmlContent, boolean flag);
}
