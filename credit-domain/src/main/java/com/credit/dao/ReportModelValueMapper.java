package com.credit.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.credit.domain.ReportModelValue;
@Mapper
public interface ReportModelValueMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ReportModelValue record);

    int insertSelective(ReportModelValue record);

    ReportModelValue selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ReportModelValue record);

    int updateByPrimaryKey(ReportModelValue record);
    
    List<ReportModelValue> selectByReportModelId(Long reportModelId);
}