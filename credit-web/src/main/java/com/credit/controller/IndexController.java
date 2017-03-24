package com.credit.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.credit.domain.TPbccrcReport;
import com.credit.service.ITPbccrcReportService;
import com.credit.util.DateUtils;
import com.credit.vo.PageTables;
import com.github.pagehelper.Page;

/**
 * Created by fuhongxing on 16/11/27.
 */
@Controller
public class IndexController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);
	
	@Autowired
	private ITPbccrcReportService tPbccrcReportService;
	
	@RequestMapping(value = "/")
	public String index(HttpServletRequest request){
		return "index";
	}
	
	/**
	 * 跳转用户管理页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/customerList")
	public String customerList(HttpServletRequest request){
		return "customerList";
	}
	
	/**
	 * 重置缓存数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/resetCacheData")
	@ResponseBody
	public String resetCacheData(HttpServletRequest request){
//		LOGGER.info("========重新加载缓存数据========");
//		Message<String> message = new Message<String>(Message.Type.SUCCESS, "重置数据成功");
//		try {
//			//重新加载央行报告解读正则模板
//			initDataListener.init();
//		} catch (Exception e) {
//			message.setType(Message.Type.FAILURE);
//			message.addMessage("重置缓存数据异常");
//			LOGGER.error("重置缓存数据异常!", e);
//		}
//		return JSON.toJSONString(message);
		return null;
	}
	
	/**
	 * 查询用户信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getUserInfo")
	@ResponseBody
	public PageTables<TPbccrcReport> getUserInfo(HttpServletRequest request, String customerName, String customerIdCard, String beginDate,String endDate, Integer draw, Integer start, Integer length){
		start = start == 0 ? 0+1 : (start/length)+1;
		LOGGER.info("========加载用户数据,当前页{}========",start);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(customerName)){
			paramMap.put("customerName", "%"+customerName+"%");
		}
		if(!StringUtils.isEmpty(customerIdCard)){
			paramMap.put("customerIdCard", "%"+customerIdCard+"%");
		}
		if(!StringUtils.isEmpty(beginDate) && !StringUtils.isEmpty(endDate)){
			paramMap.put("beginDate", DateUtils.stringToDate(beginDate, null));
			paramMap.put("endDate", DateUtils.addDate(DateUtils.stringToDate(endDate, null), 1));
		}
		List<TPbccrcReport> list = tPbccrcReportService.findReportInfo(paramMap, start, length);
//		List<TPbccrcCustomerHistory> list = customerHistoryService.findCustomerInfo(paramMap, start == 0 ? 0+1 : (start/length)+1, length);
		PageTables<TPbccrcReport> data = new PageTables<TPbccrcReport>();
		Long count = ((Page<TPbccrcReport>) list).getTotal();
		LOGGER.info("size:"+ count);
		data.setData(list);
		data.setDraw(draw);
		data.setRecordsTotal(count);
		data.setRecordsFiltered(count);
		return data;
	}
}
