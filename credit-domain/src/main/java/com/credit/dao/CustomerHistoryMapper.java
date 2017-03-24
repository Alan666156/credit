package com.credit.dao;

import java.util.List;
import java.util.Map;

import com.credit.domain.TPbccrcCustomerHistory;
/**
 * 用户上传历史dao
 * @author Alan Fu
 */
//@Mapper
public interface CustomerHistoryMapper {
	
	public List<TPbccrcCustomerHistory> getCustomerHistoryWithPg(Map<String,Object> paramMap);
	
	public TPbccrcCustomerHistory getCustomerHistoryByCustomerNameAndCustomerIdCard(Map<String, Object> paramMap);
	
	public void saveCustomerHistory(TPbccrcCustomerHistory record);
	
	public void updateByPrimaryKey(TPbccrcCustomerHistory record);
	
	public TPbccrcCustomerHistory findByReportId(Long reportId);
	
	public List<TPbccrcCustomerHistory> findAll(Map<String,Object> paramMap, int currentPage, int pageSize);
}
