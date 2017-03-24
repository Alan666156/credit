package com.credit.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.credit.domain.TPbccrcCreditRecord;

@Mapper
public interface TPbccrcCreditRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TPbccrcCreditRecord record);

    int insertSelective(TPbccrcCreditRecord record);

    TPbccrcCreditRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TPbccrcCreditRecord record);

    int updateByPrimaryKey(TPbccrcCreditRecord record);
    /**根据报告id查询用户的信贷记录*/
    List<TPbccrcCreditRecord> findByReportId(Map<String, Object> map);
}