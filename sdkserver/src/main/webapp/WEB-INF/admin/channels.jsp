<%--
  Created by IntelliJ IDEA.
  User: xiaohei
  Date: 2015/8/22
  Time: 14:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;

%>
<base href="<%=basePath%>">
<html>
<head>
    <title></title>

    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/js/plugins/easyui/themes/color.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/u8server.css">

    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/js/plugins/easyui/locale/easyui-lang-zh_CN.js"></script>

</head>
<body>
<div id="channels">

</div>

<div id="easyui_toolbar" region="north" border="false"
     style="border-bottom: 1px solid #ddd; height: 32px; padding: 2px 5px; background: #fafafa;">
    <div style="float: left;">
        <a class="easyui-linkbutton" plain="true" icon="icon-add" onclick="javascript:showAddDialog();">新增</a>
    </div>

    <div class="datagrid-btn-separator"></div>

    <div style="float: left;">
        <a class="easyui-linkbutton" plain="true" icon="icon-edit" onclick="javascript:showEditDialog();">编辑</a>
    </div>

    <div class="datagrid-btn-separator"></div>

    <div style="float: left;">
        <a class="easyui-linkbutton" plain="true"
           icon="icon-remove" onclick="javascript:deleteChannel();">删除</a>
    </div>

    <div id="tb" style="float: right;">
        <input id="search_box" class="easyui-searchbox" style="width: 250px"
               data-options="searcher:doSearch,prompt:'请输入查询词',menu:'#search_menu'"/>

        <div id="search_menu" style="width:120px">
            <div data-options="name:'channel_name'">渠道名称</div>
            <div data-options="name:'channel_id'">渠道商ID</div>
        </div>
    </div>

</div>

<div id="dialog_add" class="easyui-dialog u8_form"
     closed="true" buttons="#dlg-buttons" style="height: 500px;width: 500px;">
    <div class="ftitle">渠道信息</div>
    <form id="fm" method="post" novalidate>
        <input id="id" type="hidden" name="id"/>
        <input id="appID" type="hidden" name="appID"/>
        <input id="masterID" type="hidden" name="masterID"/>

        <div class="u8_form_row">
            <label>所属游戏：</label>
            <input id="games" type="text" class="easyui-combobox" name="allgames" maxlength="255" required="true"/>
        </div>

        <div class="u8_form_row">
            <label>渠道商：</label>
            <input id="masters" type="text" class="easyui-combobox" name="allmasters" maxlength="255" required="true"/>
        </div>
        <div class="u8_form_row">
            <label>渠道号：</label>
            <input id="channel" type="text" value="111" class="easyui-textbox" name="channelID" maxlength="255"
                   required="true"/>
            <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="recommendChannelID()"
               style="width:70px">推荐</a>
        </div>
        <div class="u8_form_row">
            <label>平台号：</label>
            <input id="platID" type="text" value="111" class="easyui-textbox" name="platID" maxlength="255"
                   required="true"/>
        </div>
        <div class="u8_form_row">
            <label>充值状态(0:开放/1:关闭)：</label>
            <input type="text" class="easyui-textbox" name="openPayFlag" prompt="关闭后，下单会返回错误" maxlength="255"
                   novalidate/>
        </div>
        <div class="u8_form_row">
            <label>CPID：</label>
            <input type="text" class="easyui-textbox" name="cpID" maxlength="255" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>AppID：</label>
            <input type="text" class="easyui-textbox" name="cpAppID" maxlength="255" novalidate/>
        </div>
        <div class="u8_form_row">
            <label>AppKey：</label>
            <input type="text" class="easyui-textbox" name="cpAppKey" maxlength="255" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>AppSecret：</label>
            <input type="text" class="easyui-textbox" name="cpAppSecret" maxlength="1024" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>PayID：</label>
            <input type="text" class="easyui-textbox" name="cpPayID" maxlength="255" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>PayPublicKey：</label>
            <input type="text" class="easyui-textbox" name="cpPayKey" maxlength="1024" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>PayPrivateKey：</label>
            <input type="text" class="easyui-textbox" name="cpPayPriKey" maxlength="1024" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>特殊配置：</label>
            <input type="text" class="easyui-textbox" name="cpConfig" maxlength="1024" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>登录认证地址：</label>
            <input type="text" class="easyui-textbox" name="authUrl" prompt="这里会覆盖渠道商配置中的" maxlength="1024" novalidate/>
        </div>

        <div class="u8_form_row">
            <label>支付回调地址：</label>
            <input type="text" class="easyui-textbox" name="payCallbackUrl" prompt="这里会覆盖渠道商配置中的" maxlength="1024"
                   novalidate/>
        </div>

        <div class="u8_form_row">
            <label>渠道下单地址：</label>
            <input type="text" class="easyui-textbox" name="orderUrl" prompt="这里会覆盖渠道商配置中的" maxlength="1024"
                   novalidate/>
        </div>

        <div class="u8_form_row">
            <label>脚本类路径：</label>
            <input type="text" class="easyui-textbox" name="verifyClass" prompt="这里会覆盖渠道商配置中的" maxlength="1024"
                   novalidate/>
        </div>

       <%--<div class="u8_form_row">
            <label>是否开启支付：</label>
            <input type="text" class="easyui-textbox" name="openPayFlag" prompt="1为开启,2为不开启" maxlength="1024"
                   novalidate/>
        </div>--%>

        <%--<div class="u8_form_row">
            <label>平台类型：</label>
            <input type="text" class="easyui-textbox" name="platID" prompt="1为Android,2为IOS" maxlength="1024"
                   novalidate/>
        </div>--%>

        <div class="u8_form_row">
            <label>是否多支付渠道：</label>
            <input type="text" class="easyui-textbox" name="platType" prompt="1为是,0为否" maxlength="1024"
                   novalidate/>
        </div>

    </form>
