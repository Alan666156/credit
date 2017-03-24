package com.credit.service.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.credit.dao.ReportModelMapper;
import com.credit.domain.ReportModel;
import com.credit.service.IReportModelService;

@Service
public class ReportModelServiceImpl implements IReportModelService {
	
	@Autowired 
	private ReportModelMapper  reportModelMapper;
	
	@Override
	public List<ReportModel> selectByType(String type) {
		Map<String,Object> paramMap = new HashMap<String, Object>();
		paramMap.put("type", type);
		return reportModelMapper.selectByType(paramMap);
	}

	@Override
	public List<ReportModel> selectAll() {
		return reportModelMapper.selectAll();
	}

}
