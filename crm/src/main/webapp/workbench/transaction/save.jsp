<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Set" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<%
	Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
	Set<String> set = pMap.keySet();


%>

<!DOCTYPE html>
<html>
<head>
    <base href="<%=basePath%>">
    <meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<script type="text/javascript" src="jquery/bs_typeahead/bootstrap3-typeahead.min.js"></script>
	<script type="text/javascript">

		var json ={

			<%
				for (String key:set){
					String value = pMap.get(key);


			%>
				"<%=key%>":<%=value%>,
			<%
				}
			%>



		};
		/*
			关于阶段和可能性
				是一种一一对应的关系
				一个阶段对应一个可能性
				我们现在可以将阶段和可能性想象成是一种键值对之间的对应关系
				以阶段为key，通过选中的阶段，触发可能性value
				stage               possibility
				key					value
				01资质审查			10
				02需求分析			25
				03价值建议			40
				...
				...
				07成交				100
				08..				0
				09..				0
				对于以上的数据，通过观察得到结论
				（1）数据量不是很大
				（2）这是一种键值对的对应关系

				如果同时满足以上两种结论，那么我们将这样的数据保存到数据库表中就没有什么意义了
				如果遇到这种情况，我们需要用到properties属性文件来进行保存
				stage2Possibility.properties
				01资质审查=10
				02需求分析=20
				....

				stage2Possibility.properties这个文件表示的是阶段和键值对之间的对应关系
				将来，我们通过stage，以及对应关系，来取得可能性这个值
				这种需求在交易模块中需要大量的使用到

				所以我们就需要将该文件解析在服务器缓存中
				application.setAttribute(stage2Possibility.properties文件内容)
		 */


		$(function () {
			//时间控件
			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});
			//查询并展示所有者
			$.ajax({
				url:"workbench/transaction/GetUserList.do",
				type:"get",
				success:function (data) {
					$("#create-transactionOwner").html("");
					var html="<option></option>";
					$.each(data,function (i,n) {
						html+='<option  value="'+n.id+'">'+n.name+'</option>';

					})

					$("#create-transactionOwner").html(html);

				}
			})
			//搜索市场活动源
			$("#searchMarketActivity").click(function () {
				$("#findMarketActivity").modal("show");
			})

			$("#findMarketActivity").keydown(function (event) {
				if (event.keyCode == 13) {
					var aname =$.trim($("#activityNameBtn").val());
					if (!aname){
						alert("请输入要搜索的内容")
						return false;
					}
					$.ajax({
						url:"workbench/transaction/GetMarketActivity.do",
						data:{
							"aname":aname
						},
						type:"get",
						dataType:"json",
						success:function (data) {
							var html ="";
							$.each(data,function (i,n) {
								html +='<tr>';
								html +='<td><input type="radio" name="xz" value="'+n.id+'"/></td>';
								html +='<td id="'+n.id+'">'+n.name+'</td>';
								html +='<td>'+n.startDate+'</td>';
								html +='<td>'+n.endDate+'</td>';
								html +='<td>'+n.owner+'</td>';
								html +='</tr>';
							})
							$("#activityResourceTbody").html(html);


						}
					})
					return false;
				}

				})
			//市场活动提交
			$("#submitActivity").click(function () {

				var $xz=$("input[name=xz]:checked").val();

				$("#create-activitySrc").val($("#"+$xz).html());
				//保存id到隐藏域中
				$("#create-activityId").val($xz);

			})
			//打开联系人查询事件
			$("#searchContacts").click(function () {
				$("#findContacts").modal("show");

			})
			//通过名字模糊查询联系人信息
			$("#findContacts").keydown(function (event) {
				if (event.keyCode == 13) {
					var cname =$.trim($("#findContactsByName").val());
					if (!cname){
						alert("请输入要搜索的内容")
						return false;
					}
					$.ajax({
						url:"workbench/transaction/GetContactsByName.do",
						data:{
							"cname":cname
						},
						type:"get",
						dataType:"json",
						success:function (data) {
							var html ="";
							$.each(data,function (i,n) {
								html+='<tr>';
								html+='<td><input type="radio" name="contacts" value="'+n.id+'"/></td>';
								html+='<td id="'+n.id+'">'+n.fullname+'</td>';
								html+='<td>'+n.email+'</td>';
								html+='<td>'+n.mphone+'</td>';
								html+='</tr>';
							})
							$("#findContactsTbody").html(html);


						}
					})
					return false;
				}
			})
			//联系人信息提交
			$("#submitContacts").click(function () {
				var $cid = $("input[name=contacts]:checked").val();

				$("#create-contactsName").val($("#"+$cid).html());
				//保存隐藏id信息
				$("#create-contactsId").val($cid)

			})
			//自动补全客户名称
			$("#create-customerName").typeahead({
				source: function (query, process) {
					$.get(
							"workbench/transaction/getCustomerName.do",
							{ "name" : query },
							function (data) {
								//alert(data);
								process(data);
							},
							"json"
					);
				},
				delay: 1500
			});

			$("#create-transactionStage").change(function () {
				/*

					我们现在以json.key的形式不能取得value
					因为今天的stage是一个可变的变量
					如果是这样的key，那么我们就不能以传统的json.key的形式来取值
					我们要使用的取值方式为
					json[key]


				 */
				var stage = $("#create-transactionStage").val();
				var possibility = json[stage];
				$("#create-possibility").val(possibility);

			})


			//新增交易提交
			$("#saveBtn").click(function () {
				$("#saveForm").submit();
			})


		})







	</script>
