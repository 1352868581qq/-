
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<base href="<%=basePath%>">
<html>
<head>

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

    <script type="text/javascript" src="jquery/echarts/echarts.min.js"></script>

    <script type="text/javascript">
      $(function () {
          //页面加载完毕执行
          getCharts();



      })
      function getCharts() {
          $.ajax({
              url:"workbench/transaction/getCharts.do",
              type:"get",
              dateType:"json",
              success:function (data) {
                 var myChart = echarts.init(document.getElementById('main'));
                 //我们要画的图
                  var option = {
                      title: {
                          text: '交易漏斗图',
                          subtext: '不同阶段交易数量统计'
                      },

                      series: [
                          {
                              name:'交易漏斗图',
                              type:'funnel',
                              left: '10%',
                              top: 60,
                              //x2: 80,
                              bottom: 60,
                              width: '80%',
                              // height: {totalHeight} - y - y2,
                              min: 0,
                              max: data.total,
                              minSize: '0%',
                              maxSize: '100%',
                              sort: 'descending',
                              gap: 2,
                              label: {
                                  show: true,
                                  position: 'inside'
                              },
                              labelLine: {
                                  length: 10,
                                  lineStyle: {
                                      width: 1,
                                      type: 'solid'
                                  }
                              },
                              itemStyle: {
                                  borderColor: '#fff',
                                  borderWidth: 1
                              },
                              emphasis: {
                                  label: {
                                      fontSize: 20
                                  }
                              },
                              data: data.dataList
                          /*[
                                  {value: 60, name: '访问'},
                                      {value: 40, name: '咨询'},
                                      {value: 20, name: '订单'},
                                      {value: 80, name: '点击'},
                                      {value: 100, name: '展现'}
                                  ]*/
                          }
                      ]
                  };
                  myChart.setOption(option);
              }
          })
      }


    </script>
</head>
<body>
    <%--为echarts准备一个具备大小（宽高）的dom--%>
    <div id="main" style="width: 600px;height: 400px;"></div>
</body>
</html>
