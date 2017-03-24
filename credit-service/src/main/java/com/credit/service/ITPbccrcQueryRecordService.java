package com.credit.service;

import java.util.List;
import java.util.Map;

import com.credit.domain.TPbccrcQueryRecord;

public interface ITPbccrcQueryRecordService {
	/**
     * 获取机构或个人的查询记录
     * @param map
     * @return
     */
    List<TPbccrcQueryRecord> findByReportIdAndQueryType(Map<String, Object> map);
}
