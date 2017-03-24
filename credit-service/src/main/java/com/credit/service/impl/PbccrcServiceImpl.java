package com.credit.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.credit.common.WebConstants;
import com.credit.dao.TPbccrcCreditCardRecordMapper;
import com.credit.dao.TPbccrcCreditRecordMapper;
import com.credit.dao.TPbccrcCustomerHistoryMapper;
import com.credit.dao.TPbccrcLoanRecordMapper;
import com.credit.dao.TPbccrcQueryRecordMapper;
import com.credit.dao.TPbccrcReportContentMapper;
import com.credit.dao.TPbccrcReportMapper;
import com.credit.dao.TPbccrcReportQuerylogMapper;
import com.credit.domain.TPbccrcCreditCardRecord;
import com.credit.domain.TPbccrcCreditRecord;
import com.credit.domain.TPbccrcCustomerHistory;
import com.credit.domain.TPbccrcLoanRecord;
import com.credit.domain.TPbccrcQueryRecord;
import com.credit.domain.TPbccrcReport;
import com.credit.domain.TPbccrcReportContent;
import com.credit.domain.TPbccrcReportQuerylog;
import com.credit.service.IReportAnalyzeService;
import com.credit.service.ITPbccrcCreditRecordService;
import com.credit.service.PbccrcService;
import com.credit.util.CustomerHistorEnum;
import com.credit.util.Result;
@Service
public class PbccrcServiceImpl implements PbccrcService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PbccrcServiceImpl.class);
	
	@Autowired
	private TPbccrcReportMapper tPbccrcReportMapper;
	
	@Autowired
	private TPbccrcReportContentMapper tPbccrcReportContentMapper;
	
	@Autowired
	private TPbccrcQueryRecordMapper tPbccrcQueryRecordMapper;
	
	@Autowired
	private TPbccrcCreditRecordMapper tPbccrcCreditRecordMapper;
	
	@Autowired
	private TPbccrcReportQuerylogMapper  tPbccrcReportQuerylogMapper;
	
	@Autowired
	private TPbccrcCustomerHistoryMapper tPbccrcCustomerHistoryMapper;
	
