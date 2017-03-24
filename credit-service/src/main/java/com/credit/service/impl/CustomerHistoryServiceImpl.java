package com.credit.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.credit.dao.TPbccrcCustomerHistoryMapper;
import com.credit.domain.TPbccrcCustomerHistory;
import com.credit.service.ICustomerHistoryService;
@Service
public class CustomerHistoryServiceImpl implements ICustomerHistoryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerHistoryServiceImpl.class);
//	@Autowired
//	CustomerHistoryMapper customerHistoryDao;
	@Autowired
	TPbccrcCustomerHistoryMapper tPbccrcCustomerHistoryMapper;
	
	
	/**
	 * 插入客户查询历史记录状态
	 */
	@Override
	public void saveCustomerHistory(TPbccrcCustomerHistory record){
		tPbccrcCustomerHistoryMapper.insert(record);
	}
	
	/**
	 * 根据客户姓名和身份证号查询最近的一条记录
	 */
	@Override
	public TPbccrcCustomerHistory getCustomerHistoryByCustomerNameAndCustomerIdCard(
			String customerName,String custormerIdCard,String operator) {
		Map<String,Object> paramMap = new HashMap<String, Object>();
		paramMap.put("customerName", customerName);
		paramMap.put("customerIdCard", custormerIdCard);
		paramMap.put("operator", operator);
		LOGGER.info("查询客户最近一条记录");
		return tPbccrcCustomerHistoryMapper.getCustomerHistoryByCustomerNameAndCustomerIdCard(paramMap);
	}

	@Override
	public TPbccrcCustomerHistory findByReportId(Long reportId) {
		return tPbccrcCustomerHistoryMapper.findByReportId(reportId);
	}
	
	@Override
	public List<TPbccrcCustomerHistory> findCustomerInfo(Map<String, Object> paramMap, int currentPage, int pageSize) {
		return tPbccrcCustomerHistoryMapper.findAll(paramMap, currentPage, pageSize);
	}
	
	

}
