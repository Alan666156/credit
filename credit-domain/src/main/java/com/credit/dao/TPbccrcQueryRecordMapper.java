package com.credit.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.credit.domain.TPbccrcQueryRecord;
@Mapper
public interface TPbccrcQueryRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TPbccrcQueryRecord record);

    int insertSelective(TPbccrcQueryRecord record);

    TPbccrcQueryRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TPbccrcQueryRecord record);

    int updateByPrimaryKey(TPbccrcQueryRecord record);
    /**
     * 获取机构或个人的查询记录
     * @param map
     * @return
     */
    List<TPbccrcQueryRecord> findByReportIdAndQueryType(Map<String, Object> map);
    
    /**
     * 个人查询统计(同一天查询多次只算一次)
     * @param map
     * @return
     */
    Integer findQueryCountByType(Map<String, Object> map);
    /**
     * 机构查询统计
     * @param map
     * @return
     */
    Integer findQueryCount(Map<String, Object> map);
    
    
}