//	@Autowired 
//	private CustomerHistoryMapper customerHistoryDao;
	
	@Autowired
	private TPbccrcCreditCardRecordMapper tPbccrcCreditCardRecordMapper;
	
	@Autowired 
	private TPbccrcLoanRecordMapper tPbccrcLoanRecordMapper;

	@Autowired
	private ITPbccrcCreditRecordService tPbccrcCreditRecordService;
	
	@Autowired
	private IReportAnalyzeService reportAnalyzeService;
	
	
	public void savePbccrcReport(TPbccrcReport report, String htmlContent){
		//插入征信报告信息
		tPbccrcReportMapper.insert(report);
		TPbccrcReportQuerylog reportQueryLog=null;
		reportQueryLog=getPbccrcReportQueryLogByCustomerIdCardAndCustomerName(report.getCustomerName(), report.getCustomerIdCard(),null);
		if(reportQueryLog != null){
			LOGGER.info("用户已存在报告，更新报告ID:{}" , report.getId());
			updateReportQueryLogById(reportQueryLog.getId(),report.getId());
		}else{
			//插入征信报告用户信息
		    reportQueryLog = new TPbccrcReportQuerylog();
			reportQueryLog.setCreatedDate(new Date());
			reportQueryLog.setLastModifiedDate(new Date());
			reportQueryLog.setCustomerIdCard(report.getCustomerIdCard());
			reportQueryLog.setCustomerName(report.getCustomerName());
			reportQueryLog.setReportId(report.getId());
			//用户主表保存
			tPbccrcReportQuerylogMapper.insert(reportQueryLog);
			LOGGER.info("用户首次上传报告成功...");
		}
		TPbccrcReportContent reportContent = new TPbccrcReportContent();
		reportContent.setContent(htmlContent);
		reportContent.setReportId(report.getId());
		//保存html
		tPbccrcReportContentMapper.insert(reportContent);
		LOGGER.info("=========报告html保存成功======="+report.getId());
		if(report.getQueryRecords() != null){
			for(TPbccrcQueryRecord queryRecord : report.getQueryRecords()){
				queryRecord.setReportId(report.getId());
				tPbccrcQueryRecordMapper.insert(queryRecord);
			}
			LOGGER.info("=========人行征信报告-查询记录保存成功======="+report.getId());
		}
		//保存信贷记录(信用卡和贷款)
		if(report.getCreditRecords() != null){
			for(TPbccrcCreditRecord creditRecord : report.getCreditRecords()){
				creditRecord.setReportId(report.getId());
				tPbccrcCreditRecordMapper.insert(creditRecord);
			}
			LOGGER.info("=========人行征信报告-信贷记录保存成功=======");
		}
		//用户上传历史记录
		TPbccrcCustomerHistory customerHistory = new TPbccrcCustomerHistory();
		customerHistory.setCustomerIdCard(report.getCustomerIdCard());
		customerHistory.setCustomerName(report.getCustomerName());
		customerHistory.setQueryDate(new Date());
		customerHistory.setOperator(report.getCreatorId());
		customerHistory.setReportState(CustomerHistorEnum.REPORT_ALREADY_GET.getDesc()); 
		customerHistory.setReportId(report.getId());
		tPbccrcCustomerHistoryMapper.insert(customerHistory);
		
	}
	/**
	 * 异步保存字段解读信用卡信息和贷款信息
	 * @param report
	 * @param htmlContent
	 */
	@Async
	public void syncSavePbccrcReportCardAndLoan(TPbccrcReport report,String htmlContent){
		savePbccrcReportCardAndLoan( report, htmlContent, true);
	}
	
	/**
	 * 保存字段解读信用卡信息和贷款信息
	 * @param report
	 * @param htmlContent
	 * @param flag 区别是新数据还是老数据(true为新数据，false为老数据)
	 */
	public void savePbccrcReportCardAndLoan(TPbccrcReport report, String htmlContent, boolean flag){
		//字段解读拆分
		report = reportAnalyzeService.splitCardAndLoan(report,htmlContent);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("reportId", report.getId());
		if(StringUtils.isEmpty(report.getErrorMsg())){
			//查询当前报告是否已经存在信用卡解读信息
			List<TPbccrcCreditCardRecord> creditCardRecord = tPbccrcCreditCardRecordMapper.findByReportId(map);
			//保存信用卡信息
			if(!CollectionUtils.isEmpty(report.getCreditCardRecords()) && CollectionUtils.isEmpty(creditCardRecord)){
				for(TPbccrcCreditCardRecord cardRecord : report.getCreditCardRecords()){
					cardRecord.setReportId(report.getId());
					cardRecord.setCreatedDate(new Date());
					cardRecord.setCreatedBy(report.getCreatorId());
					tPbccrcCreditCardRecordMapper.insert(cardRecord);
				}
			}
			
			//查询当前报告是否已经存在贷款解读信息
			List<TPbccrcLoanRecord> loan = tPbccrcLoanRecordMapper.findByReportId(map);
			//保存贷款信息
			if(!CollectionUtils.isEmpty(report.getLoanRecords()) && CollectionUtils.isEmpty(loan)){
				for(TPbccrcLoanRecord loanRecord : report.getLoanRecords()){
					loanRecord.setReportId(report.getId());
					loanRecord.setCreatedDate(new Date());
					loanRecord.setCreatedBy(report.getCreatorId());
					tPbccrcLoanRecordMapper.insert(loanRecord);
				}
			}
			
			//信用规则判断入库
			map.put("flag","flag");
			if(flag){
				map.put("customerIdCard", report.getCustomerIdCard());
				map.put("customerName", report.getCustomerName());
				Result<String> result = tPbccrcCreditRecordService.checkRule(map);
				if(WebConstants.SUCCESS_CODE.equals(result.getCode())){
					LOGGER.info("保存规则判断结果成功!!!");
				}else{
					LOGGER.info(JSON.toJSONString(result.getMessages()));
				}
			}
		}
	}
	
	public TPbccrcReport getPbccrcReport(Long reportId){
		return tPbccrcReportMapper.selectByPrimaryKey(reportId);
	}
	
	public TPbccrcReport getPbccrcReport(String customerName, String customerIdCard){
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("customerName", customerName);
		paramMap.put("customerIdCard", customerIdCard);
		return tPbccrcReportMapper.getReportByCustNameAndIdCard(paramMap);
	}
	
	public TPbccrcReportContent getPbccrcReportContent(Long reportId){
		return tPbccrcReportContentMapper.getReportContentByReportId(reportId);
	}
	
	/**
	 * 根据主键id更新征信查询记录最后更新时间
	 * @param id
	 */
	public int updateReportQueryLogById(Long id,Long reportId){
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("id", id);
		paramMap.put("reportId", reportId);
		return tPbccrcReportQuerylogMapper.updateReportQueryLogById(paramMap);
	}
	
	/**
	 * 
	 */
	public TPbccrcReportQuerylog getPbccrcReportQueryLogByCustomerIdCardAndCustomerName(String customerName, String customerIdCard,Long reportId){
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("customerName", customerName);
		paramMap.put("customerIdCard", customerIdCard);
		paramMap.put("reportId", reportId);
		return tPbccrcReportQuerylogMapper.getPbccrcReportQueryLogByCustomerIdCardAndCustomerName(paramMap);
	}
	
	
}
