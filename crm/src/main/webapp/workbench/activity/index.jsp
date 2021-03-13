<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
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

<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

<script type="text/javascript">
	//页面加载完毕
	$(function(){
		//页面加载完毕后执行。每页加载三条记录
		pageList(1,3);
		//为创建添加绑定事件，打开添加操作的模态窗口
		$("#addBtn").click(function () {
			//选择时间的方式
			$(".time").datetimepicker({
				minView:"month",
				language:"zh-CN",
				format:"yyyy-mm-dd",
				autoclose:true,
				todayBtn:true,
				pickerPosition:"bottom-left"
			});
			/*
				操作模态窗口的方式：
					需要操作的模态窗口的jquery对象，调用modal方法，为该方法传递show和hide
			*/

			//$("#createActivityModal").modal("show");
			//创建市场活动所有者首选用户名，改为动态选择
			$.ajax({
				url:"workbench/activity/getUserlist.do",
				data:{},
				type:"get",
				dataType:"json",
				success:function (data) {
					var html= "<option></option>";
					$.each(data,function (i,n) {
						html +="<option value='"+n.id+"'>"+n.name+"</option>"
					})
					$("#create-Owner").html(html);
					//动态首选登录的用户名
					var id = "${user.id}";
					$("#create-Owner").val(id);
					//所有下拉框处理完毕，展现模态窗口
					$("#createActivityModal").modal("show");

				}

			})
		})
		//添加市场活动操作
		$("#saveBtn").click(function () {
			alert("save");
			$.ajax({
				url:"workbench/activity/save.do",
				data:{

					"owner":$.trim($("#create-Owner").val()),
					"name":$.trim($("#create-Name").val()),
					"startDate":$.trim($("#create-startDate").val()),
					"endDate":$.trim($("#create-endDate").val()),
					"cost":$.trim($("#create-cost").val()),
					"description":$.trim($("#create-description").val())


				},
				type:"post",
				dataType:"json",
				success:function x(data) {
					//data {"success":true/false}
					if (data.success){

						//刷新市场活动信息列表，局部刷新
						/*
							form表单的jquery对象没有submit()方法
							让我们提交表单，但是没有reset()方法重置
							（但是idea为我们提示了）
							所以需要将jquery对象转为dom对象


						*/
						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						$("#activityAddForm")[0].reset();


						//关闭添加操作的模态窗口
						$("#createActivityModal").modal("hide");
						alert("添加成功！")
					} else {
						alert("添加失败！")
					}

				}
			})
		})
		//为查询按钮绑定事件，触发pageList方法
		$("#searchBtn").click(function () {

			alert("执行查询");

			/*

				点击查询按钮的时候，我们应该将搜索框中的信息保存起来,保存到隐藏域中


			 */

			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			pageList(1,3);

		})
		//为复选框绑定事件，触发全选操作
		$("#qx").click(function () {
			$("input[name=xz]").prop("checked",this.checked);

			//以下做法是不对的,因为动态获取的元素，不能以普通绑定事件的形式来进行操作的
			//动态生成的元素，我们要以on方法的形式来触发事件

			/*$("input[name=xz]").click(function () {
				a
			})*/
			//语法：$(需要绑定元素的有效的外层元素).on(绑定事件的方式，需要绑定的元素jQuery对象，回调函数)
			$("#activitytboby").on("click",$("input[name=xz]"),function () {
				//alert("2");
				$("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length);
			})
		})



		//为删除按钮绑定事件，执行市场活动删除操作
		$("#deleteBtn").click(function () {
			//找到复选框中选中的按钮
			var $xz = $("input[name=xz]:checked");
			if ($xz.length==0){
				alert("请选择要删除的活动");
			}else {//可能选了一条，可能多条
				if(confirm("确定删除")){
					//需要传多个id，拼接参数
					var param = "";
					//将$xz中的每一个dom对象遍历出来，取其value值，就相当于取得需要删除的记录的id
					var id ;
					for(var i=0;i<$xz.length;i++){
						param +="id="+$($xz[i]).val();
						//如果不是最后一个元素，需要加个&符号
						if (i < $xz.length-1){
							param +="&";
						}
					}
					$.ajax({
						url:"workbench/activity/deleteActivity.do",
						type:"post",
						data:param,
						dataType:"json",
						success:function (data) {

							if (data.success){
								pageList(1,3);
								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
								alert("删除成功");
							}else {
								alert("删除失败");
							}
						}
					})

				}

			}


		})
		
		//添加更新按钮事件，更改活动信息
		$("#editBtn").click(function () {
			var $xz = $("input[name=xz]:checked");
			//触发查询事件，先根据查询用户所选的记录获取得该记录的信息
			if($xz.length < 1){
				alert("请选择要修改的活动");
			}else if ($xz.length > 1) {
				alert("请选择一条要修改的活动")
			}else{

				var id = $xz.val();
				$.ajax({
					url:"workbench/activity/getUserListAndActivity.do",
					data:{
						"id":id
					},
					type:"get",
					dataType:"json",
					success:function (data) {
						//{{user1,user1},activity}
							//导入拥有者 （多个）
							var html = "<option></option>";
							$.each(data.uList,function (i,n) {
								html +="<option value='"+n.id+"'>"+n.name+"</option>";
							})

							$("#edit-owner").html(html);
							//添加activity信息 （一条）
							$("#edit-id").val(data.activity.id);
							$("#edit-name").val(data.activity.name);
							$("#edit-owner").val(data.activity.owner);
							$("#edit-startDate").val(data.activity.startDate);
							$("#edit-endDate").val(data.activity.endDate);
							$("#edit-cost").val(data.activity.cost);
							$("#edit-description").val(data.activity.description);

							$("#editActivityModal").modal("show");

					}
				})
			}
			
		})
		//为更新按钮提交
		$("#updateBtn").click(function () {
			/*//选择时间的方式
			$(".form-control time").datetimepicker({
				minView:"month",
				language:"zh-CN",
				format:"yyyy-mm-dd",
				autoclose:true,
				todayBtn:true,
				pickerPosition:"bottom-left"
			});*/
			//发送请求
			$.ajax({
				url:"workbench/activity/updateActivity.do",
				type:"post",
				data:{
					"id" : $.trim($("#edit-id").val()),
					"owner" : $.trim($("#edit-owner").val()),
					"name" : $.trim($("#edit-name").val()),
					"startDate" : $.trim($("#edit-startDate").val()),
					"endDate" : $.trim($("#edit-endDate").val()),
					"cost" : $.trim($("#edit-cost").val()),
					"description" : $.trim($("#edit-description").val())
				},
				dataType:"json",
				success:function (data) {
					if (data.success){
						alert("更新成功");
					} else {
						alert("更新失败");
					}
				}

			})

			pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
					,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
			$("#editActivityModal").modal("hide");
		})





	})


	//将所有市场所有活动添加到当前页，加分页处理
	//对于所有的关系型数据库，做前端的分页相关操作的基础组件，
	// 就是pageNo(页码)和pageSize（每一页的记录数）
	function pageList(pageNo,pageSize) {
		//将全选的复选框的勾取消选中
		$("#qx").prop("checked",false);

		//查询前，将隐藏于域保存的信息取出来，重新赋予到搜索框中
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));

		//alert("展示市场活动列表")
		$.ajax({
			url: "workbench/activity/pageList.do",
			data: {
				"pageNo": pageNo,
				"pageSize": pageSize,
				"name": $.trim($("#search-name").val()),
				"owner": $.trim($("#search-owner").val()),
				"startDate": $.trim($("#search-startDate").val()),
				"endDate": $.trim($("#search-endDate").val())
			},
			type: "get",
			dataType: "json",
			success: function (data) {
				//alert("22");

					var html = "";
					$.each(data.dataList, function (i, n) {
						//alert("-发送ajax请求");

						html += '<tr class="active">';
						html += '<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
						html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
						html += '<td>'+n.owner+'</td>';
						html += '<td>'+n.startDate+'</td>';
						html += '<td>'+n.endDate+'</td>';
						html += '</tr>';

					})
					$("#activitytboby").html(html);
					//计算总页数，
					var totalPages = data.total%pageSize ==0 ?
									data.total/pageSize :parseInt(data.total/pageSize)+1;

					//数据处理完毕后，结合分页查询，对前端展现分页信息
					$("#activityPage").bs_pagination({
						currentPage: pageNo, // 页码
						rowsPerPage: pageSize, // 每页显示的记录条数
						maxRowsPerPage: 20, // 每页最多显示的记录条数
						totalPages: totalPages, // 总页数
						totalRows: data.total, // 总记录条数

						visiblePageLinks: 3, // 显示几个卡片

						showGoToPage: true,
						showRowsPerPage: true,
						showRowsInfo: true,
						showRowsDefaultInfo: true,
						//该回调函数在点击分页组件的时候触发的
						onChangePage : function(event, data){
							pageList(data.currentPage , data.rowsPerPage);
						}
					})
			}
		})
	}

</script>
</head>
<body>

	<input type="hidden" id="hidden-name">
	<input type="hidden" id="hidden-owner">
	<input type="hidden" id="hidden-startDate">
	<input type="hidden" id="hidden-endDate">

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-Owner">

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-Name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" data-dismiss="modal" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id"/>
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">

								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name" >
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label" >开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate" readonly>
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label" >结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate" readonly>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" data-dismiss="modal" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	<%--市场活动列表--%>
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
	<%--查询市场活动--%>
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon" >名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon" >所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon" >开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="searchBtn">查询</button>
				  
				</form>
			</div>
	<%--创建修改删除--%>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" data-toggle="modal" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" data-toggle="modal" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<%--数据列表，使用分页--%>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activitytboby">

					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage">

				</div>


			</div>
			
		</div>
		
	</div>
</body>
</html>