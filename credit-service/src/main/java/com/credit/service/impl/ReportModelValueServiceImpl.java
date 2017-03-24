package com.credit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.credit.dao.ReportModelValueMapper;
import com.credit.domain.ReportModelValue;
import com.credit.service.IReportModelValueService;

@Service
public class ReportModelValueServiceImpl implements IReportModelValueService {

	@Autowired ReportModelValueMapper 	reportModelValueMapper;
	
	@Override
	public List<ReportModelValue> selectByReportModelId(Long reportModelId) {
		return reportModelValueMapper.selectByReportModelId(reportModelId);
	}

}
