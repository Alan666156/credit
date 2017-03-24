<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="${ctx}/css/jquery.dataTables.min.css" rel="stylesheet">
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.0/css/bootstrap.min.css">
<link rel="stylesheet" href="${ctx}/css/bootstrap-datetimepicker.min.css">
<link rel="stylesheet" href="//cdn.bootcss.com/bootstrap3-dialog/1.35.3/css/bootstrap-dialog.min.css">

<!-- 可选的Bootstrap主题文件（一般不用引入） -->
<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.0/css/bootstrap-theme.min.css">

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="http://cdn.bootcss.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<script src="//cdn.bootcss.com/bootstrap3-dialog/1.35.3/js/bootstrap-dialog.min.js"></script>
<script src="${ctx}/js/bootstrap-datetimepicker.min.js"></script>
<script src="${ctx}/js/bootstrap-datetimepicker.zh-CN.js"></script>

<script src="${ctx}/js/jquery.dataTables.min.js"></script>
<script src="${ctx}/js/fnProcessingIndicator.js"></script>
<script src="${ctx}/js/dateTable-Tools.js" type="text/javascript"></script>
<script src="${ctx}/js/common.js"></script>

<style>
   table>thead:first-child>tr:first-child>th{text-align:center;}
   table tr td{text-align: center;}
   table thead tr th{text-align: center;}
   table tr td{text-align: center;word-break: keep-all;white-space: nowrap;}
   table thead tr th{text-align: center;word-break: keep-all;white-space: nowrap;}
   #im{float: left;margin-left: 30px;}
	#education{line-height: 15px;}
	.modal-dialog{z-index: 9999;}
</style>
<title>央行征信信息</title>
</head>

