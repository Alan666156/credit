package com.credit.service.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.credit.common.CreditType;
import com.credit.common.WebConstants;
import com.credit.dao.TPbccrcCreditCardRecordMapper;
import com.credit.dao.TPbccrcCreditRecordMapper;
import com.credit.dao.TPbccrcCustomerCreditMapper;
import com.credit.dao.TPbccrcCustomerHistoryMapper;
import com.credit.dao.TPbccrcLoanRecordMapper;
import com.credit.dao.TPbccrcQueryRecordMapper;
import com.credit.dao.TPbccrcReportContentMapper;
import com.credit.dao.TPbccrcReportMapper;
import com.credit.domain.TPbccrcCreditCardRecord;
import com.credit.domain.TPbccrcCreditRecord;
import com.credit.domain.TPbccrcCustomerCredit;
import com.credit.domain.TPbccrcCustomerHistory;
import com.credit.domain.TPbccrcLoanRecord;
import com.credit.domain.TPbccrcQueryRecord;
import com.credit.domain.TPbccrcReport;
import com.credit.domain.TPbccrcReportContent;
import com.credit.service.ITPbccrcCreditRecordService;
import com.credit.service.PbccrcService;
import com.credit.util.Arith;
import com.credit.util.DateUtils;
import com.credit.util.Result;
import com.credit.util.SpringContextUtil;
import com.credit.vo.LoanVo;

@Service
public class TPbccrcCreditRecordServiceImpl implements ITPbccrcCreditRecordService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TPbccrcCreditRecordServiceImpl.class);
	@Autowired
	private TPbccrcReportMapper tPbccrcReportMapper;
	@Autowired
	private TPbccrcCreditRecordMapper tPbccrcCreditRecordMapper;
	@Autowired
	private TPbccrcQueryRecordMapper tPbccrcQueryRecordMapper;
	@Autowired
	private TPbccrcCreditCardRecordMapper tPbccrcCreditCardRecordMapper;
	@Autowired
	private TPbccrcLoanRecordMapper tPbccrcLoanRecordMapper;
//	@Autowired
//	private TPbccrcReportQuerylogMapper tPbccrcReportQuerylogMapper;
	@Autowired
	private TPbccrcCustomerCreditMapper tPbccrcCustomerCreditMapper;
	@Autowired
	private TPbccrcReportContentMapper tPbccrcReportContentMapper;
//	@Autowired
//	private ICustomerHistoryDao ICustomerHistoryDao;
	@Autowired
	private TPbccrcCustomerHistoryMapper tPbccrcCustomerHistoryMapper;
	
