package com.credit.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.credit.domain.TPbccrcCustomerHistory;
@Mapper
public interface TPbccrcCustomerHistoryMapper {
    int deleteByPrimaryKey(Long id);
    
    int insert(TPbccrcCustomerHistory record);

    int insertSelective(TPbccrcCustomerHistory record);

    TPbccrcCustomerHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TPbccrcCustomerHistory record);

    int updateByPrimaryKey(TPbccrcCustomerHistory record);
    
    TPbccrcCustomerHistory getCustomerHistoryByCustomerNameAndCustomerIdCard(Map<String, Object> paramMap);
    
    TPbccrcCustomerHistory findByReportId(Long reportId);
    /**
     * 分页查询用户信息
     * @param paramMap
     * @param currentPage
     * @param pageSize
     * @return
     */
    public List<TPbccrcCustomerHistory> findAll(Map<String,Object> paramMap, int currentPage, int pageSize);
}