<body>
	<h1>央行征信信息</h1>
	<form class="form-inline" role="form">
	  <div class="form-group">
	    <div class="input-group">
	      <div class="input-group-addon">姓名：</div>
	      <input class="form-control" type="text" placeholder="姓名" name='customerName' id='customerName'>
	    </div>
	  </div>
	  <div class="form-group">
	    <div class="input-group">
	      <div class="input-group-addon">身份证：</div>
	      <input class="form-control" type="text" placeholder="身份证" name='customerIdCard' id='customerIdCard'>
	    </div>
	  </div>
	  <div class="form-group">
	    <div class="input-group">
	      <div class="input-group-addon">统计日期：</div>
	      <input type="text" class="form-control beginDate input-date" placeholder="开始日期" name="beginDate" readonly>
		  <a class="icon-remove date-clear" id="beginDate" href="#"></a>
	    </div>
	  </div>
	  <div class="form-group">
	    <div class="input-group">
	      <input type="text" class="form-control endDate input-date" placeholder="结束日期" name="endDate" readonly>
		  <a class="icon-remove date-clear" id="endDate" href="#"></a>
	    </div>
	  </div>
	  <button type="button" class="btn btn-success query" id='userTab'>查 询</button>
	  <button type="button" class="btn btn-success" id='upload'>上传征信报告</button>
	</form>
							  
							  
	<hr>
	<div id='content'>
		
	</div>
	
	<div class="portlet-body" style="overflow-x: scroll; height: 100%;">
		<table class="dataTable striped border bordered hovered cell-hovered display userTab TBstyle">
            <thead>
            <tr>
                <th>序号</th>
                <th>姓名</th>
                <th>证件类型</th>
                <th>证件号</th>
                <th>婚姻状况</th>
                <th>报告id</th>
                <th>创建人</th>
                <th>创建时间</th>
                <th>查询时间</th>
                <th>报告时间</th>
                <th>操作</th>
            </tr>
            </thead>			
            <tbody></tbody>
	</table>
	</div>
	
	<img src="" style="" class='photo'/>
	<script type="text/javascript">
	
		//华征学历
		function getEducationInfo(id,customerName, customerIdCard, queryDate, flag){
			$.ajax({
	            type: "post",
	            url: "${ctx}/hz/education/"+customerIdCard,
	//	            data: {customerName:$("#username").val(), customerIdCard:$("#idCard").val(),timestamp:1473237195962},
	            data: {customerName:customerName},
	            dataType: "json",
	            success: function(data){
	            	var result = $.parseJSON(data.data);
	            	$("img").attr("src",result.photo);
	            	BootstrapDialog.show({
	            		title: '学历信息',
	    	            message: '<div id="im"><img src="'+result.photo+'" alt="毕业照片" style="width: 180px;height: 230px;"></div>'+
	    	            '<div id="education">'+
		    	        	'<form class="form-horizontal" role="form" >'+
			    	      	  '<div class="form-group">'+
			    	      	    '<label class="col-sm-3 control-label">姓  名：</label>'+
			    	      	  	'<p class="form-control-static">'+result.name+'</p>'+
			    	      	  '<label class="col-sm-3 control-label">身份证：</label>'+
			    	      	  	'<p class="form-control-static">'+result.idCard+'</p>'+
			    	      	  '<label class="col-sm-3 control-label">毕业学院：</label>'+
			    	      	  	'<p class="form-control-static">'+result.graduate+'</p>'+
			    	      	  '<label class="col-sm-3 control-label">专  业：</label>'+
			    	      	  	'<p class="form-control-static">'+result.specialityName+'</p>'+
			    	      	  '<label class="col-sm-3 control-label">学历类型：</label>'+
			    	      	  	'<p class="form-control-static">'+result.educationDegree+'</p>'+
			    	      	  '<label class="col-sm-3 control-label">毕业结论：</label>'+
			    	      	  	'<p class="form-control-static">'+result.studyResult+'</p>'+
			    	      	  '</div>'+
			    	      	'</form>'+
	    	      	   '</div>'
	    	     	});
	            }
	        });
		}
		
		//华征手机在网时长
		function getMobileOnlineInfo(id,customerName, customerIdCard, queryDate, flag){
			$.ajax({
	            type: "post",
	            url: "${ctx}/hz/mobileOnline/"+customerIdCard,
	//	            data: {customerName:$("#username").val(), customerIdCard:$("#idCard").val(),timestamp:1473237195962},
	            data: {customerName:customerName},
	            dataType: "json",
	            success: function(data){
	            	var result = $.parseJSON(data.data);
	            	BootstrapDialog.show({
	            		title: '手机在网时长信息',
	    	            message: 
	    	            '<div id="education">'+
		    	        	'<form class="form-horizontal" role="form" >'+
			    	      	  '<div class="form-group">'+
			    	      	    '<label class="col-sm-3 control-label">姓  名：</label>'+
			    	      	  	'<p class="form-control-static">'+result.name+'</p>'+
			    	      	  '<label class="col-sm-3 control-label">身份证：</label>'+
			    	      	  	'<p class="form-control-static">'+result.idCard+'</p>'+
			    	      	  '<label class="col-sm-3 control-label">手机号：</label>'+
			    	      	  	'<p class="form-control-static">'+result.phone+'</p>'+
			    	      	  '<label class="col-sm-3 control-label">在网时长：</label>'+
			    	      	  	'<p class="form-control-static">'+result.times+'</p>'+
			    	      	  '</div>'+
			    	      	'</form>'+
	    	      	   '</div>'
	    	     	});
	            }
	        });
		}
		
		//华征实名认证
		function getIdCardCheckInfo(id,customerName, customerIdCard, queryDate, flag){
			$.ajax({
	            type: "post",
	            url: "${ctx}/hz/idCardCheckInfo/"+customerIdCard,
	//	            data: {customerName:$("#username").val(), customerIdCard:$("#idCard").val(),timestamp:1473237195962},
	            data: {customerName:customerName},
	            dataType: "json",
	            success: function(data){
// 	            	console.info(data);
	            	var result = $.parseJSON(data.data);
	            	BootstrapDialog.show({
	            		title: '实名认证信息',
	    	            message: 
	    	            '<div id="education">'+
		    	        	'<form class="form-horizontal" role="form" >'+
				    	      	'<div class="form-group">'+
				    	      	    '<label class="col-sm-3 control-label">姓  名：</label>'+
				    	      	  	'<p class="form-control-static">'+result.userName+'</p>'+
				    	      	    '<label class="col-sm-3 control-label">身份证：</label>'+
				    	      	  	'<p class="form-control-static">'+result.userId+'</p>'+
				    	      	    '<label class="col-sm-3 control-label">手机号：</label>'+
				    	      	  	'<p class="form-control-static">'+result.phone+'</p>'+
				    	      	    '<label class="col-sm-3 control-label">认证结果：</label>'+
			    	      	  		'<p class="form-control-static">'+result.succeed+'</p>'+
				    	      	    '<label class="col-sm-3 control-label">认证信息：</label>'+
			    	      	  		'<p class="form-control-static">'+result.msg+'</p>'+
				    	      	  '</div>'+
			    	      	'</form>'+
	    	      	   	'</div>'
	    	     	});
	            }
	        });
		}
		
		//华征车辆查询
		function getCarInfo(id,customerName, customerIdCard, queryDate, flag){
			$.ajax({
	            type: "post",
	            url: "${ctx}/hz/car/"+customerIdCard,
	//	            data: {customerName:$("#username").val(), customerIdCard:$("#idCard").val(),timestamp:1473237195962},
	            data: {customerName:customerName},
	            dataType: "json",
	            success: function(data){
// 	            	console.info(data);
	            	var result = $.parseJSON(data.data);
	            	BootstrapDialog.show({
	            		title: '车辆信息',
	    	            message: 
	    	            '<div id="education">'+
		    	        	'<form class="form-horizontal" role="form" >'+
				    	      	'<div class="form-group">'+
				    	      	    '<label class="col-sm-3 control-label">姓  名：</label>'+
				    	      	  	'<p class="form-control-static">'+result.name+'</p>'+
				    	      	    '<label class="col-sm-3 control-label">身份证：</label>'+
				    	      	  	'<p class="form-control-static">'+result.idCard+'</p>'+
				    	      	    '<label class="col-sm-3 control-label">车辆识别号：</label>'+
				    	      	  	'<p class="form-control-static">'+result.carNumber+'</p>'+
				    	      	    '<label class="col-sm-3 control-label">厂家：</label>'+
			    	      	  		'<p class="form-control-static">'+result.factory+'</p>'+
				    	      	    '<label class="col-sm-3 control-label">品牌：</label>'+
			    	      	  		'<p class="form-control-static">'+result.brands+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">车系：</label>'+
			    	      	  		'<p class="form-control-static">'+result.carSeries+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">车型：</label>'+
			    	      	  		'<p class="form-control-static">'+result.vehicleModel+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">车辆类型：</label>'+
			    	      	  		'<p class="form-control-static">'+result.vehicleType+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">车辆级别：</label>'+
			    	      	  		'<p class="form-control-static">'+result.vehicleLevel+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">销售名称：</label>'+
			    	      	  		'<p class="form-control-static">'+result.marketName+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">年款：</label>'+
			    	      	  		'<p class="form-control-static">'+result.yearModel+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">指导价格：</label>'+
			    	      	  		'<p class="form-control-static">'+result.guidancePrice+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">生产年份：</label>'+
			    	      	  		'<p class="form-control-static">'+result.productiveYear+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">上市年份：</label>'+
			    	      	  		'<p class="form-control-static">'+result.listingYear+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">上市月份：</label>'+
			    	      	  		'<p class="form-control-static">'+result.listingMonth+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">排放标准：</label>'+
			    	      	  		'<p class="form-control-static">'+result.emissionStandards+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">汽车排量：</label>'+
			    	      	  		'<p class="form-control-static">'+result.emissions+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">变速箱类型：</label>'+
			    	      	  		'<p class="form-control-static">'+result.gearboxType+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">VIN对应的年份：</label>'+
			    	      	  		'<p class="form-control-static">'+result.vinYear+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">发动机型号：</label>'+
			    	      	  		'<p class="form-control-static">'+result.engineType+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">变速器类型：</label>'+
			    	      	  		'<p class="form-control-static">'+result.transmissionType+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">挡位数：</label>'+
			    	      	  		'<p class="form-control-static">'+result.numberGear+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">车型代码：</label>'+
			    	      	  		'<p class="form-control-static">'+result.vehicleModelNum+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">VIN对应的年份：</label>'+
			    	      	  		'<p class="form-control-static">'+result.vinYear+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">车身形式：</label>'+
			    	      	  		'<p class="form-control-static">'+result.carBodyType+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">车门数：</label>'+
			    	      	  		'<p class="form-control-static">'+result.doorNumber+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">座位数：</label>'+
			    	      	  		'<p class="form-control-static">'+result.seating+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">发动机最大功率：</label>'+
			    	      	  		'<p class="form-control-static">'+result.maximumPower+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">燃油类型：</label>'+
			    	      	  		'<p class="form-control-static">'+result.fuelType+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">燃油编号：</label>'+
			    	      	  		'<p class="form-control-static">'+result.fuelNum+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">驱动方式：</label>'+
			    	      	  		'<p class="form-control-static">'+result.driveMode+'</p>'+
				    	      	  	'<label class="col-sm-3 control-label">发动机缸数：</label>'+
			    	      	  		'<p class="form-control-static">'+result.numberEngineCylinder+'</p>'+
				    	      	  '</div>'+
			    	      	'</form>'+
	    	      	   	'</div>'
	    	     	});
	            }
	        });
		}
		//当表格渲染完成后，添加鼠标点击海报展现图片预览的事件
