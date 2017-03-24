package com.credit.controller;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.credit.common.ResponseEnum;
import com.credit.domain.TPbccrcCustomerHistory;
import com.credit.domain.TPbccrcReport;
import com.credit.domain.TPbccrcReportContent;
import com.credit.domain.TPbccrcReportQuerylog;
import com.credit.service.ICustomerHistoryService;
import com.credit.service.IReportAnalyzeService;
import com.credit.service.ITPbccrcCreditRecordService;
import com.credit.service.ITPbccrcReportService;
import com.credit.service.PbccrcService;
import com.credit.util.DateUtils;
import com.credit.vo.ResponseInfo;
/**
 * 央行报告相关查询入口
 * @author fuhongxing
 */
@Controller
@RequestMapping("/creditReport")
public class CreditReportController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CreditReportController.class);
	
	@Autowired
	private ITPbccrcCreditRecordService iTPbccrcCreditRecordService;
	@Autowired
	private PbccrcService pbccrcService;
	@Autowired 
	private ICustomerHistoryService iCustomerHistoryService;
	@Autowired
	private IReportAnalyzeService reportAnalyzeService;
	@Autowired
	private ITPbccrcReportService tpbccrcReportService;
	/**
	 * 跳转获取报告页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getReport")
	public String getReport(HttpServletRequest request){
		return "getReport";
	}
	
	/**
	 * 跳转上传报告页面
	 */
	@RequestMapping(value = "/upload")
	public ModelAndView skipTest(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/pbccrc/upload");
		return modelAndView;
	}
	
	/**
	 * 上传征信报告
	 * @param file
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/uploadReport")
	@ResponseBody
	public String uploadReport(@RequestParam(value = "htmlUrl", required = false) MultipartFile file,HttpServletRequest request, ModelMap modelMap){
		String customerName = request.getParameter("customerName");
		String customerIdCard = request.getParameter("customerIdCard");
		String creatorId = request.getParameter("creatorId");
		ResponseInfo responseInfo = new ResponseInfo(ResponseEnum.SYS_SUCCESS);
		JSONObject json = new JSONObject();
		json.put("customerName", customerName);
		json.put("customerIdCard", customerIdCard);
		json.put("creatorId", creatorId);
		//防止7天内重复上传
		TPbccrcReportQuerylog queryLog = pbccrcService.getPbccrcReportQueryLogByCustomerIdCardAndCustomerName(null, customerIdCard, null);
		if(queryLog!=null){
			//验证身份证是否唯一(20161222取消验证)
			/*if(queryLog.getCustomerIdCard().equals(customerIdCard) && !queryLog.getCustomerName().equals(customerName)){
				LOGGER.warn("身份证与姓名不匹配,姓名对比:APP[{}],报告[{}] 身份证对比:APP[{}],报告[{}]", customerName, queryLog.getCustomerName(), customerIdCard, queryLog.getCustomerIdCard());
				responseInfo.setResMsg(ResponseEnum.PBCCRC_REPORT_IDCARD_ERROR.getDesc());
				responseInfo.setResCode(ResponseEnum.PBCCRC_REPORT_IDCARD_ERROR.getCode());
				json.put("message", JSON.toJSON(responseInfo));
				return json.toJSONString();
			}*/
			/*int day = Dates.daysOfTwo(Dates.getCurrDate(), queryLog.getLastUpdateTime());
			if(day < 7){
				LOGGER.warn("7天内不允许重复上传征信报告...");
				responseInfo = new ResponseInfo(ResponseEnum.PBCCRC_REPORT_QUERYLOG_EXIST, "");
				modelMap.addAttribute("msg", responseInfo.getResMsg()); 
				return "/pbccrc/result";
			}*/
		}
		
		try {
			//File targetFile = new File(fileName);  
			Document doc = Jsoup.parse(file.getInputStream(),"gbk", "");
			if (doc != null) {
				TPbccrcReport report = reportAnalyzeService.getPbccrcContent(doc.html().toString());
				report.setCustomerIdCard(customerIdCard);
				report.setCustomerName(customerName);
				report.setCreateTime(new Date());
				report.setCreatorId(creatorId);
				//截取身份证后四位
				String customerIdCardLast = StringUtils.isNotEmpty(report.getCustomerIdCard()) && report.getCustomerIdCard().length() > 4 ? report.getCustomerIdCard().substring(report.getCustomerIdCard().length() - 4) : "";
				String certNoLast = StringUtils.isNotEmpty(report.getCertNo()) && report.getCustomerIdCard().length() > 4 ? report.getCertNo().substring(report.getCertNo().length() - 4) : "";
				//判断用户信息是否与征信报告中的用户信息匹配
				if (!report.getCustomerName().equals(report.getName()) || !certNoLast.equals(customerIdCardLast)) {
					LOGGER.info("用户信息不匹配，身份证[{}] 姓名对比:APP[{}],报告[{}]后四位对比:APP[{}],报告[{}]", customerIdCard, report.getCustomerName(), report.getName(),customerIdCardLast, certNoLast);
					responseInfo.setResMsg(ResponseEnum.PBCCRC_REPORT_MESSAGE_ERROR.getDesc());
					responseInfo.setResCode(ResponseEnum.PBCCRC_REPORT_MESSAGE_ERROR.getCode());
					json.put("message", JSON.toJSON(responseInfo));
					return json.toJSONString();
				}
				
				pbccrcService.savePbccrcReport(report, doc.html().toString());
				responseInfo.setReportId(report.getId());
				pbccrcService.syncSavePbccrcReportCardAndLoan(report, doc.html().toString());
				if (StringUtils.isNotEmpty(report.getErrorMsg())) {
					LOGGER.error(report.getErrorMsg());
					responseInfo.setResMsg(ResponseEnum.PBCCRC_REPORT_ANALYSE_ERROR.getDesc());
					responseInfo.setResCode(ResponseEnum.PBCCRC_REPORT_ANALYSE_ERROR.getCode());
				}
			}
		} catch (IOException e) {
			LOGGER.error("用户名[{}],身份证[{}] 征信报告上传异常!!!",customerName, customerIdCard, e);
		}
		json.put("message", JSON.toJSON(responseInfo));
		return json.toJSONString();
	}
	
	/**
	 * 查询征信信用不良判断信息
	 * @return
	 */
	@RequestMapping(value = "/getCreditReportInfo", method = RequestMethod.POST)
	@ResponseBody
	public String getCreditReportInfo(String param, HttpServletRequest request){
		LOGGER.info("========征审系统查询用户央行征信报告信用不良信息=========" + param);
		JSONObject json = checkParam(param);
		if(json.containsKey("messages")){
			return json.toJSONString();
		}
		Map<String,Object> parameters = (Map<String,Object>)JSON.parse(param);
		String result = JSON.toJSONString(iTPbccrcCreditRecordService.checkRule(parameters));
		LOGGER.info("rules result==" + result);
		return result;
	}
	
	/**
	 * 征信报告下载
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/downLoadReport/{reportId}", method = RequestMethod.POST)
	@ResponseBody
	public void downLoadReport( @PathVariable ("reportId") Long reportId, HttpServletRequest request, HttpServletResponse response){
		LOGGER.info("征信报告下载:" + reportId);
		TPbccrcReportContent reportContent = null;
		if (reportId != null) {
			reportContent = pbccrcService.getPbccrcReportContent(reportId);
			String path = request.getSession().getServletContext().getRealPath("/")+File.separator+reportId+".html";
			try {
				LOGGER.info(path);
//				FileUtils.writeStringToFile(new File(path), reportContent.getContent(),"gbk", true);
				InputStream is = new ByteArrayInputStream(reportContent.getContent().getBytes("gbk"));
//				FileDownUtils.write(is, request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	/**
	 * APP获取央行征信信用判断信息
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getCreditInfo", method = RequestMethod.POST)
	@ResponseBody
	public String getCreditInfo(String param, HttpServletRequest request){
		LOGGER.info("========APP查询用户央行征信报告信用不良信息=========" + param);
		JSONObject json = checkParam(param);
		if(json.containsKey("messages")){
			return json.toJSONString();
		}
		Map<String,Object> parameters = (Map<String,Object>)JSON.parse(param);
		String result = iTPbccrcCreditRecordService.getCreditInfo(parameters);
		LOGGER.info("APP获取央行征信信用判断信息结果==[{}]", result);
		return result;
	}
	
	/**
	 * 获取用户的报告id
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getReportId", method = RequestMethod.POST)
	@ResponseBody
	public String getReportId(String param, HttpServletRequest request){
		LOGGER.info("========征审系统查询查询用户央行征信报告ID=========" + param);
		JSONObject json = null;
		try {
			if(StringUtils.isEmpty(param)){
				JSONObject js = new JSONObject();
				js.put("code", "999999");
				js.put("messages", "请求缺少必要参数");
				return js.toJSONString();
			}
			json = JSONObject.parseObject(param);
			if(!json.containsKey("customerIdCard") || !json.containsKey("customerName")){
				json.put("messages", "请求缺少必要参数");
				return JSON.toJSONString(json);
			}
			List<String> message = new ArrayList<String>();
			if(StringUtils.isEmpty(json.getString("customerIdCard"))){
				message.add("身份证不能为空");
			}
			if(StringUtils.isEmpty(json.getString("customerName"))){
				message.add("姓名不能为空");
			}
			if(!message.isEmpty()){
				json.put("messages", message);
				return json.toJSONString();
			}
			Map<String,Object> parameters = (Map<String,Object>)JSON.parse(param);
			TPbccrcCustomerHistory customer = iCustomerHistoryService.getCustomerHistoryByCustomerNameAndCustomerIdCard(json.getString("customerName"), json.getString("customerIdCard"), null);
			if(customer == null){
				LOGGER.info("用户名[{}],身份证[{}] 无用户征信报告记录!!!", json.getString("customerName"), json.getString("customerIdCard"));
				json.put("reportId", "");
				json.put("code", "000001");
				json.put("messages", "无用户征信报告记录");
				return json.toJSONString();
			}
			json.put("code", "000000");
			TPbccrcReport tPbccrcReport = pbccrcService.getPbccrcReport(customer.getReportId());
			//判断是否大于15天过期(首次录入复核时间-央行报告查询时间)
			int day = DateUtils.daysOfTwo(DateUtils.stringToDate(json.getString("queryDate").toString(), "yyyy-MM-dd"), tPbccrcReport.getReportTime());
			if(day > 15){
				json.put("messages", "用户报告已过期");
				json.put("code", "000002");
				json.put("reportId", "");
				LOGGER.warn("用户名[{}],身份证[{}] 用户报告已过期!!!", json.getString("customerName"), json.getString("customerIdCard"));
			}else{
				json.put("messages", "");
				json.put("reportId", customer.getReportId());
			}
		} catch (Exception e) {
			json.put("code", "999999");
			json.put("messages", "获取用户报告ID异常");
			LOGGER.error("用户名[{}],身份证[{}] 征审系统获取报告id异常!!!", json.getString("customerName"), json.getString("customerIdCard"), e);
		}
		LOGGER.info("查询报告id返回结果：{}", json.toJSONString());
		return json.toJSONString();
	}
	
	/**
	 * 查询用户信用额度(征审额度指引)
	 * @return
	 */
	@RequestMapping(value = "/getCreditLimit", method = RequestMethod.POST)
	@ResponseBody
	public String getCreditLimit(String param, HttpServletRequest request){
		LOGGER.info("========征审系统查询查询用户额度指引信息=========" + param);
		JSONObject json = checkParam(param);
		if(json.containsKey("messages")){
			return json.toJSONString();
		}
		Map<String,Object> parameters = (Map<String,Object>)JSON.parse(param);
		
		String result = iTPbccrcCreditRecordService.getCreditLimit(parameters);
		LOGGER.info("额度指引返回结果：" + result);
		return result;
	}
	
	/**
	 * 获取贷款额度(征审贷款自动填充接口使用)
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getLoanLimitInfo", method = RequestMethod.POST)
	@ResponseBody
	public String getLoanLimitInfo(String param, HttpServletRequest request){
		LOGGER.info("========征审系统查询查询贷款额度信息=========" + param);
		JSONObject json = checkParam(param);
		if(json.containsKey("messages")){
			return json.toJSONString();
		}
		Map<String,Object> parameters = (Map<String,Object>)JSON.parse(param);
		
		String result = iTPbccrcCreditRecordService.findLoanLimitInfo(parameters);
		LOGGER.info("贷款额度信息返回结果：{}", result);
		return result;
	}
	/**
	 * 修复未拆分的征信报告数据 
	 * @param param
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/repairCredit", method = RequestMethod.POST)
	@ResponseBody
	public String repairCredit(HttpServletRequest request){
		LOGGER.info("========征信报告老数据拆分修复 =========");
		//查询报告
		int currentPage = 1;
		int pageSize = 100;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<TPbccrcReport> list = null;
		boolean flag = true;
		while (flag) {
			list = tpbccrcReportService.findReportInfo(null, currentPage, pageSize);
			if(CollectionUtils.isEmpty(list)){
				flag = false;
			}else{
				for (TPbccrcReport report : list) {
					paramMap.put("reportId", report.getId());
					paramMap.put("queryDate", report.getReportTime());
					try {
						iTPbccrcCreditRecordService.checkRule(paramMap);
					} catch (Exception e) {
						LOGGER.error("老数据修复异常, 报告id:{}!!!", report.getId(), e);
					}
				}
				currentPage++;
			}
		}
		return null;
	}
	
	
	/**
	 * 验证请求参数
	 * @param param
	 * @return
	 */
	public JSONObject checkParam(String param){
		JSONObject json = null;
		List<String> message = new ArrayList<String>();
		try {
			json = JSONObject.parseObject(param);
			if(StringUtils.isEmpty(param) || !json.containsKey("customerIdCard") || !json.containsKey("customerName") || !json.containsKey("timestamp")){
				json.put("code", "999999");
				json.put("messages", "请求缺少必要参数");
				return json;
			}
			//先验证请求时间再验证其他参数，防止重复大量请求进来
			if(StringUtils.isEmpty(json.getString("timestamp"))){
				message.add("请求时间不能为空");
				json.put("code", "999999");
				json.put("messages", Arrays.toString(message.toArray()));
				return json;
			}
			
			//时间戳用来验证请求是否过期
			long date = Math.abs(System.currentTimeMillis() - Long.parseLong(json.getString("timestamp")));
			if (date >= 60000) {
				// 时间差超过60秒
				Date reqDate = new Date(Long.parseLong(json.getString("timestamp")));
				message.add("请求时间与当前时间差过大");
				LOGGER.info("当前时间:[{}],请求时间:[{}],时间差[{}]分钟", DateUtils.getCurrenDate(), DateUtils.format(reqDate, DateUtils.FORMAT_DATE_YYYY_MM_DD),date/1000/60);
				json.put("code", "000003");
				json.put("messages", Arrays.toString(message.toArray()));
				return json;
			}
			
			if(StringUtils.isEmpty(json.getString("customerIdCard"))){
				message.add("身份证不能为空");
			}
			if(StringUtils.isEmpty(json.getString("customerName"))){
				message.add("姓名不能为空");
			}
			if(StringUtils.isEmpty(json.getString("queryDate"))){
				message.add("录入复核时间不能为空");
			}
			
			
			if(message.size() > 0){
				json.put("code", "999999");
				json.put("messages", Arrays.toString(message.toArray()));
			}
		} catch (Exception e) {
			LOGGER.error("解析请求参数异常", e);
			message.clear();
			message.add("参数验证异常");
			json = new JSONObject();
			json.put("code", "999999");
			json.put("messages", Arrays.toString(message.toArray()));
		}
		return json;
	}
	
}
