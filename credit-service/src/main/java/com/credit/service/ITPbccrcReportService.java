package com.credit.service;

import java.util.List;
import java.util.Map;

import com.credit.domain.TPbccrcReport;

public interface ITPbccrcReportService {
	public List<TPbccrcReport> findReportInfo(Map<String, Object> paramMap, int currentPage, int pageSize);
}