// 		$(".userTab").on( 'draw.dt', function () {
// 	            $('[data-toggle="popover"]').popover({
// 	                html:true,
// 	                trigger:'hover',
// 	                placement : 'top',
// 	                container: 'body',
// 	                delay : {show: 500, hide: 1000},
// 	                content: function () {
// 	                	BootstrapDialog.show({
// 		            		title: '提示信息',
// 		    	            message: JSON.stringify(data)
// 		    	     	});
// 	                }
// 	            });
// 	        });
		
		//获取贷款额度
		function getLoanLimitInfo(id,customerName,customerIdCard,queryDate,flag){
			var url ="${ctx}/creditReport/getLoanLimitInfo";
			
			var obj = new Object();
			obj.customerName=customerName;
			obj.customerIdCard=customerIdCard;
			if(isNotNull(id)){
				obj.reportId=id;
			}
			obj.queryDate=getLocalTime(new Date(queryDate));
			obj.timestamp=new Date().getTime();
			$.ajax({
	            type: "post",
	            url: url,
//		            data: {customerName:$("#username").val(), customerIdCard:$("#idCard").val(),timestamp:1473237195962},
	            data: {param:JSON.stringify(obj)},
	            dataType: "json",
	            success: function(data){
	            	var html='';
	            	if(data.code=='000000' && isNotNull(data.loan)){
// 	            		html='<div class="container">';
		            	for(var i=0;i < data.loan.length;i++){
		            		var result = data.loan[i];
		            		html+='<div class="row">'+
// 				    	      	'<div class="col-md-2">序  号：'+result.no+'</div>'+
				    	      	'<div class="col-md-4">'+result.no+'.额  度：'+result.grantMoney+'</div>'+
				    	      	'<div class="col-md-4">负  债：'+result.debtMoney+'</div>'+
				    	      	'<div class="col-md-4">期  数：'+result.repayPeriods+'</div>'+
// 				    	      	'<div class="col-md-3">已还期数：'+result.alreadyRepayPeriods+'</div>'+
				    	      	'</div>';
				    	     
		            	}
// 		            	html+="</div>";
					}else{
						html=JSON.stringify(data);
					}
	            	
	            	BootstrapDialog.show({
	            		title: '贷款额度信息',
	    	            message: html
	    	            
	    	     	 });
// 	            	return JSON.stringify(data);
	            }
	        });
			
			
		}
		//信用不良信息
		function getCredit(id,customerName,customerIdCard,queryDate,flag){
			var url ="${ctx}/creditReport/getCreditReportInfo";
			//app获取信用不良结果
			if(flag==1){
				console.info(flag);
				url ="${ctx}/creditReport/getCreditInfo";
			}
			
			var obj = new Object();
			obj.customerName=customerName;
			obj.customerIdCard=customerIdCard;
			if(isNotNull(id)){
				obj.reportId=id;
			}
			obj.queryDate=getLocalTime(new Date(queryDate));
			obj.timestamp=new Date().getTime();
			$.ajax({
	            type: "post",
	            url: url,
//		            data: {customerName:$("#username").val(), customerIdCard:$("#idCard").val(),timestamp:1473237195962},
	            data: {param:JSON.stringify(obj)},
	            dataType: "json",
	            success: function(data){
// 	            	$('.modal-dialog').dialog({modal:true});
	            	var html='';
	            	if(data.code=='000000'){
// 	            		html='<div class="container">';
		            	html+='<div class="row">'+
				    	      	'<div class="col-md-6">是否符合无综合信用：'+data.type+'</div>'+
				    	      	'<div class="col-md-6">贷记卡负债总额度：'+data.creditLimitMoney+'</div>'+
				    	      	'<div class="col-md-6">贷记卡负债已使用总额度：'+data.alreadyUseMoney+'</div>'+
				    	      	'<div class="col-md-6">近三个月查询次数：'+data.threeMonthqueryCount+'</div>'+
				    	      	'<div class="col-md-6">近一个月查询次数：'+data.oneMonthqueryCount+'</div>'+
				    	      	'<div class="col-md-6">信用判断结果：'+data.messages+'</div>'+
				    	      	'</div>';
// 		            	html+="</div>";
					}else{
						html=JSON.stringify(data);
					}
	            	
	            	BootstrapDialog.show({
	            		title: '提示信息',
	    	            message: html
	    	     	 });
// 	            	return JSON.stringify(data);
	            }
	        });
		}
		
		
		//查看报告
		function getReportInfo(reportId,customerName,customerIdCard,queryDate,obj){
			window.open('${ctx}/pbccrc/reportView?reportId='+reportId+"&customerName="+customerName+"&customerIdCard="+customerIdCard+"&queryDate="+getLocalTime(new Date(queryDate)));
		}
		//上传征信报告
		$("#upload").click(function(){
			window.location = "${ctx}/creditReport/upload";
		});
		//查询
		$(".query").click(function() {
			var id = $(this).attr("id");
			var flag = checkDateTime();
			if(flag!=null && !flag){
				return false;
			}
			console.info(id);
			$('.'+id).dataTable().fnDestroy();//销毁
			initData();
		});
		
		
		//监听回车事件，按下回车时去查询
		/* $("#query").keypress(function(e) {
			console.info(e.keyCode);
			if (e.keyCode == 13) {
				$('#userList').dataTable().fnDestroy();//销毁
				initData();//加载数据
			}
		}); */
		
		//额度指引
		function creditLimit(id,customerName,customerIdCard,queryDate,obj){
			
// 			<a href="#" tabindex="0" class="btn btn-lg btn-danger" role="button" data-toggle="popover" data-trigger="focus" title="Dismissible popover" data-content="And here's some amazing content. It's very engaging. Right?">Dismissible popover</a>
			var obj = new Object();
			obj.customerName=customerName;
			obj.customerIdCard=customerIdCard;
			if(isNotNull(id)){
				obj.reportId=id;
			}
			obj.queryDate=getLocalTime(new Date(queryDate));
			obj.timestamp=new Date().getTime();
			$.ajax({
	            type: "post",
	            url: "${ctx}/creditReport/getCreditLimit",
//		            data: {customerName:$("#username").val(), customerIdCard:$("#idCard").val(),timestamp:1473237195962},
	            data: {param:JSON.stringify(obj)},
	            dataType: "json",
	            success: function(data){
	            	var html='';
	            	if(isNotNull(data) && data.code=='000000'){
		            	 html='<div class="row">'+
		    	      	'<div class="col-md-6">近一个月查询次数：'+data.oneMonthqueryCount+'</div>'+
		    	      	'<div class="col-md-6">近三个月查询次数：'+data.threeMonthqueryCount+'</div>'+
		    	      	'<div class="col-md-6">信用卡逾期总次数：'+data.cardCount+'</div>'+
		    	      	'<div class="col-md-6">贷款逾期总次数：'+data.loanCount+'</div>'+
		    	      	'<div class="col-md-6">是否有90天以上逾期：'+data.type+'</div>'+
		    	      	'<div class="col-md-6">状态码：'+data.code+'</div>'+
		    	      	'</div>';
	            	}else{
	            		html=JSON.stringify(data);
	            	}
	            	 BootstrapDialog.show({
	            		title: '提示信息',
	     	            message: html
	     	     	 });
// 	            	 alert(JSON.stringify(data));
	            }
	        });
		}
	        
		$(function() {
// 			initData();
		});
		//初始化table数据
		function initData(){
			var i = 0;//序号
			var temp = 1;//记录次数
			var customerName=$("#customerName").val();
			var customerIdCard=$("#customerIdCard").val();
			var beginDate = $('.beginDate').val();
			var endDate = $('.endDate').val();
			$('.userTab').DataTable({
		    	"oLanguage": {
		    		"sLengthMenu": "每页显示 _MENU_ 条记录",
		    		"sZeroRecords": "抱歉， 没有找到",
		    		"sInfo": "从 _START_ 到 _END_ /共 _TOTAL_ 条数据",
		    		"sInfoEmpty": "",
		    		"sInfoFiltered": "(从 _MAX_ 条数据中检索)",
		    		"sProcessing": "正在加载中......",
		    		"sSearch": "搜索:",
		    		"oPaginate": {
		    		"sFirst": "首页",
		    		"sPrevious": "上一页",
		    		"sNext": "下一页",
		    		"sLast": "尾页",
		    		},
		    	"sZeroRecords": "没有检索到数据",
		     	"sProcessing": "<img src='${ctx}/resources/images/loading.gif' />"//加载图片
		    	},
		    	"stateSave": false, //状态保存 - 再次加载页面时还原表格状态
		    	"pagingType":"full_numbers", //分页样式
		    	"bFilter":false,//开启搜索过滤
		    	"data-searching":false,//开启搜索
		    	"bAutoWidth": false, //自动宽度
		    	"bPaginate": true, //是否显示分页工具
		    	"ordering": false, //禁用排序功能
		    	"aLengthMenu": [[10, 20, 30], [10, 20, 30]],//定义每页显示数据数量
		    	"processing": true,//是否显示处理状态(排序的时候，数据很多耗费时间长的话，也会显示这个)
		        "serverSide": true,//是否开启服务器模式
// 		    	"ajax": "${ctx}/getUserInfo",
		    	"ajax" : {
					"url" : "${ctx}/getUserInfo",
					"data":{customerName:customerName,customerIdCard:customerIdCard,beginDate:beginDate,endDate:endDate},
					"type" : "POST",
					"dataType" : 'json',
					//"success": fnCallback,  
					"timeout" : 15000, // optional if you want to handle timeouts (which you should)  
					"error" : handleAjaxError
				// this sets up jQuery to give me errors
				},
		    	"columns": [
		            { "data": "", 
						"render" : function(data, type, row ,start) {
							var begin = start.settings._iDisplayStart;//开始记录数
							var len = start.settings._iDisplayLength;//每页显示条数
							var total = start.settings._iRecordsTotal;//总记录数
							if(i==0){
								i = 1+begin;
								temp++;
							}else if(temp == len){
								var idx =i+1;
								i = 0;
								temp = 1;
								return idx;
							}else{
								i++;
								temp++;
							}
							
							if(i == total){//最后一条数据
								i = 0;
								temp = 1;	
								return total;
							}
							
							return i;
						}
					},
					{ "data": "customerName"},
					{ "data": "certType"},
		            { "data": "customerIdCard" },
		            { "data": "maritalStatus" },
					{ "data": "id"},
					{ "data": "creatorId"},
					{ "data": "createTime", //创建时间
						//格式化时间
						"render" : function(data, type, row) {
							if (data != null) {
								return getLocalTime(new Date(data));
							} else {
								return "";
							}
						}
					},
					{ "data": "queryTime", //查询时间
						//格式化时间
						"render" : function(data, type, row) {
							if (data != null) {
								return getLocalTime(new Date(data));
							} else {
								return "";
							}
						}
					},
					{ "data": "reportTime",//报告生成时间
						"render" : function(data, type, row) {
							if (data!=null) {
								return getLocalTime(new Date(data));
							} else {
								return "";
							}
						}						
					},
					{ "data": "option", 
						"render" : function(data, type, row) {
							return '<a href="#" onclick=getReportInfo('+row.id+',\''+ row.customerName+'\''+',\''+ row.customerIdCard+'\''+','+ row.queryTime+',this)>查看报告</a>'+
							'  <a href="#" onclick=creditLimit('+row.id+',\''+ row.customerName+'\''+',\''+ row.customerIdCard+'\''+','+ row.queryTime+',this)>额度指引</a>'+
							'  <a href="#" class="creditCheck" data-toggle="popover" data-trigger="focus" onclick=getCredit('+row.id+',\''+ row.customerName+'\''+',\''+ row.customerIdCard+'\''+','+ row.queryTime+','+0+')>信用判断</a>'+
							'  <a href="#" class="creditCheck" data-toggle="popover" data-trigger="focus" onclick=getLoanLimitInfo('+row.id+',\''+ row.customerName+'\''+',\''+ row.customerIdCard+'\''+','+ row.queryTime+',this)>贷款额度</a>'+
							'  <a href="#" class="creditCheck" data-toggle="popover" data-trigger="focus" onclick=getCredit('+row.id+',\''+ row.customerName+'\''+',\''+ row.customerIdCard+'\''+','+ row.queryTime+','+1+')>APP信用判断</a>'+
							'  <a href="#" class="creditCheck" data-toggle="popover" data-trigger="focus" onclick=getEducationInfo('+row.id+',\''+ row.customerName+'\''+',\''+ row.customerIdCard+'\''+','+ row.queryTime+','+1+')>学历</a>'+
							'  <a href="#" class="creditCheck" data-toggle="popover" data-trigger="focus" onclick=getMobileOnlineInfo('+row.id+',\''+ row.customerName+'\''+',\''+ row.customerIdCard+'\''+','+ row.queryTime+','+1+')>在网时长</a>'+
							'  <a href="#" class="creditCheck" data-toggle="popover" data-trigger="focus" onclick=getCarInfo('+row.id+',\''+ row.customerName+'\''+',\''+ row.customerIdCard+'\''+','+ row.queryTime+','+1+')>车辆</a>'+
							'  <a href="#" class="creditCheck" data-toggle="popover" data-trigger="focus" onclick=getIdCardCheckInfo('+row.id+',\''+ row.customerName+'\''+',\''+ row.customerIdCard+'\''+','+ row.queryTime+','+1+')>实名认证</a>';
						}
					}
		        ],
			"retrieve": true
		    });
		}
		
	</script>
</body>
</html>