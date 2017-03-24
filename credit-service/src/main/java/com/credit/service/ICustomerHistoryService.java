package com.credit.service;


import java.util.List;
import java.util.Map;

import com.credit.domain.TPbccrcCustomerHistory;

public interface ICustomerHistoryService {
	
	
	public void saveCustomerHistory(TPbccrcCustomerHistory record);
	
	public TPbccrcCustomerHistory getCustomerHistoryByCustomerNameAndCustomerIdCard(String customerName,String custormerIdCard,String operator);
	
	public TPbccrcCustomerHistory findByReportId(Long reportId);
	
	public List<TPbccrcCustomerHistory> findCustomerInfo(Map<String,Object> paramMap, int currentPage, int pageSize);
}
