<%--
  Created by IntelliJ IDEA.
  User: xiaohei
  Date: 2015/8/22
  Time: 10:58
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
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/u8server.css">

    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/color.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/u8server.css">

    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/locale/easyui-lang-zh_CN.js"></script>


</head>
<body>

  <div class="u8_header">
      <div class="u8_logo">

      </div>
      <div class="u8_title">
        U8Server后台管理系统
      </div>
      <div style="float:right;color: #ffffff;font-size: 12px;margin-top: 5px;margin-right: 10px;">
          <span>当前登录用户：</span><span>${sessionScope.adminName}</span>
          <span><a href="javascript:void(0)" onclick="exit();" style="width:90px;color:#ffffff">[退出]</a></span>
      </div>
  </div>

  <script type="text/javascript">

      function exit(){

          $.post('<%=basePath%>/admin/exit', {}, function(result){
              if (result.state == 1) {

                  location.href="<%=basePath%>/admin/login"

              }

          }, 'json');
      }


  </script>

</body>
</html>
