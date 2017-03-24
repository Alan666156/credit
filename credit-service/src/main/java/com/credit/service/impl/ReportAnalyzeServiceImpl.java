package com.credit.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.credit.common.WebConstants;
import com.credit.domain.ReportModelValue;
import com.credit.domain.TPbccrcCreditCardRecord;
import com.credit.domain.TPbccrcCreditRecord;
import com.credit.domain.TPbccrcLoanRecord;
import com.credit.domain.TPbccrcQueryRecord;
import com.credit.domain.TPbccrcReport;
import com.credit.service.IReportAnalyzeService;
import com.credit.util.Arith;
import com.credit.util.DateUtils;
import com.credit.util.InitDatas;
import com.credit.util.JackJsonUtil;
/**
 * 央行征信报告解读字段拆分
 * @author fuhongxing
 */
@Service
public class ReportAnalyzeServiceImpl implements IReportAnalyzeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportAnalyzeServiceImpl.class);

	public TPbccrcReport getPbccrcContent(String htmlContent) {
		LOGGER.info("============开始解析征信报告,生成报告主表信息=============");
		TPbccrcReport report = new TPbccrcReport();
		StringBuilder errorMsg = new StringBuilder();
		try {
			DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			Document doc = Jsoup.parse(htmlContent, "", Parser.xmlParser());
			Element body = doc.body();

			Elements e1 = body.select("tr > td > strong[class=p]");
			if (e1.isEmpty()) {
				errorMsg.append("征信报告样式发生变化，请重新编写解析方法。");
			}
			for (int i = 0; i < e1.size(); i++) {
				Element e = e1.get(i);
				if (e.text().startsWith("报告编号")) {
					report.setReportNo(e.text().substring(5, e.text().length()));
				} else if (e.text().startsWith("查询时间")) {
					try {
						report.setQueryTime(df.parse(e.text().substring(5, e.text().length())));
					} catch (ParseException ex) {
						errorMsg.append("查询时间转换失败，原值为" + e.text().substring(5, e.text().length()) + "。");
					}
				} else if (e.text().startsWith("报告时间")) {
					try {
						report.setReportTime(df.parse(e.text().substring(5, e.text().length())));
					} catch (ParseException ex) {
						errorMsg.append("报告时间转换失败，原值为" + e.text().substring(5, e.text().length()) + "。");
					}
				} else if (e.text().startsWith("姓名")) {
					report.setName(e.text().substring(3, e.text().length()).trim());
				} else if (e.text().startsWith("证件类型")) {
					report.setCertType(e.text().substring(5, e.text().length()));
				} else if (e.text().startsWith("证件号码")) {
					report.setCertNo(e.text().substring(5, e.text().length()).trim());
					// 婚姻状况
					report.setMaritalStatus(e1.get(i + 1).text());
					// 信贷记录说明
					report.setCreditRecordDescription(e1.get(i + 2).text());
					// 公共记录说明
					report.setCommonRecordDescription(e1.get(i + 3).text());
					// 查询记录说明
					report.setQueryRecordDescription(e1.get(i + 4).text());
				}

				// 查询记录
				if (e.text().startsWith("机构查询记录明细")) {
					Elements queryRecordElements = e.parent().parent().parent().parent().select("tr");
					for (int j = 3; j < queryRecordElements.size(); j++) {
						Elements tdElements = queryRecordElements.get(j).select("td");
						if (tdElements.size() == 4) {
							TPbccrcQueryRecord queryRecord = new TPbccrcQueryRecord();
							queryRecord.setQueryNo(tdElements.get(0).text());
							queryRecord.setQueryDate(tdElements.get(1).text());
							queryRecord.setOperator(tdElements.get(2).text());
							queryRecord.setQueryReason(tdElements.get(3).text());
							queryRecord.setType(WebConstants.QUERY_RECORD_TYPE_ORG);
							report.addQueryRecord(queryRecord);
						}
					}
				}
				if (e.text().startsWith("个人查询记录明细")) {
					Elements queryRecordElements = e.parent().parent().parent().parent().select("tr");
					for (int j = 3; j < queryRecordElements.size(); j++) {
						Elements tdElements = queryRecordElements.get(j).select("td");
						if (tdElements.size() == 4) {
							TPbccrcQueryRecord queryRecord = new TPbccrcQueryRecord();
							queryRecord.setQueryNo(tdElements.get(0).text());
							queryRecord.setQueryDate(tdElements.get(1).text());
							queryRecord.setOperator(tdElements.get(2).text());
							queryRecord.setQueryReason(tdElements.get(3).text());
							queryRecord.setType(WebConstants.QUERY_RECORD_TYPE_SELF);
							report.addQueryRecord(queryRecord);
						}
					}
				}
			}

			String type = WebConstants.CREDIT_RECORD_TYPE_CREDIT_CARD_OVERDUE;
			// 信贷记录-信用卡
			Elements eCreditCard = body.select("span[class=h1]:contains(信用卡) + ol span, span[class=h1]:contains(信用卡) + ol li");
			int recordNo = 0;// 编号
			for (int i = 0; i < eCreditCard.size(); i++) {
				Element e = eCreditCard.get(i);
				if (e.tagName().equals("span")) {
					if (e.text().startsWith("发生过逾期")) {
						type = WebConstants.CREDIT_RECORD_TYPE_CREDIT_CARD_OVERDUE;
					} else if (e.text().startsWith("从未逾期过")) {
						type = WebConstants.CREDIT_RECORD_TYPE_CREDIT_CARD_NORMAL;
					}
				} else {
					recordNo++;
					TPbccrcCreditRecord creditRecord = new TPbccrcCreditRecord();
					creditRecord.setContent(e.text());
					creditRecord.setType(type);
					creditRecord.setRecordNum(Long.valueOf(recordNo));
					report.addCreditRecord(creditRecord);

				}
			}
			// 信贷记录-购房贷款
			recordNo = 0;
			Elements eHousingLoan = body.select("span[class=h1]:contains(购房贷款) + ol span, span[class=h1]:contains(购房贷款) + ol li");
			for (int i = 0; i < eHousingLoan.size(); i++) {
				Element e = eHousingLoan.get(i);
				if (e.tagName().equals("span")) {
					if (e.text().startsWith("发生过逾期")) {
						type = WebConstants.CREDIT_RECORD_TYPE_HOUSING_LOAN_OVERDUE;
					} else if (e.text().startsWith("从未逾期过")) {
						type = WebConstants.CREDIT_RECORD_TYPE_HOUSING_LOAN_NORMAL;
					}
				} else {
					recordNo++;
					TPbccrcCreditRecord creditRecord = new TPbccrcCreditRecord();
					creditRecord.setContent(e.text());
					creditRecord.setType(type);
					creditRecord.setRecordNum(Long.valueOf(recordNo));
					report.addCreditRecord(creditRecord);

				}
			}

			// 信贷记录-其他贷款
			recordNo = 0;
			Elements eOtherLoan = body.select("span[class=h1]:contains(其他贷款) + ol span, span[class=h1]:contains(其他贷款) + ol li");
			for (int i = 0; i < eOtherLoan.size(); i++) {
				Element e = eOtherLoan.get(i);
				if (e.tagName().equals("span")) {
					if (e.text().startsWith("发生过逾期")) {
						type = WebConstants.CREDIT_RECORD_TYPE_OTHER_LOAN_OVERDUE;

					} else if (e.text().startsWith("从未逾期过")) {
						type = WebConstants.CREDIT_RECORD_TYPE_OTHER_LOAN_NORMAL;
					}
				} else {
					recordNo++;
					TPbccrcCreditRecord creditRecord = new TPbccrcCreditRecord();
					creditRecord.setContent(e.text());
					creditRecord.setType(type);
					creditRecord.setRecordNum(Long.valueOf(recordNo));
					report.addCreditRecord(creditRecord);

				}
			}

			// 保证人代偿信息
			recordNo = 0;
			Elements eRepayment = body.select("span[class=h1]:contains(保证人代偿信息) + br ol:eq(0) li");
			for (int i = 0; i < eRepayment.size(); i++) {
				Element e = eRepayment.get(i);
				recordNo++;
				TPbccrcCreditRecord creditRecord = new TPbccrcCreditRecord();
				creditRecord.setContent(e.text());
				creditRecord.setType(WebConstants.CREDIT_RECORD_TYPE_REPAYMENT);
				creditRecord.setRecordNum(Long.valueOf(recordNo));
				report.addCreditRecord(creditRecord);
			}

			// 为他人担保信息
			recordNo = 0;
			Elements eGuarantee = body.select("span[class=h1]:contains(为他人担保信息) + ol li");
			for (int i = 0; i < eGuarantee.size(); i++) {
				Element e = eGuarantee.get(i);
				recordNo++;
				TPbccrcCreditRecord creditRecord = new TPbccrcCreditRecord();
				creditRecord.setContent(e.text());
				creditRecord.setType(WebConstants.CREDIT_RECORD_TYPE_GUARANTEE);
				creditRecord.setRecordNum(Long.valueOf(recordNo));
				report.addCreditRecord(creditRecord);
			}

			// 底部的说明
			StringBuilder description = new StringBuilder();
			Elements e2 = body.select("ol > li[class=p]");
			for (int i = 0; i < e2.size(); i++) {
				Element e = e2.get(i);
				description.append(e.text());
			}
			report.setDescription(description.toString());

			// 信贷记录-信息概要
			StringBuilder creditReportSummary = new StringBuilder();
			Elements e3 = body.select("td div > span");
			for (int i = 0; i < e3.size(); i++) {
				Element e = e3.get(i);
				creditReportSummary.append(e.text());
			}
			report.setCreditRecordSummary(creditReportSummary.toString());

			Elements e4 = body.select("tr > td > table > tbody > tr > td > table > tbody > tr > td[class=p]");
			for (int i = 0; i < e4.size(); i++) {
				Element e = e4.get(i);
				try {
					if (e.text().endsWith("笔数") && e.text().length() < 4) {
						report.setAssetTotal(Integer.valueOf(e4.get(i + 1).text()));
						report.setRepaymentTotal(Integer.valueOf(e4.get(i + 2).text()));
					}
					if (e.text().endsWith("账户数") && e.text().length() < 5) {
						report.setCreditCardTotal(Integer.valueOf(e4.get(i + 1).text()));
						report.setHousingLoanTotal(Integer.valueOf(e4.get(i + 2).text()));
						report.setOtherLoanTotal(Integer.valueOf(e4.get(i + 3).text()));
					}
					if (e.text().endsWith("未结清/未销户账户数")) {
						report.setCreditCardActive(Integer.valueOf(e4.get(i + 1).text()));
						report.setHousingLoanActive(Integer.valueOf(e4.get(i + 2).text()));
						report.setOtherLoanActive(Integer.valueOf(e4.get(i + 3).text()));
					}
					if (e.text().endsWith("发生过逾期的账户数")) {
						report.setCreditCardOverdue(Integer.valueOf(e4.get(i + 1).text()));
						report.setHousingLoanOverdue(Integer.valueOf(e4.get(i + 2).text()));
						report.setOtherLoanOverdue(Integer.valueOf(e4.get(i + 3).text()));
					}
					if (e.text().endsWith("发生过90天以上逾期的账户数")) {
						report.setCreditCardSeriousOverdue(Integer.valueOf(e4.get(i + 1).text()));
						report.setHousingLoanSeriousOverdue(Integer.valueOf(e4.get(i + 2).text()));
						report.setOtherLoanSeriousOverdue(Integer.valueOf(e4.get(i + 3).text()));
					}
					if (e.text().endsWith("为他人担保笔数")) {
						report.setCreditCardGuarantee(Integer.valueOf(e4.get(i + 1).text()));
						report.setHousingLoanGuarantee(Integer.valueOf(e4.get(i + 2).text()));
						report.setOtherLoanGuarantee(Integer.valueOf(e4.get(i + 3).text()));
					}
				} catch (NumberFormatException ex) {
					errorMsg.append("信贷记录信息概要表格数字转换失败，出错标识位为" + e.text() + "，原值为" + e4.get(i + 1).text() + "，"
							+ e4.get(i + 2).text() + "，" + e4.get(i + 3).text() + "。");
				}
			}
			// 公共记录-法院强制执行记录
			Elements e5 = body.select("span[class=h1]:contains(强制执行记录) + table");
			Elements courtEnforcementElements = e5.select("td");
			Map<String, String> courtEnforcementMap = new HashMap<String, String>();
			for (int i = 0; i < courtEnforcementElements.size(); i++) {
				Element e = courtEnforcementElements.get(i);
				if (e.text().indexOf("：") != -1) {
					courtEnforcementMap.put(e.text().substring(0, e.text().indexOf("：")), e.text().substring(e.text().indexOf("：") + 1, e.text().length()));
				}
			}
			if (!courtEnforcementMap.isEmpty()) {
				report.setCommonRecordDetail(JackJsonUtil.objToStr(courtEnforcementMap));
			}
		} catch (Exception ex) {
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw, true));
			errorMsg.append(sw.toString().length() > 1000 ? sw.toString().substring(0, 999) : sw.toString());
			LOGGER.error("央行征信报告解析异常", ex);
		}
		report.setErrorMsg(errorMsg.toString());
		return report;
	}

	//
	public TPbccrcReport splitCardAndLoan(TPbccrcReport report, String htmlContent) {
		StringBuilder errorMsg = new StringBuilder();
		LOGGER.info("字段解读拆分入参：" + report.getId());
		try {

			Document doc = Jsoup.parse(htmlContent, "", Parser.xmlParser());
			Element body = doc.body();

			Elements e1 = body.select("tr > td > strong[class=p]");
			if (e1.isEmpty()) {
				errorMsg.append("征信报告样式发生变化，请重新编写解析方法。");
			}
			String type = WebConstants.CREDIT_RECORD_TYPE_CREDIT_CARD_OVERDUE;
			String recordType = WebConstants.OVERDUE;
			// 信贷记录-信用卡
			Elements eCreditCard = body.select("span[class=h1]:contains(信用卡) + ol span, span[class=h1]:contains(信用卡) + ol li");
			int cardRecordNo = 0;
			for (int i = 0; i < eCreditCard.size(); i++) {
				Element e = eCreditCard.get(i);
				if (e.tagName().equals("span")) {
					if (e.text().startsWith("发生过逾期")) {
						type = WebConstants.CREDIT_RECORD_TYPE_CREDIT_CARD_OVERDUE;
						recordType = WebConstants.OVERDUE;
					} else if (e.text().startsWith("从未逾期过")) {
						type = WebConstants.CREDIT_RECORD_TYPE_CREDIT_CARD_NORMAL;
						recordType = WebConstants.NORMAL_OVERDUE;
					}
				} else {
					cardRecordNo++;
					TPbccrcCreditRecord creditRecord = new TPbccrcCreditRecord();
					creditRecord.setContent(e.text());
					creditRecord.setType(type);
					report.addCreditRecord(creditRecord);
					if (creditRecord != null && StringUtils.isNotEmpty(creditRecord.getContent())) {
						TPbccrcCreditCardRecord cardRecord = getPbccrcContentForCreditCard("1", creditRecord.getContent());
						cardRecord.setRecordNum(Long.valueOf(cardRecordNo));
						cardRecord.setType(recordType);
						cardRecord.setContent(e.text());
						report.addCreditCardRecord(cardRecord);
					}

				}
			}

			// 信贷记录-购房贷款
			Elements eHousingLoan = body.select("span[class=h1]:contains(购房贷款) + ol span, span[class=h1]:contains(购房贷款) + ol li");
			int loanRecordNo = 0;
			for (int i = 0; i < eHousingLoan.size(); i++) {
				Element e = eHousingLoan.get(i);
				// 取标题是否发生过逾期(例如:发生过逾期的账户明细如下：)
				if (e.tagName().equals("span")) {
					if (e.text().startsWith("发生过逾期")) {
						type = WebConstants.CREDIT_RECORD_TYPE_HOUSING_LOAN_OVERDUE;
						recordType = WebConstants.OVERDUE;
					} else if (e.text().startsWith("从未逾期过")) {
						type = WebConstants.CREDIT_RECORD_TYPE_HOUSING_LOAN_NORMAL;
						recordType = WebConstants.NORMAL_OVERDUE;
					}
				} else {
					loanRecordNo++;
					TPbccrcCreditRecord creditRecord = new TPbccrcCreditRecord();
					creditRecord.setContent(e.text());
					creditRecord.setType(type);
					report.addCreditRecord(creditRecord);
					if (creditRecord != null && StringUtils.isNotEmpty(creditRecord.getContent())) {
						TPbccrcLoanRecord loanRecord = getPbccrcContentForLoan("2", creditRecord.getContent());
						loanRecord.setRecordNum(Long.valueOf(loanRecordNo));
						loanRecord.setType(recordType);
						loanRecord.setLoanType(WebConstants.CREDIT.购房贷款.toString());
						loanRecord.setContent(e.text());
						report.addLoanRecord(loanRecord);
					}

				}
			}

			// 信贷记录-其他贷款
			Elements eOtherLoan = body.select("span[class=h1]:contains(其他贷款) + ol span, span[class=h1]:contains(其他贷款) + ol li");
			int otherRecordNo = 0;
			for (int i = 0; i < eOtherLoan.size(); i++) {
				Element e = eOtherLoan.get(i);
				if (e.tagName().equals("span")) {
					if (e.text().startsWith("发生过逾期")) {
						type = WebConstants.CREDIT_RECORD_TYPE_OTHER_LOAN_OVERDUE;
						recordType = WebConstants.OVERDUE;

					} else if (e.text().startsWith("从未逾期过")) {
						type = WebConstants.CREDIT_RECORD_TYPE_OTHER_LOAN_NORMAL;
						recordType = WebConstants.NORMAL_OVERDUE;
					}
				} else {
					otherRecordNo++;
					TPbccrcCreditRecord creditRecord = new TPbccrcCreditRecord();
					creditRecord.setContent(e.text());
					creditRecord.setType(type);
					report.addCreditRecord(creditRecord);
					if (creditRecord != null && StringUtils.isNotEmpty(creditRecord.getContent())) {
						TPbccrcLoanRecord loanRecord = getPbccrcContentForLoan("2", creditRecord.getContent());
						loanRecord.setRecordNum(Long.valueOf(otherRecordNo));
						loanRecord.setType(recordType);
						loanRecord.setLoanType(WebConstants.CREDIT.其他贷款.toString());
						loanRecord.setContent(e.text());
						report.addLoanRecord(loanRecord);
					}
				}
			}

		} catch (Exception ex) {
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw, true));
			errorMsg.append(sw.toString().length() > 1000 ? sw.toString().substring(0, 999) : sw.toString());
			LOGGER.error("央行征信报告字段拆分异常", ex);
		}
		report.setErrorMsg(errorMsg.toString());
		return report;
	}

	/**
	 * 信用卡信息详细字段解读
	 * 
	 * @param type
	 * @param content
	 * @return
	 */
	private static TPbccrcCreditCardRecord getPbccrcContentForCreditCard(String type, String content) {
		TPbccrcCreditCardRecord record = new TPbccrcCreditCardRecord();
		Map<String, Object> creditCardModelMap = (Map<String, Object>) InitDatas.getReportModelMap().get("CARDMODEL");
		if (creditCardModelMap != null) {
			Map<String, Object> resultMap = getContentToMap(creditCardModelMap, content);
			record.setGrantMoneyDate(resultMap.get("GRANT_MONEY_DATE") != null ? DateUtils.stringToDate(resultMap.get("GRANT_MONEY_DATE").toString(), "yyyy年MM月dd日") : null);
			record.setCurrencyType( resultMap.get("CURRENCY_TYPE") != null ? resultMap.get("CURRENCY_TYPE").toString() : null);
			record.setDeadlineDate(resultMap.get("DEADLINE_DATE") != null ? DateUtils.stringToDate(resultMap.get("DEADLINE_DATE").toString(), "yyyy年MM月") : null);
			record.setGrantOrganizations(resultMap.get("GRANT_ORGANIZATIONS") != null ? resultMap.get("GRANT_ORGANIZATIONS").toString() : null);
			String creditLimitMoney = null;
			if (resultMap.get("CREDIT_LIMIT_MONEY") != null) {
				if ((resultMap.get("CREDIT_LIMIT_MONEY").toString()).indexOf("币") > -1) {
					creditLimitMoney = resultMap.get("CREDIT_LIMIT_MONEY").toString().substring(resultMap.get("CREDIT_LIMIT_MONEY").toString().indexOf("币") + 1);
					record.setCreditLimitMoney(Arith.getStringToBigDecimal(creditLimitMoney));
				} else {
					record.setCreditLimitMoney(resultMap.get("CREDIT_LIMIT_MONEY") != null ? Arith.getStringToBigDecimal(resultMap.get("CREDIT_LIMIT_MONEY").toString()) : BigDecimal.ZERO);
				}
			} else {
				record.setCreditLimitMoney(BigDecimal.ZERO);
			}
			record.setAlreadyUseMoney(resultMap.get("ALREADY_USE_MONEY") != null ? Arith.getStringToBigDecimal(resultMap.get("ALREADY_USE_MONEY").toString()) : BigDecimal.ZERO);
			record.setOverdueMoney(resultMap.get("OVERDUE_MONEY") != null ? Arith.getStringToBigDecimal(resultMap.get("OVERDUE_MONEY").toString()) : BigDecimal.ZERO);
			record.setBad(resultMap.get("BAD") != null ? "0" : "1");
			record.setOverdueMonth(resultMap.get("OVERDUE_MONTH") != null ? Long.valueOf(resultMap.get("OVERDUE_MONTH").toString()) : 0L);
			record.setOverdueMonthNum(resultMap.get("OVERDUE_MONTH_NUM") != null ? Long.valueOf(resultMap.get("OVERDUE_MONTH_NUM").toString()) : 0L);
			record.setCreditCardSixty(resultMap.get("CREDIT_CARD_SIXTY") != null ? Long.valueOf(resultMap.get("CREDIT_CARD_SIXTY").toString()) : 0L);
			record.setCreditCardNinety(resultMap.get("CREDIT_CARD_NINETY") != null ? Long.valueOf(resultMap.get("CREDIT_CARD_NINETY").toString()) : 0L);
			record.setActive(resultMap.get("ACTIVE") != null ? "1" : "0");
			record.setOverdraftBalance(resultMap.get("OVERDRAFT_BALANCE") != null ? Arith.getStringToBigDecimal(resultMap.get("OVERDRAFT_BALANCE").toString()) : BigDecimal.ZERO);
			record.setCloseAccount(resultMap.get("CLOSE_ACCOUNT") != null ? "0" : "1");
			record.setCreditType(resultMap.get("CREDIT_TYPE") != null ? resultMap.get("CREDIT_TYPE").toString() : null);
		}
		return record;
	}

	/**
	 * 贷款信息详细字段解读
	 * 
	 * @param type
	 * @param content
	 * @return
	 */
	private static TPbccrcLoanRecord getPbccrcContentForLoan(String type, String content) {
		TPbccrcLoanRecord record = new TPbccrcLoanRecord();
		Map<String, Object> loanModelMap = (Map<String, Object>) InitDatas.getReportModelMap().get("LOANMODEL");
		if (loanModelMap != null) {
			Map<String, Object> resultMap = getContentToMap(loanModelMap, content);
			record.setGrantMoneyDate(resultMap.get("GRANT_DATE") != null ? DateUtils.stringToDate(resultMap.get("GRANT_DATE").toString(), "yyyy年MM月dd日") : null);
			record.setGrantOrganizations(resultMap.get("GRANT_ORGANIZATIONS") != null ? resultMap.get("GRANT_ORGANIZATIONS").toString() : null);
			record.setGrantMoney(resultMap.get("GRANT_MONEY") != null ? Arith.getStringToBigDecimal(resultMap.get("GRANT_MONEY").toString()) : BigDecimal.ZERO);
			record.setCurrencyType(resultMap.get("CURRENCY_TYPE") != null ? resultMap.get("CURRENCY_TYPE").toString() : null);
			record.setDeadlineDate(resultMap.get("DEADLINE_DATE") != null ? DateUtils.stringToDate(resultMap.get("DEADLINE_DATE").toString(), "yyyy年MM月") : null);
			record.setExpireDate(resultMap.get("EXPIRE_DATE") != null ? DateUtils.stringToDate(resultMap.get("EXPIRE_DATE").toString(), "yyyy年MM月dd日") : null);
			record.setSettlementDate(resultMap.get("SETTLEMENT_DATE") != null ? DateUtils.stringToDate(resultMap.get("SETTLEMENT_DATE").toString(), "yyyy年MM月") : null);
			record.setOverdueMoney(resultMap.get("OVERDUE_MONEY") != null ? Arith.getStringToBigDecimal(resultMap.get("OVERDUE_MONEY").toString()) : BigDecimal.ZERO);
			record.setBad(resultMap.get("BAD") != null ? "0" : "1");
			record.setOverdueMonth(resultMap.get("OVERDUE_MONTH") != null ? Long.valueOf(resultMap.get("OVERDUE_MONTH").toString()) : 0L);
			record.setOverdueMonthNum(resultMap.get("OVERDUE_MONTH_NUM") != null ? Long.valueOf(resultMap.get("OVERDUE_MONTH_NUM").toString()) : 0L);
			record.setSettlement(resultMap.get("SETTLEMENT") != null ? "0" : "1");
			record.setCreditType(resultMap.get("CREDIT_TYPE") != null ? resultMap.get("CREDIT_TYPE").toString() : null);
			record.setBalance(resultMap.get("BALANCE") != null ? Arith.getStringToBigDecimal(resultMap.get("BALANCE").toString()) : BigDecimal.ZERO);
			// 区分是银行还是机构发放的贷款
			if (WebConstants.BANK.浙江网商银行股份有限公司.toString().equals(record.getGrantOrganizations())
					|| WebConstants.BANK.深圳前海微众银行股份有限公司.toString().equals(record.getGrantOrganizations())) {
				record.setOrgType(WebConstants.ORGTYPE.网商银行.getCode());
			} else if (record.getGrantOrganizations().contains("银行")) {
				record.setOrgType(WebConstants.ORGTYPE.银行.getCode());
			} else if (record.getGrantOrganizations().contains("信用合作社") || record.getGrantOrganizations().contains("信用合作社联合社")
					|| record.getGrantOrganizations().contains("信用合作联社") || record.getGrantOrganizations().contains("信用社")) {
				record.setOrgType(WebConstants.ORGTYPE.信用社.getCode());
			} else if (record.getGrantOrganizations().contains(WebConstants.ORGTYPE.汽车金融公司.getValue())) {
				record.setOrgType(WebConstants.ORGTYPE.汽车金融公司.getCode());
			} else {
				record.setOrgType(WebConstants.ORGTYPE.公司机构.getCode());
			}
		}
		return record;
	}

	/**
	 * 模板解析公共类
	 * 
	 * @param modelList
	 * @param content
	 * @return
	 */
	private static Map<String, Object> getContentToMap(Map<String, Object> modelMap, String content) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		for (String key : modelMap.keySet()) {
			List<ReportModelValue> modelValueList = (List<ReportModelValue>) modelMap.get(key);
			if (CollectionUtils.isNotEmpty(modelValueList)) {
				for (ReportModelValue modelValue : modelValueList) {
					Pattern p = Pattern.compile(modelValue.getValue());
					Matcher m = p.matcher(content);
					ArrayList<String> strs = new ArrayList<String>();
					String[] valueIndex = modelValue.getValueIndex().split(":");
					while (m.find()) {
						strs.add(m.group(Integer.parseInt(valueIndex[0])));
					}
					if (CollectionUtils.isNotEmpty(strs)) {
						resultMap.put(key, strs.get(Integer.parseInt(valueIndex[1]) - 1));
						break;
					} else {
						if (modelValueList.iterator().next() == null) {
							LOGGER.error("未找到配置的模板");
						}
					}
				}
			}
		}
		return resultMap;
	}

}
