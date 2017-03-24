package com.credit.service;

import java.util.List;

import com.credit.domain.ReportModelValue;
/**
 * 央行字段解读模板值读取
 */

public interface IReportModelValueService {
	/**
	 * 根据模板id查询模板值数据
	 * @param reportModelId
	 * @return
	 */
	List<ReportModelValue> selectByReportModelId(Long reportModelId);
}
