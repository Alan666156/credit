package com.credit.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.credit.domain.TPbccrcCreditCardRecord;
/**
 * 信用卡DAO
 * @author fuhongxing
 */
@Mapper
public interface TPbccrcCreditCardRecordMapper {
    int deleteByPrimaryKey(Short id);

    int insert(TPbccrcCreditCardRecord record);

    int insertSelective(TPbccrcCreditCardRecord record);

    TPbccrcCreditCardRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TPbccrcCreditCardRecord record);

    int updateByPrimaryKey(TPbccrcCreditCardRecord record);
    /**
     * 根据行号查询
     * @param map
     * @return
     */
    List<TPbccrcCreditCardRecord> findByRecordNum(Map<String, Object> map);
    /**
     * 根据征信报告id查询
     * @param map
     * @return
     */
    List<TPbccrcCreditCardRecord> findByReportId(Map<String, Object> map);
    /**
     * 查询逾期信息(查询逾期中传入逾期金额)
     * @param map
     * @return
     */
    List<TPbccrcCreditCardRecord> findOverdueInfo(Map<String, Object> map);
    /**
     * 查询信用额度大于500的贷记卡信息
     * @param map
     * @return
     */
    List<TPbccrcCreditCardRecord> findCreditCardInfo(Map<String, Object> map);
    /**
     * 查询贷记卡或准贷记卡信息
     * @param map
     * @return
     */
    List<TPbccrcCreditCardRecord> findTPbccrcCreditCard(Map<String, Object> map);
    
    /**
     * 当前账户截止日期后有没有发放新的贷记卡
     * @param map
     * @return
     */
    List<TPbccrcCreditCardRecord> findNewCreditCard(Map<String, Object> map);
    /**
     * 贷记卡负债总额度统计
     * @param map
     * @return
     */
    BigDecimal creditCardDebtCount(Map<String, Object> map);
    /**
     * 贷记卡已使用额度统计
     * @param map
     * @return
     */
    BigDecimal creditCardAlreadyUseCount(Map<String, Object> map);
    
    /**未超过90天逾期月份数统计*/
    public Long overdueMonthCount(Map<String, Object> map);
    
    /**准贷记卡未超过90天逾期月份数统计*/
    public Long overdueMonthCountByZCard(Map<String, Object> map);
    /**准贷记卡超过90天逾期月份数统计*/
    public Long overdueMonthNumCountByZCard(Map<String, Object> map);
    
    /**超过90天逾期月份数统计*/
    public Long overdueMonthNumCount(Map<String, Object> map);
}