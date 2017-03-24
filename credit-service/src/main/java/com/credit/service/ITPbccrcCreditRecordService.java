package com.credit.service;
import java.util.List;
import java.util.Map;

import com.credit.domain.TPbccrcCreditRecord;
import com.credit.util.Result;

public interface ITPbccrcCreditRecordService {
	/**
	 * 查询用户的征信报告信息
	 * @param map
	 * @return
	 */
	public List<TPbccrcCreditRecord> findCreditRecord(Map<String, Object> map);
	
	/**
	 * 规则判断
	 */
	public Result<String> checkRule(Map<String, Object> map);
	
	public String getCreditLimit(Map<String, Object> map);
	/**
	 * 获取贷款额度信息(征审贷款自动填充使用)
	 * @param map
	 * @return
	 */
	public String findLoanLimitInfo(Map<String, Object> map);
	/**
	 * APP获取信用判断结果
	 * @param map
	 * @return
	 */
	public String getCreditInfo(Map<String, Object> map);
}
