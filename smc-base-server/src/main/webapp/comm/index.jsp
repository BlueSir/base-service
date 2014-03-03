<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="base.jsp"%>
<%
    String auth=(String)request.getSession().getAttribute("auth");
    String admin = (String)request.getSession().getAttribute("admin");
%>
<html  lang="zh-CN" class="fuelux" >
<style type="text/css">
    html,body{height:100%;}
    body {

    }
</style>
<script type="text/javascript">

    function resize(){
        document.getElementById('iframepage').style.height = document.body.clientHeight - 100+"px";
    }
    window.onload = resize;
    function clickMenu(url,dom) {
        $("ul.nav-tabs > li").removeClass("active");
        $(dom).parent().addClass("active");
        $("#iframepage").attr("src", url);
        //window.frames("iframepage").location.reload(true);
    }

    /**设置iframe自适应高度*/

    function iFrameHeight() {
        var iframe = document.getElementById("iframepage");
        iframe.style.height = document.body.clientHeight - 84+"px";
        alert(document.body.clientHeight - 84)
    }
</script>
<body style="overflow:hidden ">
<div class="navbar navbar-fixed-top navbar-inverse">
    <div class="navbar-inner">
        <div class="container">
            <a id="BrandLink" class="brand" href="index.jsp">SMC后台管理系统</a><br/><br/>
            <div class="nav-collapse">
                <%
                    if(auth != null){
                %>
                <ul id="menu_ul" class="nav nav-tabs">
                    <li class="active"><a href="javascript:void(0);"
                           onclick="clickMenu('config.jsp',this);">配置管理</a></li>
                    <li><a href="javascript:void(0);"
                                          onclick="clickMenu('schedule.jsp',this);">分布式任务</a></li>
                    <%
                        if(admin != null && admin.equals("admin")){
                    %>
                    <li><a href="javascript:void(0);"
                                          onclick="clickMenu('usermanage.jsp',this);">用户管理</a></li>

                </ul>
                    <%
                        }
                    %>
                <p class="navbar-text pull-right">
                    <%=auth %> 欢迎你 &nbsp&nbsp<a href="../logout.go" >Logout</a> &nbsp&nbsp&nbsp
                </p>
                    <%
                    }
                    %>
            </div>
        </div>
    </div>
</div>
<div class="row-fluid"  height="100%">
    <div hight="82px"></div>
    <iframe id="iframepage" name="iframepage" height="100%"
            src="config.jsp"  frameborder="0" scrolling="yes"  width="100%"></iframe>
</div>
</body>
</html>