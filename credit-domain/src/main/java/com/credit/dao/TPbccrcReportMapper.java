package com.credit.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.credit.domain.TPbccrcReport;
@Mapper
public interface TPbccrcReportMapper {

  int deleteByPrimaryKey(Long id);

  int insert(TPbccrcReport record);

  int insertSelective(TPbccrcReport record);

  TPbccrcReport selectByPrimaryKey(Long id);

  int updateByPrimaryKeySelective(TPbccrcReport record);

  int updateByPrimaryKey(TPbccrcReport record);
  
  /**
   * 根据用户名和身份证获取征信报告
   * @param paramMap
   * @return
   */
  TPbccrcReport getReportByCustNameAndIdCard(Map<String, Object> paramMap);

  /** 查询报告信息 */
  public List<TPbccrcReport> findAll(Map<String, Object> paramMap);
  
}