</div>
<div id="dlg-buttons">
    <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="saveUser()" style="width:90px">保
        存</a>
    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel"
       onclick="javascript:$('#dialog_add').dialog('close')" style="width:90px">取 消</a>
</div>


<script type="text/javascript">

    var url;
    function showAddDialog() {
        $("#dialog_add").window({
            top: ($(window).height() - 500) * 0.5,
            left: ($(window).width() - 500) * 0.5
        });

        $("#dialog_add").dialog('open').dialog('setTitle', '添加渠道');

        $('#fm').form('clear');

        url = '<%=basePath%>/admin/channels/addChannel';

    }

    function showEditDialog() {

        $("#dialog_add").window({
            top: ($(window).height() - 500) * 0.5,
            left: ($(window).width() - 500) * 0.5
        });


        var row = $('#channels').datagrid('getSelected');
        if (row) {

            $("#dialog_add").dialog('open').dialog('setTitle', '编辑渠道');
            $('#fm').form('load', row);
            $('#games').combobox('select', row.appID);
            $('#masters').combobox('select', row.masterID);
            url = '<%=basePath%>/admin/channels/saveChannel';

        } else {
            $.messager.show({
                title: '操作提示',
                msg: '请选择一条记录'
            })
        }
    }

    function deleteChannel() {
        var row = $('#channels').datagrid('getSelected');
        if (row) {
            $.messager.confirm(
                    '操作确认',
                    '确定要删除该渠道吗？(操作不可恢复)',
                    function (r) {
                        if (r) {
                            $.post('<%=basePath%>/admin/channels/removeChannel', {currChannelID: row.channelID}, function (result) {
                                if (result.state == 1) {
                                    $('#dialog_add').dialog('close');
                                    $("#channels").datagrid('reload');
                                }

                                $.messager.show({
                                    title: '操作提示',
                                    msg: result.msg
                                })

                            }, 'json');
                        }
                    }
            );
        } else {
            $.messager.show({
                title: '操作提示',
                msg: '请选择一条记录'
            })
        }
    }

    function saveUser() {

        $('#fm').form('submit', {
            url: url,
            onSubmit: function () {
                return $(this).form('validate');
            },
            success: function (result) {
                var result = eval('(' + result + ')');

                if (result.state == 1) {
                    $('#dialog_add').dialog('close');
                    $("#channels").datagrid('reload');
                }

                $.messager.show({
                    title: '操作提示',
                    msg: result.msg
                })
            }
        })

    }


    function recommendChannelID() {

        $.post('<%=basePath%>/admin/channels/recommendChannelID', {}, function (result) {
            if (result.state == 1) {
                $("#channel").textbox('setValue', result.data);
            } else {
                alert(result.msg);
            }
        });

    }

    function doSearch(value, name) {
        alert("value:" + value + ";name:" + name);
    }

    $("#channels").datagrid({
        height: 430,
        url: '<%=basePath%>/admin/channels/getAllChannels',
        method: 'POST',
        idField: 'channelID',
        striped: true,
        fitColumns: true,
        singleSelect: true,
        rownumbers: true,
        pagination: true,
        nowrap: true,
        loadMsg: '数据加载中...',
        pageSize: 10,
        pageList: [10, 20, 50, 100],
        showFooter: true,
        columns: [[
            {field: 'id', title: 'ID', width: 40, sortable: true},
            {field: 'channelID', title: '渠道号', width: 40, sortable: true},
            {field: 'platID', title: '平台ID', width: 40, sortable: true},
            {field: 'openPayFlag', title: '充值状态', width: 20, sortable: true},
            {field: 'masterName', title: '渠道名称', width: 40, sortable: true},
            {field: 'cpID', title: 'CPID', width: 40, sortable: true},
            {field: 'cpAppID', title: 'AppID', width: 40, sortable: true},
            {field: 'cpAppKey', title: 'AppKey', width: 40, sortable: true},
            {field: 'cpAppSecret', title: 'AppSecret', width: 40, sortable: true},
            {field: 'cpPayID', title: 'PayID', width: 40, sortable: true},
            {field: 'cpPayKey', title: 'PayPublicKey', width: 40, sortable: true},
            {field: 'cpPayPriKey', title: 'PayPrivateKey', width: 40, sortable: true},
            {field: 'openPayFlag', title: '是否开启支付', width: 40, sortable: true},
            {field: 'platID', title: '平台类型', width: 40, sortable: true},
            {field: 'platType', title: '是否多支付渠道', width: 40, sortable: true}
        ]],
        toolbar: '#easyui_toolbar'
    });

    $("#games").combobox({
        url: '<%=basePath%>/admin/games/getAllGamesSimple',
        valueField: 'appID',
        textField: 'name',
        onSelect: function (rec) {
            $('#appID').val(rec.appID);
        }
    });


    $("#masters").combobox({
        url: '<%=basePath%>/admin/channelMaster/getAllMastersSimple',
        valueField: 'masterID',
        textField: 'masterName',
        onSelect: function (rec) {
            $('#masterID').val(rec.masterID);
        }
    });

</script>

</body>
</html>