</head>
<body>

	<!-- 查找市场活动源 -->
	<div class="modal fade" id="findMarketActivity" role="dialog">
		<div class="modal-dialog" role="document" style="width: 80%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">查找市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" id="activityNameBtn" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable3" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
							</tr>
						</thead>
						<tbody id="activityResourceTbody">
						<%--	<tr>
								<td><input type="radio" name="activity"/></td>
								<td>发传单</td>
								<td>2020-10-10</td>
								<td>2020-10-20</td>
								<td>zhangsan</td>
							</tr>
							<tr>
								<td><input type="radio" name="activity"/></td>
								<td>发传单</td>
								<td>2020-10-10</td>
								<td>2020-10-20</td>
								<td>zhangsan</td>--%>
							</tr>
						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" id="submitActivity" class="btn btn-primary" data-dismiss="modal">提交</button>
				</div>
			</div>
		</div>
	</div>

	<!-- 查找联系人 -->	
	<div class="modal fade" id="findContacts" role="dialog">
		<div class="modal-dialog" role="document" style="width: 80%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">查找联系人</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" id="findContactsByName" style="width: 300px;" placeholder="请输入联系人名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>邮箱</td>
								<td>手机</td>
							</tr>
						</thead>
						<tbody id="findContactsTbody">
							<%--<tr>
								<td><input type="radio" name="activity"/></td>
								<td>李四</td>
								<td>lisi@bjpowernode.com</td>
								<td>12345678901</td>
							</tr>--%>
						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" id="submitContacts" class="btn btn-primary" data-dismiss="modal">提交</button>
				</div>
			</div>
		</div>
	</div>
	
	<%--创建主页面--%>
	<div style="position:  relative; left: 30px;">
		<h3>创建交易</h3>
	  	<div style="position: relative; top: -40px; left: 70%;">
			<button type="button" id="saveBtn" class="btn btn-primary">保存</button>
			<button type="button" id="exitBtn" class="btn btn-default">取消</button>
		</div>
		<hr style="position: relative; top: -40px;">
	</div>
	<form action="workbench/transaction/save.do" id="saveForm" class="form-horizontal" role="form" style="position: relative; top: -30px;">
		<div class="form-group">
			<label for="create-transactionOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" name="owner" id="create-transactionOwner">

				  <%--<option>zhangsan</option>
				  <option>lisi</option>
				  <option>wangwu</option>--%>
				</select>
			</div>
			<label for="create-amountOfMoney" class="col-sm-2 control-label">金额</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" name="money" class="form-control" id="create-amountOfMoney">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-transactionName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" name="name" class="form-control" id="create-transactionName">
			</div>
			<label for="create-expectedClosingDate" class="col-sm-2 control-label">预计成交日期<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" name="expectedDate" class="form-control time" id="create-expectedClosingDate">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-accountName" class="col-sm-2 control-label">客户名称<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" name="customerName" id="create-customerName" placeholder="支持自动补全，输入客户不存在则新建">

			</div>
			<label for="create-transactionStage" class="col-sm-2 control-label">阶段<span style="font-size: 15px; color: red;">*</span></label>
			<div class="col-sm-10" style="width: 300px;">
			  <select class="form-control" id="create-transactionStage" name="stage">
			  	<option></option>
				  <c:forEach items="${stagelist}" var="s">
					<option value="${s.value}">${s.text}</option>
				  </c:forEach>
			 <%-- 	<option>资质审查</option>
			  	<option>需求分析</option>
			  	<option>价值建议</option>
			  	<option>确定决策者</option>
			  	<option>提案/报价</option>
			  	<option>谈判/复审</option>
			  	<option>成交</option>
			  	<option>丢失的线索</option>
			  	<option>因竞争丢失关闭</option>--%>
			  </select>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-transactionType" class="col-sm-2 control-label">类型</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-transactionType" name="type">
				  <option></option>
					<c:forEach items="${transactionTypelist}" var="s">
						<option value="${s.value}">${s.text}</option>
					</c:forEach>
				</select>
			</div>
			<label for="create-possibility" class="col-sm-2 control-label">可能性</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-possibility">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-clueSource" class="col-sm-2 control-label">来源</label>
			<div class="col-sm-10" style="width: 300px;">
				<select class="form-control" id="create-clueSource" name="source">
				  <option></option>
					<c:forEach items="${sourcelist}" var="s">
						<option value="${s.value}">${s.text}</option>
					</c:forEach>
				</select>
			</div>
			<label for="create-activitySrc" class="col-sm-2 control-label">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" id="searchMarketActivity" ><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control" id="create-activitySrc">
				<input type="hidden" id="create-activityId" name="activityId">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-contactsName" class="col-sm-2 control-label">联系人名称&nbsp;&nbsp;<a href="javascript:void(0);" id="searchContacts" ><span class="glyphicon glyphicon-search"></span></a></label>
			<div class="col-sm-10" style="width: 300px;">

				<input type="text" class="form-control" id="create-contactsName">
				<input type="hidden" id="create-contactsId" name="contactsId">
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-describe" class="col-sm-2 control-label">描述</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="create-describe" name="description"></textarea>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-contactSummary" class="col-sm-2 control-label">联系纪要</label>
			<div class="col-sm-10" style="width: 70%;">
				<textarea class="form-control" rows="3" id="create-contactSummary" name="contactSummary"></textarea>
			</div>
		</div>
		
		<div class="form-group">
			<label for="create-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
			<div class="col-sm-10" style="width: 300px;">
				<input type="text" class="form-control time" id="create-nextContactTime" name="nextContactTime">
			</div>
		</div>
		
	</form>
</body>
</html>