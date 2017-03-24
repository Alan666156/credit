package com.credit.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.credit.domain.ReportModel;

@Mapper
public interface ReportModelMapper {
	
    int deleteByPrimaryKey(Long id);

    int insert(ReportModel record);

    int insertSelective(ReportModel record);

    ReportModel selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ReportModel record);

    int updateByPrimaryKey(ReportModel record);
    
    List<ReportModel> selectByType(Map<String,Object> paramMap);
    
    List<ReportModel> selectAll();
}