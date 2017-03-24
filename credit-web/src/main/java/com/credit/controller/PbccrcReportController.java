package com.credit.controller;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.credit.common.ResponseEnum;
import com.credit.domain.TPbccrcCustomerHistory;
import com.credit.domain.TPbccrcReport;
import com.credit.domain.TPbccrcReportContent;
import com.credit.domain.TPbccrcReportQuerylog;
import com.credit.service.IReportAnalyzeService;
import com.credit.service.PbccrcService;
import com.credit.util.DateUtils;
import com.credit.vo.AttachmentResponseInfo;
import com.credit.vo.ResponseInfo;

@Controller
@RequestMapping(value = "/pbccrc")
public class PbccrcReportController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PbccrcReportController.class);

	@Autowired
	private PbccrcService pbccrcService;
//	@Autowired
//	private ICustomerHistoryService customerHistoryService;
	@Autowired
	private IReportAnalyzeService reportAnalyzeService;
	/**
	 * APP上传征信报告
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/saveReport")
	@ResponseBody
	public String saveReport(HttpServletRequest request, ModelMap modelMap) throws UnsupportedEncodingException {
		LOGGER.info("==========展业APP上传用户央行征信报告 , 姓名[{}],身份证[{}] 操作人[{}]============",request.getParameter("customerName"), request.getParameter("customerIdCard"), request.getParameter("creatorId"));
		ResponseInfo responseInfo=null;
		String flowId = null;
		try {
			String customerName = request.getParameter("customerName");
			String customerIdCard = request.getParameter("customerIdCard");
			String htmlContent = request.getParameter("htmlContent");
			String creatorId = request.getParameter("creatorId");
			flowId = request.getParameter("flowId");
			responseInfo = new ResponseInfo(ResponseEnum.SYS_SUCCESS, flowId);
			LOGGER.debug("报告html：" + htmlContent);
			//防止7天内重复上传
			TPbccrcReportQuerylog queryLog = pbccrcService.getPbccrcReportQueryLogByCustomerIdCardAndCustomerName(null, customerIdCard, null);
			if(queryLog != null){
				//验证身份证是否唯一
				if(queryLog.getCustomerIdCard().equals(customerIdCard) && !queryLog.getCustomerName().equals(customerName)){
					LOGGER.error("姓名[{}],身份证[{}] 身份证与姓名不匹配!!!", customerName, customerIdCard);
					responseInfo = new ResponseInfo(ResponseEnum.PBCCRC_REPORT_IDCARD_ERROR, flowId);
					return JSON.toJSONString(responseInfo);
				}
				//判断规则：当前时间与最新的报告入库时间对比--->最新规则：当前时间-最新报告的报告时间>7
				TPbccrcReport rep = pbccrcService.getPbccrcReport(queryLog.getReportId());
				int day = DateUtils.daysOfTwo(DateUtils.getCurrenDate(), rep.getReportTime());
				if(day < 7){
					LOGGER.error("姓名[{}],身份证[{}] 7天内不允许重复上传征信报告...", customerName, customerIdCard);
					responseInfo = new ResponseInfo(ResponseEnum.PBCCRC_REPORT_QUERYLOG_EXIST, flowId);
					return JSON.toJSONString(responseInfo);
				}
			}
			
			TPbccrcReport report = reportAnalyzeService.getPbccrcContent(htmlContent);
			report.setCustomerIdCard(customerIdCard);
			report.setCustomerName(customerName);
			report.setCreateTime(new Date());
			report.setCreatorId(creatorId);
			//截取身份证后四位
			String customerIdCardLast = StringUtils.isNotEmpty(report.getCustomerIdCard()) && report.getCustomerIdCard().length()>4 ? report.getCustomerIdCard().substring(report.getCustomerIdCard().length()-4) : "";
			String certNoLast = StringUtils.isNotEmpty(report.getCertNo()) && report.getCustomerIdCard().length()>4 ? report.getCertNo().substring(report.getCertNo().length()-4) : "";
			//判断用户信息是否与征信报告中的用户信息匹配
			if(!report.getCustomerName().equals(report.getName()) || !certNoLast.equals(customerIdCardLast)){
				LOGGER.info("用户信息不匹配，身份证[{}] 后四位对比:APP[{}],报告[{}]", customerIdCard, customerIdCardLast, certNoLast);
				responseInfo = new ResponseInfo(ResponseEnum.PBCCRC_REPORT_MESSAGE_ERROR, flowId);
				return JSON.toJSONString(responseInfo);
			}
			if(StringUtils.isNotEmpty(report.getErrorMsg())){
				LOGGER.error("报告解析异常："+report.getErrorMsg());
				responseInfo = new ResponseInfo(ResponseEnum.PBCCRC_REPORT_ANALYSE_ERROR, flowId);
			}
			//保存报告
			pbccrcService.savePbccrcReport(report, htmlContent);
			responseInfo.setReportId(report.getId());
			//异步拆分
			pbccrcService.syncSavePbccrcReportCardAndLoan(report, htmlContent);
		} catch (Exception e) {
			LOGGER.error("姓名[{}],身份证[{}] 上传征信报告异常!!!", request.getParameter("customerName"), request.getParameter("customerIdCard"), e);
			responseInfo = new ResponseInfo(ResponseEnum.PBCCRC_REPORT_ERROR, flowId);
		} finally {
		}
		LOGGER.info("app上传报告返回结果=={}",JSON.toJSONString(responseInfo));
		return JSON.toJSONString(responseInfo);
	}
	
	/**
	 * APP查询用户报告
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/getReportHtmlContent")
	@ResponseBody
	public String getReportHtmlContent(HttpServletRequest request, ModelMap modelMap) throws UnsupportedEncodingException {
		LOGGER.info("=======APP查询用户报告======= 身份证[{}],姓名[{}]",request.getParameter("customerIdCard"),request.getParameter("customerName"));
		String customerName = request.getParameter("customerName");
		String customerIdCard = request.getParameter("customerIdCard");
		String flowId = request.getParameter("flowId");
		String userCode = request.getParameter("userCode");
		TPbccrcCustomerHistory customerHistory = new TPbccrcCustomerHistory();
		customerHistory.setCustomerIdCard(customerIdCard);
		customerHistory.setCustomerName(customerName);
		customerHistory.setQueryDate(new Date());
		customerHistory.setOperator(userCode);
		AttachmentResponseInfo<Object> responseInfo = null;
		
		try{
			TPbccrcReportQuerylog reportQuerylog = pbccrcService.getPbccrcReportQueryLogByCustomerIdCardAndCustomerName(customerName, customerIdCard,null);
			if(reportQuerylog == null){
				responseInfo = new AttachmentResponseInfo<Object>(ResponseEnum.PBCCRC_REPORT_QUERYLOG_ERROR, flowId);
				LOGGER.error("用户名[{}],身份证[{}] 未获取报告，请通过手机客户端获取", customerName, customerIdCard);
				return JSON.toJSONString(responseInfo);
			}
			TPbccrcReport report = pbccrcService.getPbccrcReport(reportQuerylog.getReportId());
			TPbccrcReportContent reportContent = null;
			if (report != null) {
				reportContent = pbccrcService.getPbccrcReportContent(report.getId());
			}
			if (report != null && reportContent != null) {
				if (report.getCreateTime() != null) {
					//报告时间与当前时间对比是否大于7天
					int day = DateUtils.daysOfTwo(DateUtils.getCurrenDate(), report.getReportTime());
					if(day > 7){
						LOGGER.error("用户名[{}],身份证[{}] APP查询报告时间已超过7天...", customerName, customerIdCard);
						responseInfo = new AttachmentResponseInfo<Object>(ResponseEnum.PBCCRC_REPORT_EXPIRED, flowId);
					} else {
						responseInfo = new AttachmentResponseInfo<Object>(ResponseEnum.SYS_SUCCESS, flowId);
						Map<String, Object> result = new HashMap<String, Object>();
						result.put("reportId", report.getId());
						result.put("htmlContent", reportContent.getContent());
						responseInfo.setAttachment(result);
					}
				} else {
					responseInfo = new AttachmentResponseInfo<Object>(ResponseEnum.PBCCRC_REPORT_DATA_ERROR, flowId);
				}
			} else {
				responseInfo = new AttachmentResponseInfo<Object>(ResponseEnum.PBCCRC_REPORT_NOT_EXIST, flowId);
			}
		}catch(Exception e){
			LOGGER.error("APP查询用户报告异常!!!", e);
			responseInfo = new AttachmentResponseInfo<Object>(ResponseEnum.SYS_FAILD, flowId);
		}finally{
		}
		LOGGER.info("APP查询报告返回结果==reportId{}, resCode{}, resMsg{}",responseInfo.getReportId(), responseInfo.getResCode(),responseInfo.getResMsg());
		return JSON.toJSONString(responseInfo);
	}
	
	/**
	 * 查询显示报告html(测试时使用)
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/getReportContent")
	public String getReportContent(HttpServletRequest request, ModelMap modelMap) {
		LOGGER.info("=====获取央行征信报告html页面=====");
		String code = "$1$QfXSqZLn$/Akkf";
		if(StringUtils.isNotEmpty(request.getParameter("code")) && code.equals(request.getParameter("code"))){
			this.reportView(request, modelMap);
		}else{
			modelMap.put("htmlContent", "查询码不正确");
		}
		return "/pbccrc/reportView";
	}
	
	/**
	 * 征审系统获取报告内容html
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/reportView")
	public String reportView(HttpServletRequest request, ModelMap modelMap) {
		LOGGER.info("=====征审请求获取征信报告html页面=====");
		LOGGER.info("身份证[{}],姓名[{}],录入复核时间[{}],查询系统[{}]",request.getParameter("customerIdCard"),request.getParameter("customerName"),request.getParameter("queryDate"),request.getParameter("sources"));
		String idStr = request.getParameter("reportId");//报告id
		String customerName = null;
		
		try {
			if(StringUtils.isNotEmpty(request.getParameter("customerName"))){
				customerName = URLDecoder.decode(request.getParameter("customerName"),"UTF-8");
			}
			String customerIdCard = request.getParameter("customerIdCard");
			String queryDate = request.getParameter("queryDate");
			Long id = null;
			TPbccrcReport report = null;
			TPbccrcReportQuerylog reportQuerylog =null;
			//如果传入的报告id不为空，按照报告id查询
			if (StringUtils.isNotEmpty(idStr)) {
				id = Long.parseLong(idStr);
			}else if(StringUtils.isNotEmpty(customerName) && StringUtils.isNotEmpty(customerIdCard)){
				reportQuerylog = pbccrcService.getPbccrcReportQueryLogByCustomerIdCardAndCustomerName(customerName, customerIdCard,null);
				if(reportQuerylog == null){
					modelMap.put("htmlContent", "未获取报告，请通过手机客户端获取");
					LOGGER.warn("用户名[{}],身份证[{}] 未获取报告，请通过手机客户端获取...", customerName, customerIdCard);
					return "/pbccrc/reportView";
				}
				id = reportQuerylog.getReportId();
			}else{
				modelMap.put("htmlContent", "【征信报告id】和【客户姓名+身份证】不能都为空");
				return "/pbccrc/reportView";
			}
			report = pbccrcService.getPbccrcReport(id);
			
			if (report != null) {
				LOGGER.info("获取报告html...");
				TPbccrcReportContent reportContent = pbccrcService.getPbccrcReportContent(report.getId());
				if(reportContent!=null){
					if(report.getCreateTime() != null){
						//根据报告id查询时不进行过期判断
						//判断是否大于15天过期(首次录入复核时间-央行报告查询时间)
						LOGGER.info("判断是否过期,央行报告时间[{}]",report.getReportTime());
						int day = DateUtils.daysOfTwo(DateUtils.stringToDate(queryDate.toString(), "yyyy-MM-dd"), report.getReportTime());
						if(StringUtils.isEmpty(idStr) && day > 15){
							LOGGER.warn("用户名[{}],身份证[{}] 该客户的征信报告已过期...", customerName, customerIdCard);
							modelMap.put("htmlContent", "该客户的征信报告已过期");
						}else{
							String htmlContent = reportContent.getContent();
							if (StringUtils.isNotEmpty(htmlContent)) {
								Document doc = Jsoup.parse(htmlContent, "", Parser.xmlParser());
								Element body = doc.body();
								modelMap.put("htmlContent", body.html());
							}else {
								modelMap.put("htmlContent", "征信报告没有数据，请联系系统管理员");
							}
						}
					}
				}else {
					modelMap.put("htmlContent", "征信报告不存在");
				}
			}else {
				modelMap.put("htmlContent", "征信报告不存在");
			}
		} catch (Exception e) {
			LOGGER.error("获取报告html异常!!!", e);
			modelMap.put("htmlContent", "征信报告获取异常!");
		}finally {
		}
		return "reportView";
	}
}
