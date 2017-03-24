package com.credit.service.impl;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.credit.dao.TPbccrcReportMapper;
import com.credit.domain.TPbccrcReport;
import com.credit.service.ITPbccrcReportService;
import com.github.pagehelper.PageHelper;
@Service
public class TPbccrcReportServiceImpl implements ITPbccrcReportService {
	@Autowired
	private TPbccrcReportMapper tPbccrcReportMapper;
	
	@Override
	public List<TPbccrcReport> findReportInfo(Map<String, Object> paramMap, int currentPage, int pageSize) {
		PageHelper.startPage(currentPage, pageSize);
		return tPbccrcReportMapper.findAll(paramMap);
	}

}
