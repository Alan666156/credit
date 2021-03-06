function post(api, params, dataType, callback, error) {
	var timestamp = Date.parse(new Date());
	api = api + "?timestamp=" + timestamp;
	$.ajax({
		url : api,
		dataType : dataType,
		method : 'post',
		data : params,
		success : callback,
		error : error
	});
}

function load(api) {
	var timestamp = Date.parse(new Date());
	api = api + "?timestamp=" + timestamp;
	$.ajax({
		url : api,
		success : function(result) {
			if(result.indexOf('登录页面')>=0){
				window.location.href = '/';
			}else{
				$('.main').html(result);
			}
		}
	});
}
function onSuccess(event, xhr, settings) {
	if(xhr.responseText.indexOf('登录页面')>=0){
		window.location.href = '/';
	}
}

$(document).ajaxSuccess(onSuccess);



/**格式当前时间 带时分秒 yyyy-MM-dd HH:mm:ss*/
function getLocalTime(milliseconds) {
	var datetime = new Date();
	datetime.setTime(milliseconds);
	var year = datetime.getFullYear();
	// 月份重0开始，所以要加1，当小于10月时，为了显示2位的月份，所以补0
	var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1)
			: datetime.getMonth() + 1;
	var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime
			.getDate();
	var hour = datetime.getHours() < 10 ? "0" + datetime.getHours() : datetime
			.getHours();
	var minute = datetime.getMinutes() < 10 ? "0" + datetime.getMinutes()
			: datetime.getMinutes();
	var second = datetime.getSeconds() < 10 ? "0" + datetime.getSeconds()
			: datetime.getSeconds();
	return year + "-" + month + "-" + date + " " + hour + ":" + minute + ":"
			+ second;
}

/** 时间戳=>本地时间 */
function getLocalTimeDate(milliseconds) {
	var datetime = new Date();
	datetime.setTime(milliseconds);
	var year = datetime.getFullYear();
	// 月份重0开始，所以要加1，当小于10月时，为了显示2位的月份，所以补0
	var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1)
			: datetime.getMonth() + 1;
	var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime
			.getDate();
	var hour = datetime.getHours() < 10 ? "0" + datetime.getHours() : datetime
			.getHours();
	var minute = datetime.getMinutes() < 10 ? "0" + datetime.getMinutes()
			: datetime.getMinutes();
	var second = datetime.getSeconds() < 10 ? "0" + datetime.getSeconds()
			: datetime.getSeconds();
	return year + "-" + month + "-" + date;
}


//对象空值判断
function isNotNull(obj){
	if(obj == "" || obj == null || obj == undefined){
		return false;
	}else{
		return true;
	}
}

/**
 * 日历控件
 * 
 */

//清空时间
$(".date-clear").click(function() {
	var id = $(this).attr("id");
	$('.'+id).val("");
});
//结束时间不能大于开始时间
$('.endDate').datetimepicker().on('changeDate', function(ev){
	var begin;
	var end = new Date($('.endDate').val()).getTime();
	if(isNotNull($('.beginDate').val())){
		begin = new Date($('.beginDate').val()).getTime();
	}
    if (begin > end){
    	$('.endDate').val('');
        alert("结束时间不能小于开始时间");
    }
});

function checkDateTime(){
	  var beginDate = $('.beginDate').val();
	  var endDate = $('.endDate').val();
	  if(!isNotNull(beginDate) && !isNotNull(endDate)){
		  return true;
	  }
	  
	  if(!isNotNull(beginDate) || !isNotNull(endDate)){
		  if(isNotNull(beginDate) && !isNotNull(endDate)){
//			   alert("请选择结束时间");
			   BootstrapDialog.show({
           		title: '提示信息',
   	            message: "请选择结束时间"
   	     	  });
			   return false;
		   }
		   if(isNotNull(endDate) && !isNotNull(beginDate)){
//			   alert("请选择开始时间");
			   BootstrapDialog.show({
	           		title: '提示信息',
	   	            message: "请选择开始时间"
	   	     	  });
			   return false;
		   }
	  }
	  
	  if(isNotNull(beginDate) && isNotNull(endDate)){
		  var date1 = new Date(beginDate).getTime();
		  var date2= new Date(endDate).getTime();
		  if( date1 > date2 ){
//			  alert("开始时间不能大于结束日期");
			  BootstrapDialog.show({
	           		title: '提示信息',
	   	            message: "开始时间不能大于结束日期"
	   	     	  });
			  return false;
		  } 
	  }
}

$(function(){
	initData();
	$('.input-date').datetimepicker({
	    language:  'zh-CN',
	    format: 'yyyy-mm-dd',
	    autoclose:true,
	    todayHighlight: 1,
		minView: 2,
	    endDate:new Date() //设置当前时间后的日期不能选择
	});
	
	/* $(".query").click(function() {
	  var id = $(this).attr("id");
	 var beginDate = $('.beginDate').val();
	  var endDate = $('.endDate').val();
	  if(!isNotNull(beginDate) || !isNotNull(endDate)){
		  if(isNotNull(beginDate) && !isNotNull(endDate)){
			   alert("请选择结束时间");
			   return false;
		   }
		   if(isNotNull(endDate) && !isNotNull(beginDate)){
			   alert("请选择开始时间");
			   return false;
		   }
	  }
	  
	  if(isNotNull(beginDate) && isNotNull(endDate)){
		  var date1 = new Date(beginDate).getTime();
		  var date2= new Date(endDate).getTime();
		  if( date1 > date2 ){
			  alert("开始时间不能大于结束日期");
			  return false;
		  } 
	  }*/
	  //编号必须为正整数 bug # 5388
	  /*var userId = $("#userId").val();
	  if(isNotNull(userId) && !/^(\d+)$/.test(userId) ){
		  alert("请输入有效的数字");
		  return false;
	  }
	  var mobile = $("#mobile").val();
	  if(isNotNull(mobile) && !/^1\d{10}$/.test(mobile)){
		  alert("请输入有效的电话号码");
		  return false;
	  }
	  //查询
	 $('.'+id).dataTable().fnDestroy();//销毁
	 initData();
	});*/
})