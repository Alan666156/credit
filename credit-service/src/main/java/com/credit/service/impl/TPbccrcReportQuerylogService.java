package com.credit.service.impl;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.credit.dao.TPbccrcReportQuerylogMapper;
import com.credit.domain.TPbccrcReport;
import com.credit.domain.TPbccrcReportQuerylog;
import com.credit.service.ITPbccrcReportQuerylogService;
import com.credit.service.PbccrcService;
@Service
public class TPbccrcReportQuerylogService implements ITPbccrcReportQuerylogService{
	@Autowired
	private TPbccrcReportQuerylogMapper tPbccrcReportQuerylogMapper;
	@Autowired
	private PbccrcService pbccrcService;
	@Override
	public TPbccrcReportQuerylog findCustomerInfo(Map<String, Object> map) {
		return tPbccrcReportQuerylogMapper.getPbccrcReportQueryLogByCustomerIdCardAndCustomerName(map);
	}
	@Override
	public void savePbccrcReportCardAndLoan(TPbccrcReport report, String htmlContent, boolean flag) {
		pbccrcService.savePbccrcReportCardAndLoan(report, htmlContent, flag);
	}


}
