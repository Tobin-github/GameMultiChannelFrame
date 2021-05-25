<%--
  Created by IntelliJ IDEA.
  User: xiaohei
  Date: 2015/8/29
  Time: 11:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;

%>
<base href="<%=basePath%>">
<html>
<head>
    <title></title>

    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/color.css">

    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.min.js"></script>

    <script src="http://cdn.hcharts.cn/highcharts/highcharts.js"></script>
    <script src="http://cdn.hcharts.cn/highcharts/modules/exporting.js" type="text/javascript"></script>

</head>
<body>
    <div id="container" style="min-width:700px;height:400px"></div>

<script type="text/javascript">

$(function(){



});

</script>

</body>



</html>
