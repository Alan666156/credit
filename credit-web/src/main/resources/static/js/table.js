/**
 * 本对象只是对jquery datatable公共属性进行提取，渐少引用页面代码量
 */
var MyDataTable = {
		createNew:function() {
			var myDataTable = {};
			// 国际化配置
			myDataTable.getOLanguage = function() {
				return {
					"sLengthMenu" : "每页显示 _MENU_ 条记录 ",
					"sZeroRecords" : "抱歉， 没有找到",
					"sInfo" : "从 _START_ 到 _END_ /共 _TOTAL_ 条数据",
					"sInfoEmpty" : "",
					"sInfoFiltered" : "(从 _MAX_ 条数据中检索)",
					"sProcessing" : "正在加载中......",
					"sSearch" : "搜索:",
					"oPaginate" : {
						"sFirst" : "首页",
						"sPrevious" : "前一页",
						"sNext" : "后一页",
						"sLast" : "尾页",
					},
					"sZeroRecords" : "没有检索到数据",
					"sProcessing" : "<img src='images/loading.gif' />"//加载图片 
				};
			},
			
			myDataTable.getRender = function() {
				return function(data, type, row) {
					if (data != null) {
						return data;
					} else {
						return "";
					}
				};
			},
			
			// 获取yyyy-MM-dd HH:mm:ss格式日期
			myDataTable.getRenderOfDateYMDHMS = function() {
				return function(data, type, row) {
					if (data != null) {
						return getLocalTime(data);
					} else {
						return "";
					}
				};
			},

			myDataTable.getRenderOfDateYMD = function() {
				return function(data, type, row) {
					if (data != null) {
						return getLocalTimeDate(data);
					} else {
						return "";
					}
				};
			}
			
			return myDataTable;
		}
}
$(function() {
	var myDataTable  = MyDataTable.createNew();
	window.myDataTable = myDataTable;
});