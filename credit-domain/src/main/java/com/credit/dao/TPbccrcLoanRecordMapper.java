package com.credit.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.credit.domain.TPbccrcLoanRecord;
@Mapper
public interface TPbccrcLoanRecordMapper {
	public int deleteByPrimaryKey(Short id);

    public int insert(TPbccrcLoanRecord record);

    public int insertSelective(TPbccrcLoanRecord record);

    public TPbccrcLoanRecord selectByPrimaryKey(Short id);

    public int updateByPrimaryKeySelective(TPbccrcLoanRecord record);

    public int updateByPrimaryKey(TPbccrcLoanRecord record);
    /**
     * 根据报告id查询
     * @param map
     * @return
     */
    List<TPbccrcLoanRecord> findByReportId(Map<String, Object> map);
    
    List<TPbccrcLoanRecord> findcreditLoanInfo(Map<String, Object> map);
    /**
     * 查询非农贷款未结清/结清时间小于半年，不是呆账信息
     * @param map
     * @return
     */
    List<TPbccrcLoanRecord> findcreditLoan(Map<String, Object> map);
    /**
     * 根据行号查询
     * @param map
     * @return
     */
    public List<TPbccrcLoanRecord> findByRecordNum(Map<String, Object> map);
    
    /**
     * 当前账户截止日期后有没有发放新的非农贷款
     * @param map
     * @return
     */
    public List<TPbccrcLoanRecord> findcreditNewLoan(Map<String, Object> map);
    /**
     * 当前账户截止日期后有没有发放新的贷款
     * @param map
     * @return
     */
    public List<TPbccrcLoanRecord> findcreditNewLoanInfo(Map<String, Object> map);
    /**查询贷款额度信息*/
    public List<TPbccrcLoanRecord> findLoanLimitInfo(Map<String, Object> map);
    
    /**未超过90天逾期月份数统计*/
    public Long overdueMonthCount(Map<String, Object> map);
    
    /**超过90天逾期月份数统计*/
    public Long overdueMonthNumCount(Map<String, Object> map);
    
    
}