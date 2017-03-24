package com.credit.util;


import java.util.HashMap;
import java.util.Map;

public class InitDatas {
	private static Map<String,Object> reportModelMap = new HashMap<String, Object>();

	public static Map<String, Object> getReportModelMap() {
		return reportModelMap;
	}

	public static void setReportModelMap(Map<String, Object> reportModelMap) {
		InitDatas.reportModelMap = reportModelMap;
	}
	
}