//	@Autowired
//	private ITPbccrcReportQuerylogService iTPbccrcReportQuerylogService;
	
	@Override
	public List<TPbccrcCreditRecord> findCreditRecord(Map<String, Object> map) {
		TPbccrcReport tPbccrcReport = tPbccrcReportMapper.getReportByCustNameAndIdCard(map);
		if(tPbccrcReport != null){
			map.put("reportId", tPbccrcReport.getId());
			return tPbccrcCreditRecordMapper.findByReportId(map);
		}
		return null;
	}
	
	
	
	/**
	 * 规则判断
	 */
	public Result<String> checkRule(Map<String, Object> map){
		Result<String> result = new Result<String>();
		try {
			//如果传递的是报告reportId不为空则按报告id查询，否则按身份证+用户名查询
			Long reportId = map.get("reportId")==null ? null : Long.valueOf(map.get("reportId").toString());
			TPbccrcCustomerHistory customer = null;
			if(reportId != null){
				customer = tPbccrcCustomerHistoryMapper.findByReportId(reportId);
			}else{
				customer = tPbccrcCustomerHistoryMapper.getCustomerHistoryByCustomerNameAndCustomerIdCard(map);
			}
			if(customer == null){
				result.setMessages("未查询到客户信用报告信息");
				result.setCode("000001");
				result.setType(Result.Type.YES);
				result.setStatus(Result.Status.YES);
				LOGGER.info("用户名[{}],身份证[{}] 无用户征信报告信息",map.get("customerName"),map.get("customerIdCard"));
				return result;
			}
			
			TPbccrcReport tPbccrcReport = tPbccrcReportMapper.selectByPrimaryKey(customer.getReportId());
			//查询用户的人行征信报告信贷记录(有报告无贷款和信用卡)
			map.put("reportId", customer.getReportId());
			List<TPbccrcCreditRecord> list = tPbccrcCreditRecordMapper.findByReportId(map);
			
			//数据库有报告无信贷记录(无信用卡和贷款记录,有查询记录)
			if(CollectionUtils.isEmpty(list)){
				
				if(map.get("queryDate") != null && reportId == null){
					//判断是否大于15天过期(首次录入复核时间-央行报告查询时间)
					int day = DateUtils.daysOfTwo(DateUtils.stringToDate(map.get("queryDate").toString(), "yyyy-MM-dd"), tPbccrcReport.getReportTime());
					if(day > 15){
						result.setMessages("用户报告已过期");
						result.setCode("000002");
						LOGGER.info("用户名[{}],身份证[{}] 用户报告已过期...",map.get("customerName"),map.get("customerIdCard"));
						return result;
					}
				}
				result.setMessages("用户信用良好");
				result.setCode("000000");
				result.setType(Result.Type.YES);
				result.setStatus(Result.Status.YES);
				//查询次数统计,近3个月
				map.put("month", -3);
				map.put("queryTime",tPbccrcReport.getReportTime());
				int orgCount = tPbccrcQueryRecordMapper.findQueryCount(map);
				int personCount = tPbccrcQueryRecordMapper.findQueryCountByType(map);
				//查询次数统计,近1个月
				map.put("month", -1);
				int oneMonthCount = tPbccrcQueryRecordMapper.findQueryCount(map);
				int oneMonthPersonCount = tPbccrcQueryRecordMapper.findQueryCountByType(map);
				result.setOneMonthqueryCount(oneMonthCount + oneMonthPersonCount);
				result.setThreeMonthqueryCount(orgCount + personCount);
				LOGGER.info("用户名[{}],身份证[{}] 有用户征信报告信息，无信用卡和贷款记录",map.get("customerName"),map.get("customerIdCard"));
				return result;
			}
			//区分字段解读时调用，修复征审没有拆分和规则判断结果的数据
			if(map.get("flag") == null){
				//根据报告id查询时不进行过期判断
				if(reportId == null){
					//判断是否大于15天过期(首次录入复核时间-央行报告查询时间)
					int day = DateUtils.daysOfTwo(DateUtils.stringToDate(map.get("queryDate").toString(), "yyyy-MM-dd"), tPbccrcReport.getReportTime());
					if(day > 15){
						result.setMessages("用户报告已过期");
						result.setCode("000002");
						LOGGER.info("用户名[{}],身份证[{}] 用户报告已过期...",map.get("customerName"),map.get("customerIdCard"));
						return result;
					}
				}
				//查询信用不良规则判断结果是否存在
				TPbccrcCustomerCredit customerCredit = tPbccrcCustomerCreditMapper.findByReportId(customer.getReportId());
				if(customerCredit != null){
					LOGGER.info("用户名[{}],身份证[{}] 用户信用不良判断结果已存在...",map.get("customerName"),map.get("customerIdCard"));
					result.setType(customerCredit.getType().equals("0") ? Result.Type.YES : Result.Type.NO);
					result.setStatus(customerCredit.getStatus().equals("0") ? Result.Status.YES : Result.Status.NO);
					result.setCode("000000");
					result.setMessages(customerCredit.getResult());
					result.setCreditLimitMoney(customerCredit.getCreditLimitMoney());
					result.setAlreadyUseMoney(customerCredit.getAlreadyUseMoney());
					result.setOneMonthqueryCount(customerCredit.getOneMonthQueryCount());
					result.setThreeMonthqueryCount(customerCredit.getThreeMonthQueryCount());
					return result;
				}
				
				//修复老数据
				TPbccrcReportContent tPbccrcReportContent = tPbccrcReportContentMapper.getReportContentByReportId(customer.getReportId());
				List<TPbccrcCreditCardRecord> creditCardRecord = tPbccrcCreditCardRecordMapper.findByReportId(map);
				List<TPbccrcLoanRecord> loanRecord = tPbccrcLoanRecordMapper.findByReportId(map);
				if(tPbccrcReportContent!=null && CollectionUtils.isEmpty(creditCardRecord) && CollectionUtils.isEmpty(loanRecord)){
					LOGGER.info("用户名[{}],身份证[{}]已上传报告，未进行规则判断,执行数据修复",map.get("customerName"),map.get("customerIdCard"));
					//TODO spring循环注入(修改后仍不能注入成功，使用替代方案先代替)
					PbccrcService pbccrcService = SpringContextUtil.getBean(PbccrcService.class);
					pbccrcService.savePbccrcReportCardAndLoan(tPbccrcReport, tPbccrcReportContent.getContent(),false);
				}
			}
			//一、无综合信用报告判断规则&系统展示
			/*a)信用报告上没有贷记卡、准贷记卡和贷款记录的（为他人担保信息不算有贷款记录）。
			b)	征信报告有贷记卡和准贷记卡的，但每张卡的信用额度都小于500。没有信用额度的，视作信用额度为0；只计算人民币账户。
			c)	征信报告上仅有贷款的，所有贷款类型都是农户贷款。
			d)	信用报告上既有信用卡也有贷款，且信用卡和贷款分别满足上述b、c点的
			若满足上述条件之一的：
			1）	在APP端查询提示“客户符合无综合信用条件”。
			2）	进件后审批意见表有无信用记录自动填“否”，允许手工修改。*/
			BigDecimal limitMoney = new BigDecimal(500);
			map.put("creditType", WebConstants.CREDIT.贷记卡.toString());
			//查询次数统计,近三个月
			map.put("month", -3);
			map.put("queryTime",tPbccrcReport.getReportTime());
			int orgCount = tPbccrcQueryRecordMapper.findQueryCount(map);
			int personCount = tPbccrcQueryRecordMapper.findQueryCountByType(map);
			//查询次数统计,近1个月
			map.put("month", -1);
			int oneMonthCount = tPbccrcQueryRecordMapper.findQueryCount(map);
			int oneMonthPersonCount = tPbccrcQueryRecordMapper.findQueryCountByType(map);
			//贷记卡负债统计
			result.setCreditLimitMoney(tPbccrcCreditCardRecordMapper.creditCardDebtCount(map));
			result.setAlreadyUseMoney(tPbccrcCreditCardRecordMapper.creditCardAlreadyUseCount(map));
			result.setOneMonthqueryCount(oneMonthCount + oneMonthPersonCount);
			result.setThreeMonthqueryCount(orgCount + personCount);
			
			//查询贷记卡信息
			List<TPbccrcCreditCardRecord> debitCard = tPbccrcCreditCardRecordMapper.findTPbccrcCreditCard(map);
			//查询准贷记卡信息
			map.put("creditType", TPbccrcCreditRecord.CreditType.准贷记卡);
			List<TPbccrcCreditCardRecord> zdebitCard = tPbccrcCreditCardRecordMapper.findTPbccrcCreditCard(map);
			map.put("creditLimitMoney", limitMoney);
			//查询信用额度大于500的准贷记卡信息
			List<TPbccrcCreditCardRecord> zdebitCardTemp = tPbccrcCreditCardRecordMapper.findCreditCardInfo(map);
			//查询信用额度大于500的贷记卡信息
			map.put("creditType", TPbccrcCreditRecord.CreditType.贷记卡);
			List<TPbccrcCreditCardRecord> debitCardTemp = tPbccrcCreditCardRecordMapper.findCreditCardInfo(map);
			//贷款查询
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("reportId", customer.getReportId());
			List<String> creditType = new ArrayList<String>();
			//查询农户贷款
			creditType.add(TPbccrcCreditRecord.CreditType.农户贷款.toString());
			params.put("creditType", creditType);
			List<TPbccrcLoanRecord> nloan = tPbccrcLoanRecordMapper.findcreditLoanInfo(params);
			creditType.clear();
			
			//查询非农贷款
			creditType.add(WebConstants.CREDIT.个人住房贷款.toString());
			creditType.add(WebConstants.CREDIT.个人住房公积金贷款.toString());
			creditType.add(WebConstants.CREDIT.个人汽车贷款.toString());
			creditType.add(WebConstants.CREDIT.个人消费贷款.toString());
			creditType.add(WebConstants.CREDIT.个人经营性贷款.toString());
			creditType.add(WebConstants.CREDIT.个人助学贷款.toString());
			creditType.add(WebConstants.HOUSE_LOAN);
			creditType.add(WebConstants.CREDIT.其他贷款.toString());
			params.put("creditType", creditType);
			List<TPbccrcLoanRecord> loan = tPbccrcLoanRecordMapper.findcreditLoanInfo(params);
			boolean flag = false;
			//征信报告仅有贷记卡，但每张卡的信用额度都小于500。没有信用额度的，视作信用额度为0
			if(!CollectionUtils.isEmpty(debitCard) && CollectionUtils.isEmpty(debitCardTemp) && CollectionUtils.isEmpty(zdebitCard) 
					&& CollectionUtils.isEmpty(loan) && CollectionUtils.isEmpty(nloan)){
				flag = true;
			}
			
			//征信报告仅有准贷记卡，但每张卡的信用额度都小于500。没有信用额度的，视作信用额度为0
			if(!CollectionUtils.isEmpty(zdebitCard) && CollectionUtils.isEmpty(zdebitCardTemp) && CollectionUtils.isEmpty(debitCard) 
					&& CollectionUtils.isEmpty(loan) && CollectionUtils.isEmpty(nloan)){
				flag = true;
			}
			
			//征信报告同时有贷记卡和准贷记卡，但每张卡的信用额度都小于500。没有信用额度的，视作信用额度为0
			if(!CollectionUtils.isEmpty(zdebitCard) && CollectionUtils.isEmpty(zdebitCardTemp) && !CollectionUtils.isEmpty(debitCard) 
					&& CollectionUtils.isEmpty(debitCardTemp) && CollectionUtils.isEmpty(loan) && CollectionUtils.isEmpty(nloan)){
				flag = true;
			}
			
			//征信报告上仅有贷款的，所有贷款类型都是农户贷款
			if(!CollectionUtils.isEmpty(nloan) && CollectionUtils.isEmpty(debitCard) && CollectionUtils.isEmpty(zdebitCard) && CollectionUtils.isEmpty(loan)){
				flag = true;
			}
			
			//同时拥有贷款、信用卡
			if((!CollectionUtils.isEmpty(debitCard) && CollectionUtils.isEmpty(debitCardTemp) && CollectionUtils.isEmpty(zdebitCard) && !CollectionUtils.isEmpty(nloan) && CollectionUtils.isEmpty(loan)) || 
					(!CollectionUtils.isEmpty(zdebitCard) && CollectionUtils.isEmpty(zdebitCardTemp) && CollectionUtils.isEmpty(debitCard) && !CollectionUtils.isEmpty(nloan) && CollectionUtils.isEmpty(loan))){
				flag = true;
			}
			//无贷款、无信用卡
			if(CollectionUtils.isEmpty(debitCard) && CollectionUtils.isEmpty(zdebitCard) && CollectionUtils.isEmpty(loan) && CollectionUtils.isEmpty(nloan)){
				flag = true;
			}
			
			//客户是否符合无综合信用条件
			if(flag){
				result.setType(Result.Type.YES);
				LOGGER.info("姓名[{}] 身份证[{}] ：符合无综合信用条件...", map.get("customerName"), map.get("customerIdCard"));
			}else{
				result.setType(Result.Type.NO);
			}
			
			/**=========信用不良规则判断=========*/
			LOGGER.info("======开始信用不良规则判断======");
			//查询信用额度大于500、已激活、未销户、不是呆账的贷记卡信息
			map.put("bad", "1");
			map.put("closeAccount", "1");
//			map.put("active", "0");
			JSONObject memo = new JSONObject();
			List<String> resultMessage = new ArrayList<String>();//存放判断结果集
			//贷记卡判断
			List<Long> message = checkCreditCard(map, tPbccrcReport);
			String msg =null;
			if(!CollectionUtils.isEmpty(message)){
				Collections.sort(message);
				memo.put("card", JSON.toJSON(message));
				//客户信用卡
				msg = "客户信用卡" + Arrays.toString(message.toArray()).replace("[", "").replace("]", "");
			}
			//准贷记卡判断
			List<Long> messages = checkZCreditCard(map, tPbccrcReport);
			if(!CollectionUtils.isEmpty(messages)){
				Collections.sort(messages);
				memo.put("zcard", JSON.toJSON(messages));
				if(CollectionUtils.isEmpty(message)){
					msg = "客户信用卡" + Arrays.toString(messages.toArray()).replace("[", "").replace("]", "");
				}else{
					msg = msg +", " + Arrays.toString(messages.toArray()).replace("[", "").replace("]", "");
				}
			}
			if(msg != null){
				msg=msg+"信用不良";
				message.clear();
				resultMessage.add(msg);
//				result.getMessages().addAll(message);
			}
			
			//贷款判断
			JSONObject json = checkCreditLoan(map, tPbccrcReport);
			if(json.getJSONArray(CreditType.购房贷款.getName()) != null){
				message = JSON.parseArray(json.getString(CreditType.购房贷款.getName()), Long.class);
				memo.put("house", json.getString(CreditType.购房贷款.getName()));
				msg = CreditType.购房贷款.getValue() + Arrays.toString(message.toArray()).replace("[", "").replace("]", "") + "信用不良";
				resultMessage.add(msg);
//				result.getMessages().addAll(message);
			}
			
			if(json.getJSONArray(CreditType.其他贷款.getName()) != null){
				message = JSON.parseArray(json.getString(CreditType.其他贷款.getName()), Long.class);
				memo.put("other", json.getString(CreditType.其他贷款.getName()));
				msg = CreditType.其他贷款.getValue() + Arrays.toString(message.toArray()).replace("[", "").replace("]", "") + "信用不良";
				resultMessage.add(msg);
//				result.getMessages().addAll(message);
			}
			result.setCode("000000");
			
			//保存规则判断结果
			TPbccrcCustomerCredit tPbccrcCustomerCredit = new TPbccrcCustomerCredit();
			tPbccrcCustomerCredit.setCreator(tPbccrcReport.getCreatorId());
			tPbccrcCustomerCredit.setReportId(customer.getReportId());
			tPbccrcCustomerCredit.setType(result.getType() == Result.Type.YES ? WebConstants.OVERDUE:WebConstants.NORMAL_OVERDUE);
			tPbccrcCustomerCredit.setCreditLimitMoney(result.getCreditLimitMoney());
			tPbccrcCustomerCredit.setAlreadyUseMoney(result.getAlreadyUseMoney());
			tPbccrcCustomerCredit.setThreeMonthQueryCount(result.getThreeMonthqueryCount());
			tPbccrcCustomerCredit.setOneMonthQueryCount(result.getOneMonthqueryCount());
			if(CollectionUtils.isEmpty(resultMessage)){
				result.setMessages("用户信用良好");
				result.setStatus(Result.Status.YES);
				tPbccrcCustomerCredit.setStatus(WebConstants.OVERDUE);
				tPbccrcCustomerCredit.setResult("用户信用良好");
			}else{
				result.setStatus(Result.Status.NO);
				tPbccrcCustomerCredit.setStatus(WebConstants.NORMAL_OVERDUE);
//				String rs = Arrays.toString(result.getMessages().toArray()).replace("[", "").replace("]", "");
				String rs = Arrays.toString(resultMessage.toArray()).replace("[", "").replace("]", "");
				tPbccrcCustomerCredit.setMemo(memo.toJSONString());//记录不良的贷款编号
//				result.getMessages().clear();
				result.setMessages(rs);
				tPbccrcCustomerCredit.setResult(rs);
			}
			tPbccrcCustomerCreditMapper.insert(tPbccrcCustomerCredit);
			LOGGER.info("保存规则判断结果成功!!!");
		} catch (Exception e) {
			result.setMessages("客户信用不良规则判断异常！");
			result.setCode("999999");
			result.setStatus(Result.Status.NO);
			LOGGER.error("用户名[{}],身份证[{}] 判断信用不良结果异常!!!", map.get("customerName"), map.get("customerIdCard"), e);
//			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		return result;
	}
	
	/**
	 * 贷记卡规则判断
	 * @param isoverdue
	 */
	public List<Long> checkCreditCard(Map<String, Object> map, TPbccrcReport tPbccrcReport){
		LOGGER.info("============贷记卡信用不良判断============");
		List<Long> result = new LinkedList<Long>();
		List<TPbccrcCreditCardRecord> list = tPbccrcCreditCardRecordMapper.findTPbccrcCreditCard(map);
		BigDecimal num = new BigDecimal(200);
		List<Long> ids = new ArrayList<Long>();//接收信用不良贷记卡,防止重复判断
		StringBuilder message = new StringBuilder();
		//贷记卡帐户状态为呆帐
		for (TPbccrcCreditCardRecord card : list) {
			if(card.getBad().equals(WebConstants.OVERDUE)){
				result.add(card.getRecordNum());
				message.append(card.getRecordNum() + ". 信用不良，贷记卡账户状态为呆账  ");
			}
			//贷记卡当前逾期中，且逾期金额≥200元的
			if(WebConstants.OVERDUE.equals(card.getType()) && card.getOverdueMoney().compareTo(num) >=0){
				ids.add(card.getId());
				result.add(card.getRecordNum());
				message.append(card.getRecordNum() + ". 信用不良，贷记卡账户当前有逾期，并且逾期金额大于200元  ");
			}
		}
		//查询信用额度大于500、已激活、未销户、不是呆账的贷记卡信息
		//人民币账户判断时外币账户不参与，外币账户判断时人民币需要参与其他外币账户不参与（ 当前外币账户+人民币账户）
		map.put("currencyType",WebConstants.CURRENCY_TYPE);
		List<TPbccrcCreditCardRecord> cardList = tPbccrcCreditCardRecordMapper.findCreditCardInfo(map);;
		
		//如果全部为逾期,有过逾期记录，但未超过90天的信用判断
		map.put("type", WebConstants.QUERY_RECORD_TYPE_ORG);
		map.put("queryReason", "贷后管理");
		map.put("queryDate", tPbccrcReport.getReportTime());
		List<TPbccrcLoanRecord> loanList = null;
		Map<String, Boolean> isOverdue  = null;
		
		//超过90天以上逾期的，如果90天以上逾期判断时没有判断出信用不良，那就要参与到未超过90天的逾期判断。
		LOGGER.info("===========90天以上逾期的判断信用不良条件============");
		//如果全部为逾期,曾有过90天的逾期记录信用判断
		for (TPbccrcCreditCardRecord card : list) {
			//排除已判断的当前逾期中并且大于200的数据
			if(!CollectionUtils.isEmpty(ids) && ids.contains(card.getId())){
				continue;
			}
			//逾期才参与条件判断
			if(WebConstants.OVERDUE.equals(card.getType()) && card.getOverdueMonthNum() > 0){
				map.put("deadlineDate",card.getDeadlineDate());
				//结清时间小于半年与截止时间、销户时间对比
				loanList = tPbccrcLoanRecordMapper.findcreditLoan(map);
				isOverdue  = isOverdue(cardList, loanList);
				//判断是否全部为90天以上逾期
				if((isOverdue.get("creditCardMonth") && isOverdue.get("creditLoanMonth")) || (CollectionUtils.isEmpty(cardList) && CollectionUtils.isEmpty(loanList))){
					// 曾有过90天以上的逾期记录
					if(CollectionUtils.isEmpty(tPbccrcCreditCardRecordMapper.findNewCreditCard(map)) && CollectionUtils.isEmpty(tPbccrcLoanRecordMapper.findcreditNewLoan(map))) {
						ids.add(card.getId());
						//2.销户日期后或账户截止日期当月及以后没有发放新的贷记卡或贷款（农户贷款不算新发放）
						result.add(card.getRecordNum());
						message.append(card.getRecordNum() + ". 信用不良，贷记卡全部为逾期,90天以上逾期信用判断 ");
					}
				}else{
					int temp = 0;
					// 1.销户日期 or 账户截止日期与当前查询日期对比在最近1年以内
					if(DateUtils.monthsOfTwo(card.getDeadlineDate(), tPbccrcReport.getReportTime()) < 12) {
						temp++;
					}
					
					//2.销户日期后或账户截止日期当月及以后没有发放新的贷记卡或贷款（农户贷款不算新发放）
					if (CollectionUtils.isEmpty(tPbccrcCreditCardRecordMapper.findNewCreditCard(map)) && CollectionUtils.isEmpty(tPbccrcLoanRecordMapper.findcreditNewLoan(map))) {
						temp++;
					}
					
					//3.近半年内有银行关注记录，查询原因是贷后管理且查询日期和征信报告的查询时间范围在半年内
					List<TPbccrcQueryRecord> listQueryRecord = tPbccrcQueryRecordMapper.findByReportIdAndQueryType(map);
					if(!CollectionUtils.isEmpty(listQueryRecord)){
						temp++;
					}
					
					if(temp == 3){
						ids.add(card.getId());
						result.add(card.getRecordNum());
						message.append(card.getRecordNum() + ". 信用不良，贷记卡部分为逾期,90天以上逾期信用判断  ");
					}
				}
			}
		}
		
		
		LOGGER.info("===========有过逾期记录但未超过90天的判断信用不良条件============");
		//有过逾期记录但未超过90天的判断信用不良条件,所有的贷记卡参与判断
		for (TPbccrcCreditCardRecord card : list) {
			if(!CollectionUtils.isEmpty(ids) && ids.contains(card.getId())){
				continue;
			}
			//先判断是否为逾期，逾期的贷记卡才参与条件判断
			if (WebConstants.OVERDUE.equals(card.getType())) {
				map.put("deadlineDate",card.getDeadlineDate());
				//结清时间小于半年与截止时间、销户时间对比
				loanList = tPbccrcLoanRecordMapper.findcreditLoan(map);
				isOverdue  = isOverdue(cardList, loanList);
				// 计算发卡年月到截至年月 月份数
				double month = 0;
				// 发卡时间大于5年的，发卡年月到截至年月月份数取60，否则取计算出的结果
				//应还款月数=截止日期-发放日期 or 应还款月数=结清日期-发放日期
				if (DateUtils.monthsOfTwo(card.getGrantMoneyDate(), new Date()) > 60) {
					month = 60;
				} else {
					month = DateUtils.monthsOfTwo(card.getGrantMoneyDate(), card.getDeadlineDate());
				}
				int temp = 0;
				
				//判断是否为全部逾期
				if ((isOverdue.get("creditCard") && isOverdue.get("creditLoan")) || (CollectionUtils.isEmpty(cardList) && CollectionUtils.isEmpty(loanList))) {
					//1.逾期月份数≥发卡年月到截至年月月份数的50% ,逾期月份数≥发卡年月到截至年月月份数的35% (逾期月份数/(发卡年月到截至年月月份数)≥50%)
					if ((card.getOverdueMonth() >= 2 && card.getOverdueMonth() < 10 && Math.round(card.getOverdueMonth() / month * 100) >= 50) ||
							(card.getOverdueMonth() >= 10 && Math.round(card.getOverdueMonth() / month * 100)>= 35)) {
						temp++;
					}
					
					//3.销户日期后或账户截止日期当月及以后没有发放新的贷记卡或贷款（农户贷款不算新发放）
					if(CollectionUtils.isEmpty(tPbccrcCreditCardRecordMapper.findNewCreditCard(map)) && CollectionUtils.isEmpty(tPbccrcLoanRecordMapper.findcreditNewLoan(map))){
						temp++;
					}
					
					if(temp == 2){
						message.append(card.getRecordNum() + ". 信用不良，贷记卡全部为逾期,但未超过90天的信用判断  ");
						result.add(card.getRecordNum());
					}
				}else{//部分为逾期(1,2,3,4 条件)
					
					//1.逾期月份数≥发卡年月到截至年月月份数的50% ,逾期月份数≥发卡年月到截至年月月份数的35% (逾期月份数/(发卡年月到截至年月月份数)≥50%)
					if ((card.getOverdueMonth() >= 2 && card.getOverdueMonth() < 10 && Math.round(card.getOverdueMonth() / month * 100) >= 50) ||
							(card.getOverdueMonth() >= 10 && Math.round(card.getOverdueMonth() / month * 100)>= 35)) {
						temp++;
					}
					// 2.销户日期 or 账户截止日期与当前查询日期对比在最近1年以内
					if (DateUtils.monthsOfTwo(card.getDeadlineDate(), tPbccrcReport.getReportTime()) < 12) {
						temp++;
					}
					//3.销户日期后或账户截止日期当月及以后没有发放新的贷记卡或贷款（农户贷款不算新发放）
					if(CollectionUtils.isEmpty(tPbccrcCreditCardRecordMapper.findNewCreditCard(map)) && CollectionUtils.isEmpty(tPbccrcLoanRecordMapper.findcreditNewLoan(map))){
						temp++;
					}
					//4.近半年内有银行关注记录，查询原因是贷后管理且查询日期和征信报告的查询时间范围在半年内
					List<TPbccrcQueryRecord> listQueryRecord = tPbccrcQueryRecordMapper.findByReportIdAndQueryType(map);
					if(!CollectionUtils.isEmpty(listQueryRecord)){
						temp++;
					}
					if(temp == 4){
						message.append(card.getRecordNum() + ". 信用不良，贷记卡部分为逾期,但未超过90天的信用判断  ");
						result.add(card.getRecordNum());
					}
				}
				
			}
			
		}
		LOGGER.info("用户名[{}],身份证[{}] 贷记卡规则判断结果==[{}]", map.get("customerName"), map.get("customerIdCard"), message.toString());
		return result;
		
	}
	
	/**
	 * 准贷记卡规则判断
	 * @param map
	 * @param tPbccrcReport
	 * @param isOverdue 是否全部逾期
	 * @return
	 */
	public List<Long> checkZCreditCard(Map<String, Object> map, TPbccrcReport tPbccrcReport){
		LOGGER.info("============准贷记卡信用不良判断============");
		map.remove("currencyType");//移除人民币
		map.put("creditType", WebConstants.CREDIT.准贷记卡.toString());
		List<TPbccrcCreditCardRecord> list = tPbccrcCreditCardRecordMapper.findTPbccrcCreditCard(map);//查询准贷记卡
		List<Long> result = new LinkedList<Long>();
		StringBuilder message = new StringBuilder();
		
		//查询信用额度大于500、已激活、未销户、不是呆账的人民币贷记卡信息
		map.put("creditType", TPbccrcCreditRecord.CreditType.贷记卡);
		//人民币账户判断时外币账户不参与，外币账户判断时人民币需要参与其他外币账户不参与（ 当前外币账户+人民币账户）
		map.put("currencyType",WebConstants.CURRENCY_TYPE);
		List<TPbccrcCreditCardRecord> cardList = tPbccrcCreditCardRecordMapper.findCreditCardInfo(map);;
		List<TPbccrcLoanRecord> loanList = null;
		Map<String, Boolean> isOverdue  = null;
		BigDecimal num = new BigDecimal(200);
		
		for (TPbccrcCreditCardRecord card : list) {
			map.put("deadlineDate",card.getDeadlineDate());
			//结清时间小于半年与截止时间、销户时间对比
			loanList = tPbccrcLoanRecordMapper.findcreditLoan(map);
			isOverdue  = isOverdue(cardList, loanList);
			
			//如果全部为逾期,有过逾期记录
			if (WebConstants.OVERDUE.equals(card.getType()) && (isOverdue.get("creditCard") && isOverdue.get("creditLoan")) 
					|| (CollectionUtils.isEmpty(cardList) && CollectionUtils.isEmpty(loanList))) {
				// 有过逾期记录但未超过90天的判断信用不良条件
				if (WebConstants.OVERDUE.equals(card.getType()) && card.getCreditCardNinety() >= 2) {
					result.add(card.getRecordNum());
					message.append(card.getRecordNum() + ". 准贷记卡信用不良");
				}
			}else{
				// a.当前使用透支余额≥200元
				// b.有过90天以上逾期记录≥2次
				if (WebConstants.OVERDUE.equals(card.getType()) && card.getOverdraftBalance().compareTo(num) >= 0 && card.getCreditCardNinety() >= 2) {
					result.add(card.getRecordNum());
					message.append(card.getRecordNum() + ". 准贷记卡信用不良");
				}
			}
			
		}
		LOGGER.info("准贷记卡判断结果==" + message.toString());
		return result;
	}
	
	/**
	 * 贷款规则判断
	 * @param map
	 * @param tPbccrcReport
	 * @param isOverdue 是否全部逾期
	 * @return
	 */
	public JSONObject checkCreditLoan(Map<String, Object> map, TPbccrcReport tPbccrcReport){
		LOGGER.info("============开始非农贷款信用不良判断============");
		map.remove("currencyType");
		List<String> creditType = new LinkedList<String>();
		creditType.add(WebConstants.CREDIT.个人住房贷款.toString());
		creditType.add(WebConstants.CREDIT.个人住房公积金贷款.toString());
		creditType.add(WebConstants.CREDIT.个人汽车贷款.toString());
		creditType.add(WebConstants.CREDIT.个人消费贷款.toString());
		creditType.add(WebConstants.CREDIT.个人经营性贷款.toString());
		creditType.add(WebConstants.CREDIT.个人助学贷款.toString());
		creditType.add(WebConstants.HOUSE_LOAN);
		creditType.add(WebConstants.CREDIT.其他贷款.toString());
		map.put("creditType", creditType);
		//查询非农贷款
		List<TPbccrcLoanRecord> list = tPbccrcLoanRecordMapper.findcreditLoanInfo(map);
		creditType.clear();
		//查询农户贷款
		creditType.add(CreditType.农户贷款.getValue());
		map.put("creditType", creditType);
		/**农户贷款*/
		List<TPbccrcLoanRecord> nlist = tPbccrcLoanRecordMapper.findcreditLoanInfo(map);
		JSONObject result = new JSONObject();//接收不良结果
		
		List<Long> ids = new ArrayList<Long>();//接受已判断不良的id
		StringBuilder message = new StringBuilder();
		//分类接收不良信息
		List<Long> house = new ArrayList<Long>();//个人住房贷款
		List<Long> other = new ArrayList<Long>();//其他贷款
		
		//呆账、贷款当前逾期中且逾期金额≥500元的
		BigDecimal num = new BigDecimal(500);
		List<TPbccrcLoanRecord> overdue = tPbccrcLoanRecordMapper.findByReportId(map);
		for (TPbccrcLoanRecord loan : overdue) {
			if(loan.getBad().equals(WebConstants.OVERDUE)){
				message.append(loan.getRecordNum() + ". 信用不良，贷款状态为呆账  ");
			}else if(WebConstants.OVERDUE.equals(loan.getType()) && loan.getOverdueMoney().compareTo(num) >= 0){//贷款当前逾期中，且逾期金额≥500元的
				message.append(loan.getRecordNum() + ". 信用不良，贷款账户当前有逾期，并且逾期金额大于500元  ");
			}
			if(loan.getBad().equals(WebConstants.OVERDUE) || WebConstants.OVERDUE.equals(loan.getType()) && loan.getOverdueMoney().compareTo(num) >= 0){
				ids.add(loan.getId());
				if(CreditType.购房贷款.getValue().equals(loan.getLoanType())){
					house.add(loan.getRecordNum());
				}else if(CreditType.其他贷款.getValue().equals(loan.getLoanType())){
					other.add(loan.getRecordNum());
				}
			}
		}
		
		map.put("type", WebConstants.QUERY_RECORD_TYPE_ORG);
		map.put("queryReason", "贷后管理");
		//人民币账户判断时外币账户不参与，外币账户判断时人民币需要参与其他外币账户不参与（ 当前外币账户+人民币账户）
		map.put("currencyType",WebConstants.CURRENCY_TYPE);
		//查询信用额度大于500、已激活、未销户、不是呆账的人民币贷记卡信息
		map.put("creditType", TPbccrcCreditRecord.CreditType.贷记卡);
		List<TPbccrcCreditCardRecord> cardList = tPbccrcCreditCardRecordMapper.findCreditCardInfo(map);
		List<TPbccrcLoanRecord> loanList = null;
		Map<String, Boolean> isOverdue  = null;
		Date date = null;
		int temp = 0;
		LOGGER.info("=========非农贷款90天以上逾期信用不良判断==========");
		//如果全部为逾期,曾有过90天的逾期记录信用判断
		for (TPbccrcLoanRecord loan : list) {
			if(!CollectionUtils.isEmpty(ids) && ids.contains(loan.getId())){
				continue;
			}
			if (WebConstants.OVERDUE.equals(loan.getType())){
				//判断是否结清,已结清使用结清时间判断，否则取截止日期(当类型为销户时，截止日期表示销户日期)
				date = WebConstants.NORMAL_OVERDUE.equals(loan.getSettlement()) ? loan.getDeadlineDate() : loan.getSettlementDate();
				map.put("deadlineDate", date);
				//结清时间小于半年与截止时间、销户时间对比
				loanList = tPbccrcLoanRecordMapper.findcreditLoan(map);
				isOverdue  = isOverdue(cardList, loanList);
				//非农贷款判断是否全部为90天以上逾期
				if ((isOverdue.get("creditCardMonth") && isOverdue.get("creditLoanMonth")) || (CollectionUtils.isEmpty(cardList) && CollectionUtils.isEmpty(loanList))) {
					// 曾有过90天以上的逾期记录
					if (loan.getOverdueMonthNum() > 0 && (CollectionUtils.isEmpty(tPbccrcCreditCardRecordMapper.findNewCreditCard(map))
									&& CollectionUtils.isEmpty(tPbccrcLoanRecordMapper.findcreditNewLoanInfo(map)))) {
						ids.add(loan.getId());
						// 2.销户日期后或账户截止日期当月及以后没有发放新的贷记卡或贷款（农户贷款不算新发放）
						message.append(loan.getRecordNum() + ". 信用不良，非农贷款全部为90天以上逾期的信用判断  ");
						if(CreditType.购房贷款.getValue().equals(loan.getLoanType())){
							house.add(loan.getRecordNum());
						}else if(CreditType.其他贷款.getValue().equals(loan.getLoanType())){
							other.add(loan.getRecordNum());
						}
					}
				} else {
					if (loan.getOverdueMonthNum() > 0 && overdueNinety(map, tPbccrcReport, loan)) {
						ids.add(loan.getId());
						message.append(loan.getRecordNum() + ". 信用不良，非农贷款部分为90天以上逾期的信用判断  ");
						if(CreditType.购房贷款.getValue().equals(loan.getLoanType())){
							house.add(loan.getRecordNum());
						}else if(CreditType.其他贷款.getValue().equals(loan.getLoanType())){
							other.add(loan.getRecordNum());
						}
					}
				}
			}
			
		}
		
		
		LOGGER.info("=========非农贷款未超过90天的逾期信用不良判断==========");
		for (TPbccrcLoanRecord loan : list) {
			//排除已判断的当前逾期中并且大于500的数据、呆账
			if(!CollectionUtils.isEmpty(ids) && ids.contains(loan.getId())){
				continue;
			}
			
			// 有过逾期记录但未超过90天的判断信用不良条件
			if (WebConstants.OVERDUE.equals(loan.getType())) {
				//判断是否结清,已结清使用结清时间判断，否则取截止日期(当类型为销户时，截止日期表示销户日期)
				date = WebConstants.NORMAL_OVERDUE.equals(loan.getSettlement()) ? loan.getDeadlineDate() : loan.getSettlementDate();
				map.put("deadlineDate", date);
				//结清时间小于半年与截止时间、销户时间对比
				loanList = tPbccrcLoanRecordMapper.findcreditLoan(map);
				isOverdue  = isOverdue(cardList, loanList);
				
				//如果全部为逾期
				if ((isOverdue.get("creditCard") && isOverdue.get("creditLoan")) || (CollectionUtils.isEmpty(cardList) && CollectionUtils.isEmpty(loanList))) {
					temp = isBadness(map, tPbccrcReport, loan, true);
					if(temp == 2){
						message.append(loan.getRecordNum() + ". 信用不良，非农贷款全部为逾期,未超过90天的信用判断  ");
						if(CreditType.购房贷款.getValue().equals(loan.getLoanType())){
							house.add(loan.getRecordNum());
						}else if(CreditType.其他贷款.getValue().equals(loan.getLoanType())){
							other.add(loan.getRecordNum());
						}
					}
				}else{//部分为逾期(1,2,3,4条件)
					temp = isBadness(map, tPbccrcReport, loan, false);
					if(temp == 4){
						message.append(loan.getRecordNum() + ". 信用不良，非农贷款部分逾期,未超过90天的信用判断  ");
						if(CreditType.购房贷款.getValue().equals(loan.getLoanType())){
							house.add(loan.getRecordNum());
						}else if(CreditType.其他贷款.getValue().equals(loan.getLoanType())){
							other.add(loan.getRecordNum());
						}
					}
				}
				
			}
		}
		
		LOGGER.info("=========农户贷款信用不良判断==========");
		for (TPbccrcLoanRecord loan : nlist) {
			if(!CollectionUtils.isEmpty(ids) && ids.contains(loan.getId())){
				continue;
			}
			
			if(loan.getType().equals(WebConstants.OVERDUE)){
				//判断是否结清,已结清使用结清时间判断，否则取截止日期(当类型为销户时，截止日期表示销户日期)
				date = WebConstants.NORMAL_OVERDUE.equals(loan.getSettlement()) ? loan.getDeadlineDate() : loan.getSettlementDate();
				map.put("deadlineDate", date);
				//结清时间小于半年与截止时间、销户时间对比
				loanList = tPbccrcLoanRecordMapper.findcreditLoan(map);
				isOverdue  = isOverdue(cardList, loanList);
				
				//农户贷款判断是否全部为90天以上逾期
				if((isOverdue.get("creditCardMonth") && isOverdue.get("creditLoanMonth")) || (CollectionUtils.isEmpty(cardList) && CollectionUtils.isEmpty(loanList))){
					if(loan.getOverdueMonthNum() > 0
						&& (CollectionUtils.isEmpty(tPbccrcCreditCardRecordMapper.findNewCreditCard(map)) && CollectionUtils.isEmpty(tPbccrcLoanRecordMapper.findcreditNewLoanInfo(map)))){
						if(CreditType.购房贷款.getValue().equals(loan.getLoanType())){
							house.add(loan.getRecordNum());
						}else if(CreditType.其他贷款.getValue().equals(loan.getLoanType())){
							other.add(loan.getRecordNum());
						}
						message.append(loan.getRecordNum() + ". 信用不良，农户贷款全部为90天以上逾期的信用判断  ");
						continue;
					}
					
				}else{
					if(loan.getOverdueMonthNum() > 0 && overdueNinety(map, tPbccrcReport, loan)){
						if(CreditType.购房贷款.getValue().equals(loan.getLoanType())){
							house.add(loan.getRecordNum());
						}else if(CreditType.其他贷款.getValue().equals(loan.getLoanType())){
							other.add(loan.getRecordNum());
						}
						message.append(loan.getRecordNum() + ". 信用不良，农户贷款部分为90天以上逾期的信用判断  ");
						continue;
					}
				}
				
				//未超过90天,全部为逾期
				if((isOverdue.get("creditCard") && isOverdue.get("creditLoan")) || (CollectionUtils.isEmpty(cardList) && CollectionUtils.isEmpty(loanList))){
					temp = isBadness(map, tPbccrcReport, loan, true);
					if(temp == 2){
						if(WebConstants.CREDIT.购房贷款.toString().equals(loan.getLoanType())){
							house.add(loan.getRecordNum());
						}else if(WebConstants.CREDIT.其他贷款.toString().equals(loan.getLoanType())){
							other.add(loan.getRecordNum());
						}
						message.append(loan.getRecordNum() + ". 信用不良，农户贷款全部为未超过90天逾期的信用判断  ");
					}
				}else{
					temp = isBadness(map, tPbccrcReport, loan, false);
					if(temp == 4){
						if(WebConstants.CREDIT.购房贷款.toString().equals(loan.getLoanType())){
							house.add(loan.getRecordNum());
						}else if(WebConstants.CREDIT.其他贷款.toString().equals(loan.getLoanType())){
							other.add(loan.getRecordNum());
						}
						message.append(loan.getRecordNum() + ". 信用不良，农户贷款部分为未超过90天逾期的信用判断  ");
					}
				}
			}
		}
		LOGGER.info("用户名[{}],身份证[{}]:贷款规则判断结果==[{}]", map.get("customerName"), map.get("customerIdCard"), message.toString());
		
		if(house.size()>0){
			Collections.sort(house);//排序
			result.put(CreditType.购房贷款.getName(), JSON.toJSON(house));
		}
		if(other.size()>0){
			Collections.sort(other);
			result.put(CreditType.其他贷款.getName(), JSON.toJSON(other));
		}
		
		return result;
	}
	
	/**
	 * 贷款规则判断
	 * @param map
	 * @param tPbccrcReport
	 * @param isOverdue 是否全部逾期
	 * @return
	 */
	@Deprecated
	public List<String> checkCreditLoans(Map<String, Object> map, TPbccrcReport tPbccrcReport){
		LOGGER.info("============开始非农贷款信用不良判断============");
		map.remove("currencyType");
		List<String> creditType = new LinkedList<String>();
		creditType.add(WebConstants.CREDIT.个人住房贷款.toString());
		creditType.add(WebConstants.CREDIT.个人住房公积金贷款.toString());
		creditType.add(WebConstants.CREDIT.个人汽车贷款.toString());
		creditType.add(WebConstants.CREDIT.个人消费贷款.toString());
		creditType.add(WebConstants.CREDIT.个人经营性贷款.toString());
		creditType.add(WebConstants.CREDIT.个人助学贷款.toString());
		creditType.add(WebConstants.HOUSE_LOAN);
		creditType.add(WebConstants.CREDIT.其他贷款.toString());
		map.put("creditType", creditType);
		//查询非农贷款
		List<TPbccrcLoanRecord> list = tPbccrcLoanRecordMapper.findcreditLoanInfo(map);
		creditType.clear();
		//查询农户贷款
		creditType.add(WebConstants.CREDIT.农户贷款.toString());
		map.put("creditType", creditType);
		/**农户贷款*/
		List<TPbccrcLoanRecord> nlist = tPbccrcLoanRecordMapper.findcreditLoanInfo(map);
		List<String> result = new ArrayList<String>();//接收不良结果
		List<Long> ids = new ArrayList<Long>();
		StringBuilder message = new StringBuilder();
		//分类接收不良信息
		StringBuilder nh = new StringBuilder();//农户贷款
		StringBuilder house = new StringBuilder();//个人住房贷款
		StringBuilder consume = new StringBuilder();//个人消费贷款
		StringBuilder business = new StringBuilder();//个人经营贷款
		StringBuilder car = new StringBuilder();//个人汽车贷款
		StringBuilder student = new StringBuilder();//个人助学贷款
		StringBuilder businessHouse = new StringBuilder();//个人商用房（包括商住两用）贷款
		StringBuilder other = new StringBuilder();//其他贷款
		
		//呆账、贷款当前逾期中且逾期金额≥500元的
		BigDecimal num = new BigDecimal(500);
		List<TPbccrcLoanRecord> overdue = tPbccrcLoanRecordMapper.findByReportId(map);
		for (TPbccrcLoanRecord loan : overdue) {
			if(loan.getBad().equals(WebConstants.OVERDUE)){
				message.append(loan.getRecordNum() + ". 信用不良，贷款状态为呆账  ");
			}else if(WebConstants.OVERDUE.equals(loan.getType()) && loan.getOverdueMoney().compareTo(num) >= 0){//贷款当前逾期中，且逾期金额≥500元的
				message.append(loan.getRecordNum() + ". 信用不良，贷款账户当前有逾期，并且逾期金额大于500元  ");
			}
			if(loan.getBad().equals(WebConstants.OVERDUE) || WebConstants.OVERDUE.equals(loan.getType()) && loan.getOverdueMoney().compareTo(num) >= 0){
				ids.add(loan.getId());
				if(WebConstants.CREDIT.农户贷款.toString().equals(loan.getCreditType())){
					nh.append(loan.getRecordNum().toString()+", ");
				}else if(WebConstants.CREDIT.个人住房贷款.toString().equals(loan.getCreditType()) || WebConstants.CREDIT.个人住房公积金贷款.toString().equals(loan.getCreditType())){
					house.append(loan.getRecordNum().toString()+", ");
				}else if(WebConstants.CREDIT.个人消费贷款.toString().equals(loan.getCreditType())){
					consume.append(loan.getRecordNum().toString()+", ");
				}else if(WebConstants.CREDIT.个人经营性贷款.toString().equals(loan.getCreditType())){
					business.append(loan.getRecordNum().toString()+", ");
				}else if(WebConstants.CREDIT.个人汽车贷款.toString().equals(loan.getCreditType())){
					car.append(loan.getRecordNum().toString()+", ");
				}else if(WebConstants.CREDIT.个人助学贷款.toString().equals(loan.getCreditType())){
					student.append(loan.getRecordNum().toString()+", ");
				}else if(WebConstants.HOUSE_LOAN.equals(loan.getCreditType())){
					businessHouse.append(loan.getRecordNum().toString()+", ");
				}else if(WebConstants.CREDIT.其他贷款.toString().equals(loan.getCreditType())){
					other.append(loan.getRecordNum().toString()+", ");
				}
			}
		}
		
		map.put("type", WebConstants.QUERY_RECORD_TYPE_ORG);
		map.put("queryReason", "贷后管理");
		//人民币账户判断时外币账户不参与，外币账户判断时人民币需要参与其他外币账户不参与（ 当前外币账户+人民币账户）
		map.put("currencyType",WebConstants.CURRENCY_TYPE);
		//查询信用额度大于500、已激活、未销户、不是呆账的人民币贷记卡信息
		map.put("creditType", TPbccrcCreditRecord.CreditType.贷记卡);
		List<TPbccrcCreditCardRecord> cardList = tPbccrcCreditCardRecordMapper.findCreditCardInfo(map);
		List<TPbccrcLoanRecord> loanList = null;
		Map<String, Boolean> isOverdue  = null;
		Date date = null;
		int temp = 0;
		LOGGER.info("=========非农贷款90天以上逾期信用不良判断==========");
		//如果全部为逾期,曾有过90天的逾期记录信用判断
		for (TPbccrcLoanRecord loan : list) {
			if(!CollectionUtils.isEmpty(ids) && ids.contains(loan.getId())){
				continue;
			}
			if (WebConstants.OVERDUE.equals(loan.getType())){
				//判断是否结清,已结清使用结清时间判断，否则取截止日期(当类型为销户时，截止日期表示销户日期)
				date = WebConstants.NORMAL_OVERDUE.equals(loan.getSettlement()) ? loan.getDeadlineDate() : loan.getSettlementDate();
				map.put("deadlineDate", date);
				//结清时间小于半年与截止时间、销户时间对比
				loanList = tPbccrcLoanRecordMapper.findcreditLoan(map);
				isOverdue  = isOverdue(cardList, loanList);
				//非农贷款判断是否全部为90天以上逾期
				if ((isOverdue.get("creditCardMonth") && isOverdue.get("creditLoanMonth")) || (CollectionUtils.isEmpty(cardList) && CollectionUtils.isEmpty(loanList))) {
					// 曾有过90天以上的逾期记录
					if (loan.getOverdueMonthNum() > 0 && (CollectionUtils.isEmpty(tPbccrcCreditCardRecordMapper.findNewCreditCard(map))
									&& CollectionUtils.isEmpty(tPbccrcLoanRecordMapper.findcreditNewLoanInfo(map)))) {
						ids.add(loan.getId());
						// 2.销户日期后或账户截止日期当月及以后没有发放新的贷记卡或贷款（农户贷款不算新发放）
						message.append(loan.getRecordNum() + ". 信用不良，非农贷款全部为90天以上逾期的信用判断  ");
						if (WebConstants.CREDIT.个人住房贷款.toString().equals(loan.getCreditType()) || WebConstants.CREDIT.个人住房公积金贷款.toString().equals(loan.getCreditType())) {
							house.append(loan.getRecordNum().toString() + ", ");
						} else if (WebConstants.CREDIT.个人消费贷款.toString().equals(loan.getCreditType())) {
							consume.append(loan.getRecordNum().toString() + ", ");
						} else if (WebConstants.CREDIT.个人经营性贷款.toString().equals(loan.getCreditType())) {
							business.append(loan.getRecordNum().toString() + ", ");
						} else if (WebConstants.CREDIT.个人汽车贷款.toString().equals(loan.getCreditType())) {
							car.append(loan.getRecordNum().toString() + ", ");
						} else if(WebConstants.CREDIT.个人助学贷款.toString().equals(loan.getCreditType())){
							student.append(loan.getRecordNum().toString()+", ");
						} else if(WebConstants.HOUSE_LOAN.equals(loan.getCreditType())){
							businessHouse.append(loan.getRecordNum().toString()+", ");
						} else if (WebConstants.CREDIT.其他贷款.toString().equals(loan.getCreditType())) {
							other.append(loan.getRecordNum().toString() + ", ");
						}
					}
				} else {
					if (loan.getOverdueMonthNum() > 0 && overdueNinety(map, tPbccrcReport, loan)) {
						ids.add(loan.getId());
						message.append(loan.getRecordNum() + ". 信用不良，非农贷款部分为90天以上逾期的信用判断  ");
						if (WebConstants.CREDIT.个人住房贷款.toString().equals(loan.getCreditType()) || WebConstants.CREDIT.个人住房公积金贷款.toString().equals(loan.getCreditType())) {
							house.append(loan.getRecordNum().toString() + ", ");
						} else if (WebConstants.CREDIT.个人消费贷款.toString().equals(loan.getCreditType())) {
							consume.append(loan.getRecordNum().toString() + ", ");
						} else if (WebConstants.CREDIT.个人经营性贷款.toString().equals(loan.getCreditType())) {
							business.append(loan.getRecordNum().toString() + ", ");
						} else if (WebConstants.CREDIT.个人汽车贷款.toString().equals(loan.getCreditType())) {
							car.append(loan.getRecordNum().toString() + ", ");
						} else if(WebConstants.CREDIT.个人助学贷款.toString().equals(loan.getCreditType())){
							student.append(loan.getRecordNum().toString()+", ");
						} else if(WebConstants.HOUSE_LOAN.equals(loan.getCreditType())){
							businessHouse.append(loan.getRecordNum().toString()+", ");
						} else if (WebConstants.CREDIT.其他贷款.toString().equals(loan.getCreditType())) {
							other.append(loan.getRecordNum().toString() + ", ");
						}
					}
				}
			}
			
		}
		
		
		LOGGER.info("=========非农贷款未超过90天的逾期信用不良判断==========");
		for (TPbccrcLoanRecord loan : list) {
			//排除已判断的当前逾期中并且大于500的数据、呆账
			if(!CollectionUtils.isEmpty(ids) && ids.contains(loan.getId())){
				continue;
			}
			
			// 有过逾期记录但未超过90天的判断信用不良条件
			if (WebConstants.OVERDUE.equals(loan.getType())) {
				//判断是否结清,已结清使用结清时间判断，否则取截止日期(当类型为销户时，截止日期表示销户日期)
				date = WebConstants.NORMAL_OVERDUE.equals(loan.getSettlement()) ? loan.getDeadlineDate() : loan.getSettlementDate();
				map.put("deadlineDate", date);
				//结清时间小于半年与截止时间、销户时间对比
				loanList = tPbccrcLoanRecordMapper.findcreditLoan(map);
				isOverdue  = isOverdue(cardList, loanList);
				
				//如果全部为逾期
				if ((isOverdue.get("creditCard") && isOverdue.get("creditLoan")) || (CollectionUtils.isEmpty(cardList) && CollectionUtils.isEmpty(loanList))) {
					temp = isBadness(map, tPbccrcReport, loan, true);
					if(temp == 2){
						message.append(loan.getRecordNum() + ". 信用不良，非农贷款全部为逾期,未超过90天的信用判断  ");
						if(WebConstants.CREDIT.个人住房贷款.toString().equals(loan.getCreditType()) || WebConstants.CREDIT.个人住房公积金贷款.toString().equals(loan.getCreditType())){
							house.append(loan.getRecordNum().toString()+", ");
						}else if(WebConstants.CREDIT.个人消费贷款.toString().equals(loan.getCreditType())){
							consume.append(loan.getRecordNum().toString()+", ");
						}else if(WebConstants.CREDIT.个人经营性贷款.toString().equals(loan.getCreditType())){
							business.append(loan.getRecordNum().toString()+", ");
						}else if(WebConstants.CREDIT.个人汽车贷款.toString().equals(loan.getCreditType())){
							car.append(loan.getRecordNum().toString()+", ");
						}else if(WebConstants.CREDIT.个人助学贷款.toString().equals(loan.getCreditType())){
							student.append(loan.getRecordNum().toString()+", ");
						}else if(WebConstants.HOUSE_LOAN.equals(loan.getCreditType())){
							businessHouse.append(loan.getRecordNum().toString()+", ");
						}else if(WebConstants.CREDIT.其他贷款.toString().equals(loan.getCreditType())){
							other.append(loan.getRecordNum().toString()+", ");
						}
					}
				}else{//部分为逾期(1,2,3,4条件)
					temp = isBadness(map, tPbccrcReport, loan, false);
					if(temp == 4){
						message.append(loan.getRecordNum() + ". 信用不良，非农贷款部分逾期,未超过90天的信用判断  ");
						if(WebConstants.CREDIT.个人住房贷款.toString().equals(loan.getCreditType()) || WebConstants.CREDIT.个人住房公积金贷款.toString().equals(loan.getCreditType())){
							house.append(loan.getRecordNum().toString()+", ");
						}else if(WebConstants.CREDIT.个人消费贷款.toString().equals(loan.getCreditType())){
							consume.append(loan.getRecordNum().toString()+", ");
						}else if(WebConstants.CREDIT.个人经营性贷款.toString().equals(loan.getCreditType())){
							business.append(loan.getRecordNum().toString()+", ");
						}else if(WebConstants.CREDIT.个人汽车贷款.toString().equals(loan.getCreditType())){
							car.append(loan.getRecordNum().toString()+", ");
						}else if(WebConstants.CREDIT.个人助学贷款.toString().equals(loan.getCreditType())){
							student.append(loan.getRecordNum().toString()+", ");
						}else if(WebConstants.HOUSE_LOAN.equals(loan.getCreditType())){
							businessHouse.append(loan.getRecordNum().toString()+", ");
						}else if(WebConstants.CREDIT.其他贷款.toString().equals(loan.getCreditType())){
							other.append(loan.getRecordNum().toString()+", ");
						}
					}
				}
				
			}
		}
		
		LOGGER.info("=========农户贷款信用不良判断==========");
		for (TPbccrcLoanRecord loan : nlist) {
			if(!CollectionUtils.isEmpty(ids) && ids.contains(loan.getId())){
				continue;
			}
			
			if(loan.getType().equals(WebConstants.OVERDUE)){
				//判断是否结清,已结清使用结清时间判断，否则取截止日期(当类型为销户时，截止日期表示销户日期)
				date = WebConstants.NORMAL_OVERDUE.equals(loan.getSettlement()) ? loan.getDeadlineDate() : loan.getSettlementDate();
				map.put("deadlineDate", date);
				//结清时间小于半年与截止时间、销户时间对比
				loanList = tPbccrcLoanRecordMapper.findcreditLoan(map);
				isOverdue  = isOverdue(cardList, loanList);
				
				//农户贷款判断是否全部为90天以上逾期
				if((isOverdue.get("creditCardMonth") && isOverdue.get("creditLoanMonth")) || (CollectionUtils.isEmpty(cardList) && CollectionUtils.isEmpty(loanList))){
					if(loan.getOverdueMonthNum() > 0
						&& (CollectionUtils.isEmpty(tPbccrcCreditCardRecordMapper.findNewCreditCard(map)) && CollectionUtils.isEmpty(tPbccrcLoanRecordMapper.findcreditNewLoanInfo(map)))){
						nh.append(loan.getRecordNum().toString()+", ");
						message.append(loan.getRecordNum() + ". 信用不良，农户贷款全部为90天以上逾期的信用判断  ");
						continue;
					}
					
				}else{
					if(loan.getOverdueMonthNum() > 0 && overdueNinety(map, tPbccrcReport, loan)){
						nh.append(loan.getRecordNum().toString()+", ");
						message.append(loan.getRecordNum() + ". 信用不良，农户贷款部分为90天以上逾期的信用判断  ");
						continue;
					}
				}
				
				//未超过90天,全部为逾期
				if((isOverdue.get("creditCard") && isOverdue.get("creditLoan")) || (CollectionUtils.isEmpty(cardList) && CollectionUtils.isEmpty(loanList))){
					temp = isBadness(map, tPbccrcReport, loan, true);
					if(temp == 2){
						message.append(loan.getRecordNum() + ". 信用不良，农户贷款全部为未超过90天逾期的信用判断  ");
						nh.append(loan.getRecordNum().toString()+", ");
					}
				}else{
					temp = isBadness(map, tPbccrcReport, loan, false);
					if(temp == 4){
						nh.append(loan.getRecordNum().toString()+", ");
						message.append(loan.getRecordNum() + ". 信用不良，农户贷款部分为未超过90天逾期的信用判断  ");
					}
				}
			}
		}
		LOGGER.info("用户名[{}],身份证[{}]:贷款规则判断结果==[{}]", map.get("customerName"), map.get("customerIdCard"), message.toString());
		if(StringUtils.isNotEmpty(nh.toString())){
			result.add(WebConstants.CREDIT.农户贷款.toString() + nh.toString().substring(0, nh.toString().length() - 2) + "信用不良");
		}
		if(StringUtils.isNotEmpty(house.toString())){
			result.add(WebConstants.CREDIT.个人住房贷款.toString() + house.toString().substring(0, house.toString().length() - 2) + "信用不良");
		}
		if(StringUtils.isNotEmpty(consume.toString())){
			result.add(WebConstants.CREDIT.个人消费贷款.toString() + consume.toString().substring(0, consume.toString().length() - 2) + "信用不良");
		}
		if(StringUtils.isNotEmpty(business.toString())){
			result.add(WebConstants.CREDIT.个人经营性贷款.toString() + business.toString().substring(0, business.toString().length() - 2) + "信用不良");
		}
		if(StringUtils.isNotEmpty(car.toString())){
			result.add(WebConstants.CREDIT.个人汽车贷款.toString() + car.toString().substring(0, car.toString().length() - 2) + "信用不良");
		}
		if(StringUtils.isNotEmpty(student.toString())){
			result.add(WebConstants.CREDIT.个人助学贷款.toString() + student.toString().substring(0, student.toString().length() - 2) + "信用不良");
		}
		if(StringUtils.isNotEmpty(businessHouse.toString())){
			result.add(WebConstants.HOUSE_LOAN + businessHouse.toString().substring(0, businessHouse.toString().length() - 2) + "信用不良");
		}
		if(StringUtils.isNotEmpty(other.toString())){
			result.add(WebConstants.CREDIT.其他贷款.toString() + other.toString().substring(0, other.toString().length() - 2) + "信用不良");
		}
		return result;
	}
	
	
	
	/**
	 * 贷款90天以上逾期
	 * @param map
	 * @param tPbccrcReport
	 * @param loan
	 * @return
	 */
	private boolean overdueNinety(Map<String, Object> map, TPbccrcReport tPbccrcReport, TPbccrcLoanRecord loan) {
		int temp =0;
		Date date = WebConstants.NORMAL_OVERDUE.equals(loan.getSettlement()) ? loan.getDeadlineDate() : loan.getSettlementDate();
		// 1.销户日期 or 账户截止日期与当前查询日期对比在最近1年以内
		if (date != null && DateUtils.monthsOfTwo(date, tPbccrcReport.getReportTime()) < 12) {
			temp++;
		}
		//2.销户日期后或账户截止日期当月及以后没有发放新的贷记卡或贷款（农户贷款不算新发放）
		map.put("deadlineDate",date);
		if (WebConstants.OVERDUE.equals(loan.getType()) && loan.getOverdueMonthNum() > 0 && 
				(CollectionUtils.isEmpty(tPbccrcCreditCardRecordMapper.findNewCreditCard(map)) && 
				CollectionUtils.isEmpty(tPbccrcLoanRecordMapper.findcreditNewLoanInfo(map)))) {
			temp++;
		}
		//3.近半年内有银行关注记录且为贷后管理
		map.put("queryDate", tPbccrcReport.getReportTime());
		List<TPbccrcQueryRecord> listQueryRecord = tPbccrcQueryRecordMapper.findByReportIdAndQueryType(map);
		if(!CollectionUtils.isEmpty(listQueryRecord)){
			temp++;
		}
		if(temp == 3){
			return true;
		}
		return false;
	}
	
	/**
	 * 贷款未超过90天信用不良判断
	 * @param map
	 * @param tPbccrcReport
	 * @param loan
	 * @param flag 标识判断条件是走ture(1、3) or false(1、2、3、4)
	 * @return 符合条件判断的个数
	 */
	private int isBadness(Map<String, Object> map, TPbccrcReport tPbccrcReport, TPbccrcLoanRecord loan, boolean flag) {
		int temp = 0;
		// 计算发卡年月到截至年月 月份数
		double month = 0;
		//判断是否结清,已结清使用结清时间判断，否则取截止日期(当类型为销户时，截止日期表示销户日期)
		Date date = WebConstants.NORMAL_OVERDUE.equals(loan.getSettlement()) ? loan.getDeadlineDate() : loan.getSettlementDate(); 
		// 发卡时间大于5年的，发卡年月到截至年月月份数取60，否则取计算出的结果
		if (DateUtils.monthsOfTwo(loan.getGrantMoneyDate(), new Date()) > 60) {
			month = 60;
		} else {
			month = DateUtils.monthsOfTwo(loan.getGrantMoneyDate(), date);
		}
		if(flag){
			//1.逾期月份数≥发卡年月到截至年月月份数的50% ,逾期月份数≥发卡年月到截至年月月份数的35%
			if ((loan.getOverdueMonth() >= 2 && loan.getOverdueMonth() < 10 && Math.round(loan.getOverdueMonth() / month * 100) >= 50) ||
					(loan.getOverdueMonth() >= 10 && Math.round(loan.getOverdueMonth() / month * 100) >= 35)) {
				temp++;
			}
			//3.销户日期后或账户截止日期当月及以后没有发放新的贷记卡或贷款（农户贷款不算新发放）
			map.put("deadlineDate", date);
			if(CollectionUtils.isEmpty(tPbccrcCreditCardRecordMapper.findNewCreditCard(map)) && CollectionUtils.isEmpty(tPbccrcLoanRecordMapper.findcreditNewLoan(map))){
				temp++;
			}
		}else{
			//1.逾期月份数≥发卡年月到截至年月月份数的50% ,逾期月份数≥发卡年月到截至年月月份数的35%
			if ((loan.getOverdueMonth() >= 2 && loan.getOverdueMonth() < 10 && Math.round(loan.getOverdueMonth() / month * 100) >= 50) ||
					(loan.getOverdueMonth() >= 10 && Math.round(loan.getOverdueMonth() / month * 100) >= 35)) {
				temp++;
			}
			
			// 2.销户日期 or 账户截止日期与当前报告日期对比在最近1年以内
			if (date!=null && DateUtils.monthsOfTwo(date, tPbccrcReport.getReportTime()) < 12) {
				temp++;
			}
			
			//3.销户日期后或账户截止日期当月及以后没有发放新的贷记卡或贷款（农户贷款不算新发放）
			map.put("deadlineDate", date);
			if(CollectionUtils.isEmpty(tPbccrcCreditCardRecordMapper.findNewCreditCard(map)) && CollectionUtils.isEmpty(tPbccrcLoanRecordMapper.findcreditNewLoan(map))){
				temp++;
			}
			
			//4.近半年内有银行关注记录，查询原因是贷后管理且查询日期和征信报告的查询时间范围在半年内
			map.put("queryDate", tPbccrcReport.getReportTime());
			List<TPbccrcQueryRecord> listQueryRecord = tPbccrcQueryRecordMapper.findByReportIdAndQueryType(map);
			if(!CollectionUtils.isEmpty(listQueryRecord)){
				temp++;
			}
		}
		return temp;
	}
	
	/**
	 * 验证逾期
	 * 非农贷款（未结清/结清时间小于半年）、贷记卡（未销户且额度>=500）
	 * 是否全部为逾期(区分90天和90天以上逾期)
	 * @return
	 */
	public Map<String, Boolean> isOverdue(List<TPbccrcCreditCardRecord> creditCardList, List<TPbccrcLoanRecord> loanList){
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		int i = 0;
		int j = 0;
		int cardTemp = 0;
		int loanTemp = 0;
		//判断贷记卡是否全部为逾期
		for (TPbccrcCreditCardRecord card : creditCardList) {
			//全部为90天以上逾期(如果既有未超过90天的逾期又有超过90天以上的逾期，当作90天以上逾期)
			if(WebConstants.OVERDUE.equals(card.getType()) && card.getOverdueMonth() > 0 && card.getOverdueMonthNum() > 0){
				j++;
			}else if(WebConstants.OVERDUE.equals(card.getType()) && card.getOverdueMonthNum() > 0){
				j++;
			}
			//全部为逾期统计
			if(WebConstants.OVERDUE.equals(card.getType())){
				i++;
			}
		}
		
		if(!CollectionUtils.isEmpty(creditCardList) && i==0){
			map.put("creditCard", Boolean.FALSE);
		}else if((i > 0 && i == creditCardList.size()) || (i==0 && j==0)){
			map.put("creditCard", Boolean.TRUE);
		}else{
			map.put("creditCard", Boolean.FALSE);
		}
		
		if(!CollectionUtils.isEmpty(creditCardList) && j==0){
			map.put("creditCardMonth", Boolean.FALSE);
		}else if((j > 0 && j == creditCardList.size()) || (j==0 && i==0)){
			map.put("creditCardMonth", Boolean.TRUE);
		}else {
			map.put("creditCardMonth", Boolean.FALSE);
		}
		cardTemp = i;
		loanTemp = j;
		i = 0;
		j = 0;
		//判断非农贷款是否全部为逾期
		for (TPbccrcLoanRecord loan : loanList) {
			//全部为90天以上逾期
			if(WebConstants.OVERDUE.equals(loan.getType()) && loan.getOverdueMonth() > 0 && loan.getOverdueMonthNum() > 0){
				j++;
			}else if(WebConstants.OVERDUE.equals(loan.getType()) && loan.getOverdueMonthNum() > 0){
				j++;
			}
			
			if(WebConstants.OVERDUE.equals(loan.getType())){
				i++;
			}

		}
		
		if(!CollectionUtils.isEmpty(loanList) && i==0){
			map.put("creditLoan", Boolean.FALSE);
		}else if((i > 0 && i == loanList.size()) || (i==0 && j==0)){
			map.put("creditLoan", Boolean.TRUE);
		}else {
			map.put("creditLoan", Boolean.FALSE);
		}
		
		if(!CollectionUtils.isEmpty(loanList) && j==0){
			map.put("creditLoanMonth", Boolean.FALSE);
		}else if((j > 0 && j == loanList.size()) || (j==0 && i==0)){
			map.put("creditLoanMonth", Boolean.TRUE);
		}else {
			map.put("creditLoanMonth", Boolean.FALSE);
		}
		
		//同时没有贷款和贷记卡
		if(i == 0 && cardTemp == 0){
			map.put("creditCard", Boolean.FALSE);
			map.put("creditLoan", Boolean.FALSE);
		}
		if(j == 0 && loanTemp == 0){
			map.put("creditCardMonth", Boolean.FALSE);
			map.put("creditLoanMonth", Boolean.FALSE);
		}
		LOGGER.info("未90天和90天以上是否全部逾期判断结果：" + JSON.toJSONString(map));;
		return map;
	}
	
	/**
	 * 额度指引统计
	 * @param map
	 */
	public String getCreditLimit(Map<String, Object> map){
		JSONObject json = new JSONObject();
		try {
			Long reportId = map.get("reportId")==null ? null : Long.valueOf(map.get("reportId").toString());
			TPbccrcReport tPbccrcReport = tPbccrcReportMapper.selectByPrimaryKey(reportId);
			//判断报告是否存在
			if(tPbccrcReport==null){
				json.put("messages", "未查询到客户信用报告信息");
				json.put("code", "000001");
				LOGGER.info("用户名[{}],身份证[{}] 无用户征信报告信息",map.get("customerName"),map.get("customerIdCard"));
				return json.toJSONString();
			}
			
			//查询逾期次数
			Long cardMonth = tPbccrcCreditCardRecordMapper.overdueMonthCount(map);
			Long cardMonthNumber = tPbccrcCreditCardRecordMapper.overdueMonthNumCount(map);
			Long zCardMonth = tPbccrcCreditCardRecordMapper.overdueMonthCountByZCard(map);//准贷记卡超过60天的逾期数
			Long zCardMonthNumber = tPbccrcCreditCardRecordMapper.overdueMonthNumCountByZCard(map);//准贷记卡超过90天的逾期数
			Long loanMonth = tPbccrcLoanRecordMapper.overdueMonthCount(map);
			Long loanMonthNumber = tPbccrcLoanRecordMapper.overdueMonthNumCount(map);
			//查询次数统计,近3个月
			map.put("month", -3);
			map.put("queryTime",tPbccrcReport.getReportTime());
			int orgCount = tPbccrcQueryRecordMapper.findQueryCount(map);
			int personCount = tPbccrcQueryRecordMapper.findQueryCountByType(map);
			//查询次数统计,近1个月
			map.put("month", -1);
			int oneMonthCount = tPbccrcQueryRecordMapper.findQueryCount(map);
			int oneMonthPersonCount = tPbccrcQueryRecordMapper.findQueryCountByType(map);
			json.put("oneMonthqueryCount", oneMonthCount + oneMonthPersonCount);//近一个月查询次数
			json.put("threeMonthqueryCount", orgCount + personCount);//近三个月查询次数
			
			json.put("cardCount", cardMonth + cardMonthNumber + zCardMonth + zCardMonthNumber);//信用卡逾期总次数
			json.put("loanCount", loanMonth + loanMonthNumber);//贷款逾期次数
			//判断是否有90天以上逾期
			if(cardMonthNumber>0 || zCardMonthNumber>0 || loanMonthNumber>0){
				json.put("type", true);
			}else{
				json.put("type", false);
			}
			json.put("code", "000000");
		} catch (Exception e) {
			json.put("code", "999999");
			json.put("messages", "查询额度指引异常");
			LOGGER.error("用户名[{}],身份证[{}] 查询额度指引异常!",map.get("customerName"),map.get("customerIdCard"), e);
		}
		return json.toJSONString();
	}
	
	@Override
	public String findLoanLimitInfo(Map<String, Object> map) {
		LoanVo vo = null;
		List<LoanVo> ls = new ArrayList<LoanVo>();
		JSONObject json = new JSONObject();
		try {
			Long reportId = map.get("reportId")==null ? null : Long.valueOf(map.get("reportId").toString());
			TPbccrcReport tPbccrcReport = tPbccrcReportMapper.selectByPrimaryKey(reportId);
			//判断报告是否存在
			if(tPbccrcReport==null){
				json.put("messages", "未查询到客户信用报告信息");
				json.put("code", "000001");
				LOGGER.info("用户名[{}],身份证[{}] 无用户征信报告信息",map.get("customerName"),map.get("customerIdCard"));
				return json.toJSONString();
			}
			
			List<TPbccrcLoanRecord> list = tPbccrcLoanRecordMapper.findLoanLimitInfo(map);
			//无贷款信息
			if(CollectionUtils.isEmpty(list)){
				LOGGER.info("用户名[{}],身份证[{}] 无贷款信息...",map.get("customerName"),map.get("customerIdCard"));
				json.put("code", "000002");
				json.put("messages", "未查询到客户贷款信息");
				return json.toJSONString();
			}
			Date date = new Date();
			for(TPbccrcLoanRecord loan : list){
				vo = new LoanVo();
				if(WebConstants.OVERDUE.equals(loan.getSettlement())){
					vo.setRepayPeriods(DateUtils.monthsOfTwo(loan.getGrantMoneyDate(), loan.getSettlementDate()));
					vo.setAlreadyRepayPeriods(DateUtils.monthsOfTwo(loan.getSettlementDate(), date));//已还期数
					vo.setDebtMoney(BigDecimal.ZERO);
				}else{
					//到期时间与发放时间为同一个月，期数为0期时算1期
					int periods = DateUtils.monthsOfTwo(loan.getGrantMoneyDate(), loan.getExpireDate());
					vo.setRepayPeriods(periods == 0 ? 1 : periods);
					vo.setDebtMoney(Arith.sub(loan.getGrantMoney(), loan.getBalance()));//负债
					int month = date.compareTo(loan.getExpireDate()) > 0 ? periods : DateUtils.monthsOfTwo(loan.getGrantMoneyDate(), date);
					vo.setAlreadyRepayPeriods(month);
				}
				vo.setGrantMoney(loan.getGrantMoney());//额度
				vo.setNo(loan.getRecordNum());
				ls.add(vo);
			}
			json.put("code", "000000");
			json.put("loan", ls);
		} catch (Exception e) {
			LOGGER.info("用户名[{}],身份证[{}] 查询征信贷款信息异常!",map.get("customerName"),map.get("customerIdCard"), e);
			json.put("code", "999999");
			json.put("messages", "查询额度指引异常");
		}
		return json.toJSONString();
	}

	
	@Override
	public String getCreditInfo(Map<String, Object> map) {
		Result<String> result = new Result<String>();
		String temp ="请补充0月或1月还款记录。";
		StringBuilder sb = new StringBuilder();
		try {
			Long reportId = map.get("reportId")==null ? null : Long.valueOf(map.get("reportId").toString());
			TPbccrcReport tPbccrcReport = tPbccrcReportMapper.selectByPrimaryKey(reportId);
			//判断报告是否存在
			if(tPbccrcReport == null){
				result.setMessages("未查询到用户信用报告信息");
				result.setCode("000001");
				result.setType(Result.Type.YES);
				result.setStatus(Result.Status.YES);
				LOGGER.info("用户名[{}],身份证[{}] 无用户征信报告信息",map.get("customerName"),map.get("customerIdCard"));
				
				return JSON.toJSONString(result);
			}
			
			List<TPbccrcCreditRecord> list = tPbccrcCreditRecordMapper.findByReportId(map);
			
			//数据库有报告无信贷记录(无信用卡和贷款记录,有查询记录)
			if(CollectionUtils.isEmpty(list)){
				result.setMessages("用户信用良好");
				result.setCode("000000");
				result.setType(Result.Type.YES);
				result.setStatus(Result.Status.YES);
				//查询次数统计,近3个月
				map.put("month", -3);
				map.put("queryTime",tPbccrcReport.getReportTime());
				int orgCount = tPbccrcQueryRecordMapper.findQueryCount(map);
				int personCount = tPbccrcQueryRecordMapper.findQueryCountByType(map);
				//查询次数统计,近1个月
				map.put("month", -1);
				int oneMonthCount = tPbccrcQueryRecordMapper.findQueryCount(map);
				int oneMonthPersonCount = tPbccrcQueryRecordMapper.findQueryCountByType(map);
				result.setOneMonthqueryCount(oneMonthCount + oneMonthPersonCount);
				result.setThreeMonthqueryCount(orgCount + personCount);
				LOGGER.info("用户名[{}],身份证[{}] 有用户征信报告信息，无信用卡和贷款记录",map.get("customerName"),map.get("customerIdCard"));
				return JSON.toJSONString(result);
			}
			List<Long> message = null;
			//查询信用不良规则判断结果是否存在
			TPbccrcCustomerCredit customerCredit = tPbccrcCustomerCreditMapper.findByReportId(reportId);
			
			if(customerCredit == null){
				LOGGER.info("用户名[{}],身份证[{}] 信用报告判断未生成...", map.get("customerName"),map.get("customerIdCard"));
				result.setMessages("暂无信用报告不良判断结果");
				result.setCode("000004");
			}else{
				LOGGER.info("用户名[{}],身份证[{}] 用户信用不良判断结果已存在...",map.get("customerName"),map.get("customerIdCard"));
				result.setType(customerCredit.getType().equals("0") ? Result.Type.YES : Result.Type.NO);
				result.setStatus(customerCredit.getStatus().equals("0") ? Result.Status.YES : Result.Status.NO);
				result.setCode("000000");
				result.setMessages(customerCredit.getResult());
				result.setCreditLimitMoney(customerCredit.getCreditLimitMoney());
				result.setAlreadyUseMoney(customerCredit.getAlreadyUseMoney());
				result.setOneMonthqueryCount(customerCredit.getOneMonthQueryCount());
				result.setThreeMonthqueryCount(customerCredit.getThreeMonthQueryCount());
				
				
				//memo为NULL时信用良好
				if(StringUtils.isNotEmpty(customerCredit.getMemo())){
					JSONObject js = JSON.parseObject(customerCredit.getMemo());
					//贷记卡
					if(StringUtils.isNotEmpty(js.getString("card"))){
						message = JSONArray.parseArray(js.getString("card"), Long.class);
						if(!CollectionUtils.isEmpty(message)){
							StringBuilder currentOverdue = new StringBuilder();//当前逾期
							StringBuilder overdue = new StringBuilder();//逾期
							map.put("recordNums", message);
							List<TPbccrcCreditCardRecord> cardList = tPbccrcCreditCardRecordMapper.findByRecordNum(map);
							for(TPbccrcCreditCardRecord card : cardList){
								if(card.getOverdueMoney() != null && card.getOverdueMoney().compareTo(BigDecimal.ZERO) > 0){
									int month = card.getDeadlineDate().getMonth();
									currentOverdue.append(card.getRecordNum()+"、"+card.getContent()+ temp.replace("0", String.valueOf(month+1)).replace("1", String.valueOf(month+2)));
								}else{
									overdue.append(card.getRecordNum() + "、初判客户信用不良。");
								}
							}
							
							if(StringUtils.isNotEmpty(currentOverdue.toString())){
								sb.append("贷记卡当前逾期：" + currentOverdue.toString());
							}
							
							if(StringUtils.isNotEmpty(overdue.toString())){
								sb.append("贷记卡非当前逾期："+overdue.toString());
							}
						}
					}
					
					
					
					//准贷记卡
					if(StringUtils.isNotEmpty(js.getString("zcard"))){
						message = JSONArray.parseArray(js.getString("zcard"), Long.class);
						if(!CollectionUtils.isEmpty(message)){
							StringBuilder currentOverdue = new StringBuilder();//当前逾期
							StringBuilder overdue = new StringBuilder();//逾期
							map.put("recordNums", message);
							List<TPbccrcCreditCardRecord> cardList = tPbccrcCreditCardRecordMapper.findByRecordNum(map);
							for(TPbccrcCreditCardRecord card : cardList){
								if(card.getOverdueMoney() != null && card.getOverdueMoney().compareTo(BigDecimal.ZERO) > 0){
									int month = card.getDeadlineDate().getMonth();
									currentOverdue.append(card.getRecordNum()+"、"+card.getContent()+ temp.replace("0", String.valueOf(month+1)).replace("1", String.valueOf(month+2)));
								}else{
									overdue.append(card.getRecordNum()+"、初判客户信用不良。");
								}
							}
							
							if(StringUtils.isNotEmpty(currentOverdue.toString())){
								sb.append("准贷记卡当前逾期：" + currentOverdue.toString());
							}
							
							if(StringUtils.isNotEmpty(overdue.toString())){
								sb.append("准贷记卡非当前逾期："+overdue.toString());
							}
						}
					}
					//购房贷款
					if(StringUtils.isNotEmpty(js.getString("house"))){
						message = JSONArray.parseArray(js.getString("house"), Long.class);
						if(!CollectionUtils.isEmpty(message)){
							StringBuilder currentOverdue = new StringBuilder();//当前逾期
							StringBuilder overdue = new StringBuilder();//逾期
							map.put("recordNums", message);
							map.put("loanType", CreditType.购房贷款.getValue());
							List<TPbccrcLoanRecord> loanList = tPbccrcLoanRecordMapper.findByRecordNum(map);
							for(TPbccrcLoanRecord loan : loanList){
								if(loan.getOverdueMoney() != null && loan.getOverdueMoney().compareTo(BigDecimal.ZERO) > 0){
									currentOverdue.append(loan.getRecordNum()+"、"+loan.getContent()+ "请补充近三个月还款流水。");
								}else{
									overdue.append(loan.getRecordNum()+"、初判客户信用不良。");
								}
							}
							
							if(StringUtils.isNotEmpty(currentOverdue.toString())){
								sb.append("购房贷款当前逾期：" + currentOverdue.toString());
							}
							
							if(StringUtils.isNotEmpty(overdue.toString())){
								sb.append("购房贷款非当前逾期："+overdue.toString());
							}
						}
					}
					//其他贷款
					if(StringUtils.isNotEmpty(js.getString("other"))){
						message = JSONArray.parseArray(js.getString("other"), Long.class);
						if(!CollectionUtils.isEmpty(message)){
							StringBuilder currentOverdue = new StringBuilder();//当前逾期
							StringBuilder overdue = new StringBuilder();//逾期
							map.put("recordNums", message);
							map.put("loanType", CreditType.其他贷款.getValue());
							List<TPbccrcLoanRecord> loanList = tPbccrcLoanRecordMapper.findByRecordNum(map);
							for(TPbccrcLoanRecord loan : loanList){
								if(loan.getOverdueMoney() != null && loan.getOverdueMoney().compareTo(BigDecimal.ZERO) > 0){
									currentOverdue.append(loan.getRecordNum()+"、"+loan.getContent() + "请补充近三个月还款流水。");
								}else{
									overdue.append(loan.getRecordNum()+"、初判客户信用不良。");
								}
							}
							
							if(StringUtils.isNotEmpty(currentOverdue.toString())){
								sb.append("其他贷款当前逾期：" + currentOverdue.toString());
							}
							
							if(StringUtils.isNotEmpty(overdue.toString())){
								sb.append("其他贷款非当前逾期："+overdue.toString());
							}
						}
						
					}
				}
			}
			result.setData(sb.toString());
		} catch (Exception e) {
			LOGGER.error("用户名[{}],身份证[{}] APP客户信用不良信息异常!",map.get("customerName"),map.get("customerIdCard"), e);
			result.setData(null);
			result.setMessages("查询客户信用不良信息异常！");
			result.setCode("999999");
			result.setStatus(Result.Status.NO);
		} finally {
		}
		return JSON.toJSONString(result);
	}

}
