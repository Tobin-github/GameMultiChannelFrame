<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;

%>
<base href="<%=basePath%>">
<!DOCTYPE HTML>
<html>
<head>
    <%--<meta charset="UTF-8">--%>
    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1" >
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <title>Title</title>
    <link rel="stylesheet" href="../../iconfont/iconfont.css">
    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/locale/easyui-lang-zh_CN.js"></script>

        <script>
            var wid=document.documentElement.clientWidth;
            var scale=wid/750;
            var css='html{font-size:'+750*scale/10+'px';
            document.write('<style>'+css+'</style>');
        </script>
        <style>
            @charset "utf-8";
            *{margin:0; padding:0; list-style:none; box-sizing: border-box; -webkit-box-sizing: border-box; outline: 0; -webkit-tap-highlight-color: transparent;-webkit-tap-highlight-color: transparent;}
            html{height: 100%;}
            a{text-decoration: none;display: block}
            body{min-height: 100%; font-size: 0.186667rem;  font-family: PingFangSC-Regular,Microsoft YaHei,Microsoft JhengHei,STHeiti STXihei,Arial; color:#666; background:#eee;}
            .iconfont {
                font-family: "iconfont" !important;
                font-size: 1.333333rem;
                font-style: normal;
                -webkit-font-smoothing: antialiased;
                -moz-osx-font-smoothing: grayscale;
            }
            .wrap{margin-top:1rem;text-align: center}
            .btn{
                display: inline-block;
                height: 1.4rem;
                line-height: 1.4rem;
                width: 2.666667rem;
                text-align: center;
                margin: 0 0.2rem;
                border-radius: 8px;
            }
            .zhifubao{color: #fff;background: #4aa7fd}
            .icon-wechat-copy{background: #52a549;color: #fff;}
            /*.icon-pingguo{background: #fe9978;color: #fff;}*/
        </style>
        <script type="text/javascript">
            function jsToIOS() {
                var str = '{"appId":"${appId}","channelId":"${channelId}","sdkUserId":"${sdkUserId}","orderId":"${orderId}","spdata":"3","payClick":"weChatClick","wxExtensionUrl":"${wxExtensionUrl}"}'
                BtnClick(str);
            }
            function jsToIOS2() {
                var str = '{"payClick":"AliClick"}'
                BtnClick(str);
            }
        </script>
</head>
<body>
    <div class="btn_back_home">
        <!--<img src="back.jpg" height="50" width="100" onclick="jsToIOS()"/>-->
        <img src="back.jpg" style="display:block;width:10%;" onclick="jsToIOS2()"/>
    </div>
    <%--<form id="aliform" action="http://www.wgb.cn:9080/pay/apple/sdkGetOrder" method="get">
        <input type="hidden" name="appId" value="${appId}"/>
        <input type="hidden" name="channelId" value="${channelId}"/>
        <input type="hidden" name="sdkUserId" value="${sdkUserId}"/>
        <input type="hidden" name="orderId" value="${orderId}"/>
        <input type="hidden" name="spdata" value="2"/>
    </form>--%>
    <form id="aliform" action="http://120.25.243.133:8080/pay/apple/sdkGetOrder" method="get">
        <input type="hidden" name="appId" value="${appId}"/>
        <input type="hidden" name="channelId" value="${channelId}"/>
        <input type="hidden" name="sdkUserId" value="${sdkUserId}"/>
        <input type="hidden" name="orderId" value="${orderId}"/>
        <input type="hidden" name="spdata" value="2"/>
    </form>
   <div class="wrap">
       <a href="#" class="btn zhifubao iconfont icon-zhifubao" onclick="document.getElementById('aliform').submit();return false" ></a>
       <%--<button type="button" class="btn weixin iconfont icon-wechat-copy" onclick="jsToIOS()"></button>--%>
   </div>


</body>
</html>