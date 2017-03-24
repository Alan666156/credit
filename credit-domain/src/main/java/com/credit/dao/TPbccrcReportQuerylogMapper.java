package com.credit.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.credit.domain.TPbccrcReportQuerylog;

@Mapper
public interface TPbccrcReportQuerylogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TPbccrcReportQuerylog record);

    int insertSelective(TPbccrcReportQuerylog record);

    TPbccrcReportQuerylog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TPbccrcReportQuerylog record);

    int updateByPrimaryKey(TPbccrcReportQuerylog record);
    
    TPbccrcReportQuerylog getPbccrcReportQueryLogByCustomerIdCardAndCustomerName(Map<String,Object> paramMap);
    
    int updateReportQueryLogById(Map<String,Object> paramMap);
    /**
     * 根据报告id查询
     * @param reportId
     * @return
     */
    TPbccrcReportQuerylog findByReportId(Long reportId);
}