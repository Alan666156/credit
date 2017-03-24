package com.credit.service;

import java.util.List;

import com.credit.domain.ReportModel;
/**
 * 央行字段解读模板读取
 */

public interface IReportModelService {
	
	/**
	 * 根据类型查询模板数据
	 * @param paramMap
	 * @return
	 */
	List<ReportModel> selectByType(String type);
	
	
	List<ReportModel> selectAll();
}
