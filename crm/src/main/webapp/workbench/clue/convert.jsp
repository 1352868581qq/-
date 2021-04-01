<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";

//?id=${clue.id}&fullname=${clue.fullname}&company=${clue.company}&owner=${clue.owner}
String id = request.getParameter("id");
String fullname = request.getParameter("fullname");
String company = request.getParameter("company");
String owner = request.getParameter("owner");

%>

<!DOCTYPE html>
<html>
<head>
    <base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>


<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<script type="text/javascript">
	$(function(){

		//时间控件
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		$("#isCreateTransaction").click(function(){
			if(this.checked){
				$("#create-transaction2").show(200);
			}else{
				$("#create-transaction2").hide(200);
			}
		});


		//通过“搜索”打开市场活动的模态窗口
		$("#openActivityModal").click(function () {
			$("#searchActivityModal").modal("show");

		})
		//搜索市场活动
		$("#searchActivityModal").keydown(function (event) {
			if (event.keyCode == 13) {
				var aname =$.trim($("#aname").val());
				if (!aname){
					alert("请输入要搜索的内容")
					return false;
				}
				$.ajax({
					url:"workbench/clue/SearchActivityByaname.do",
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
						$("#ActivityModalTboty").html(html);


					}
				})
				return false;
			}

		})
		//提交市场活动
		$("#submitActivity").click(function () {
			/*取得选中市场活动的id*/
			var $xz = $("input[name=xz]:checked");
			var id = $xz.val();
			//取得选中市场活动的名字
			var name =$("#"+id).html();
			$("#activity").val(name);
			$("#activityIdBtn").val(id);
			$("#searchActivityModal").modal("hide");

		})
		//转换线索
		$("#convertClue").click(function () {
			/*
				提交请求到后台，执行线索转换的操作，应该发出传统请求
				请求结束后，最终响应回线索列表页
				根据"为客户创建交易"的复选框有没有挑√，来判断是否需要创建交易
			 */
			if ($("#isCreateTransaction").prop("checked")){
				//alert("需要创建交易");
				//如果需要创建交易，除了要为后台传递clueId之外，还得为后台传递交易表单中的信息，金额，预计成交日期，交易名称，阶段，市场活动源（id）
				//window.location.href = "workbench/clue/convert.do?clueId=xxx&money=xxx&expectedDate=xxx&name=xxx&stage=xxx&activityId=xxx";
				//以上传递参数的方式很麻烦，而且表单一旦扩充，挂载的参数有可能超出浏览器地址栏的上限
				//我们想到使用提交交易表单的形式来发出本次的传统请求
				//提交表单的参数不用我们手动去挂载（表单中写name属性），提交表单能够提交post请求
				//提交表单
				$("#tranForm").submit();
				alert("转换成功");
			}else {
				//alert("不需要创建交易");
				//在不需要创建交易的时候，传一个clueId就可以了
				window.location.href = "workbench/clue/convert.do?clueId=${param.id}";
				alert("转换成功");

			}
			
		})


	});
</script>

</head>
<body>
	
	<!-- 搜索市场活动的模态窗口 -->
	<div class="modal fade" id="searchActivityModal" role="dialog" >
		<div class="modal-dialog" role="document" style="width: 90%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title">搜索市场活动</h4>
				</div>
				<div class="modal-body">
					<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
						<form class="form-inline" role="form">
						  <div class="form-group has-feedback">
						    <input type="text" class="form-control" id="aname" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
						    <span class="glyphicon glyphicon-search form-control-feedback"></span>
						  </div>
						</form>
					</div>
					<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
						<thead>
							<tr style="color: #B3B3B3;">
								<td></td>
								<td>名称</td>
								<td>开始日期</td>
								<td>结束日期</td>
								<td>所有者</td>
								<td></td>
							</tr>
						</thead>
						<tbody id="ActivityModalTboty">
							<%--<tr>
								<td><input type="radio" name="activity"/></td>
								<td>发传单</td>
								<td>2020-10-10</td>
								<td>2020-10-20</td>
								<td>zhangsan</td>
							</tr>--%>
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

	<div id="title" class="page-header" style="position: relative; left: 20px;">
		<h4>转换线索 <small>${param.fullname}-<%=company%></small></h4>
	</div>
	<div id="create-customer" style="position: relative; left: 40px; height: 35px;">
		新建客户：<%=company%>
	</div>
	<div id="create-contact" style="position: relative; left: 40px; height: 35px;">
		新建联系人：${param.fullname}
	</div>
	<div id="create-transaction1" style="position: relative; left: 40px; height: 35px; top: 25px;">
		<input type="checkbox" id="isCreateTransaction"/>
		为客户创建交易
	</div>
<%--为客户创建交易弹出框--%>
	<div id="create-transaction2" style="position: relative; left: 40px; top: 20px; width: 80%; background-color: #F7F7F7; display: none;" >
	
		<form id="tranForm" action="workbench/clue/convert.do" method="get">

			<input type="hidden" name="flag" value="a"/>

			<input type="hidden" name="clueId" value="${param.id}"/>

		  <div class="form-group" style="width: 400px; position: relative; left: 20px;">
		    <label for="amountOfMoney">金额</label>
		    <input type="text" class="form-control" name="amountOfMoney" id="amountOfMoney">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="tradeName">交易名称</label>
		    <input type="text" class="form-control" name="tradeName" id="tradeName" value="动力节点-">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="expectedClosingDate">预计成交日期</label>
		    <input type="text" class="form-control time" name="expectedClosingDate" id="expectedClosingDate">
		  </div>
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="stage">阶段</label>
		    <select id="stage" name="stage" class="form-control">
		    	<option></option>
				<c:forEach items="${stagelist}" var="s">
					<option value="${s.value}">${s.text}</option>
				</c:forEach>

		    <%--	<option>资质审查</option>
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
		  <div class="form-group" style="width: 400px;position: relative; left: 20px;">
		    <label for="activity">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);"  id="openActivityModal" style="text-decoration: none;"><span class="glyphicon glyphicon-search"></span></a></label>
		    <input type="text" class="form-control" id="activity"  placeholder="点击上面搜索" readonly>
			  <input type="hidden" id="activityIdBtn" name="activityId">
		  </div>
		</form>
		
	</div>
	
	<div id="owner" style="position: relative; left: 40px; height: 35px; top: 50px;">
		记录的所有者：<br>
		<b><%=owner%></b>
	</div>
	<div id="operation" style="position: relative; left: 40px; height: 35px; top: 100px;">
		<input class="btn btn-primary" type="button" id="convertClue" value="转换">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<input class="btn btn-default" type="button" value="取消">
	</div>
</body>
</html>