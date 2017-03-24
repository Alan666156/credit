package com.credit.dao;

import org.apache.ibatis.annotations.Mapper;

import com.credit.domain.TPbccrcCustomerCredit;
@Mapper
public interface TPbccrcCustomerCreditMapper {

	int insert(TPbccrcCustomerCredit record);
	
    int deleteByPrimaryKey(Long id);

    TPbccrcCustomerCredit selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TPbccrcCustomerCredit record);

    int updateByPrimaryKey(TPbccrcCustomerCredit record);
    /**
     * 根据报告id查询判断结果
     * @param id
     * @return
     */
    TPbccrcCustomerCredit findByReportId(Long id);
}