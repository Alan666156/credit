package com.credit.dao;

import org.apache.ibatis.annotations.Mapper;

import com.credit.domain.TPbccrcReportContent;
@Mapper
public interface TPbccrcReportContentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TPbccrcReportContent record);

    int insertSelective(TPbccrcReportContent record);

    TPbccrcReportContent selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TPbccrcReportContent record);

    int updateByPrimaryKeyWithBLOBs(TPbccrcReportContent record);

    int updateByPrimaryKey(TPbccrcReportContent record);
    
    TPbccrcReportContent getReportContentByReportId(Long reportId);
}