<!DOCTYPE HTML>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.sohu.smc.config.service.SmcConfiguration" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<html>
<%@ page language="java"%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
  request.setCharacterEncoding("utf-8");
  response.setCharacterEncoding("utf-8");
  String action = request.getParameter("act");
  String key = request.getParameter("key");
  if(key == null){
    key = "";
  }
  String value = request.getParameter("value");
  if(value == null){
    value = "";
  }

  String message = "";
  try{
    if(StringUtils.equals(action, "modify") || StringUtils.equals(action, "add")){
        if(StringUtils.isBlank(key)){
            message = "Key不能为空！";
        }else if(StringUtils.isBlank(value)){
            message = "Value不能为空！";
        }else {
            if(StringUtils.equals(action, "modify")){
                boolean isSucc = SmcConfiguration.setProperty(key, value);

                if(isSucc){
                    message = "配置修改成功！(key="+ key + ",value="+value+")";
                }else{
                    message = "配置修改失败，请稍候再试！(key="+ key + ",value="+value+")";
                }
            }else if( StringUtils.equals(action, "add") ){
                if(SmcConfiguration.checkExists(key)){
                    message = "配置Key已经存在，请修改Key或去修改该Key对应的值！(key="+ key + ",value="+value+")";
                }else{
                    boolean isSucc = SmcConfiguration.setProperty(key, value);
                    if(isSucc){
                        message = "配置创建成功！(key="+ key + ",value="+value+")";
                    } else{
                        message = "配置创建失败，请稍候再试！(key="+ key + ",value="+value+")";
                    }
                }
            }
            message = URLEncoder.encode(message, "UTF-8");
            response.sendRedirect("config.jsp?message="+message);

        }
    }

  }catch (Exception e){
      message = e.getMessage();
  }

%>
<head>
<meta http-equiv="Content-Type"content="text/html;charset=utf-8"/>
<meta name="viewport"content="width=device-width,user-scalable=no,minimum-scale=1.0,maximum-scale=1.0"/>
<meta http-equiv="X-UA-Compatible"content="IE=edge, chrome=1"/>
<meta http-equiv="Cache-Control"content="no-cache"/>
<meta http-equiv="Expires"content="0"/>
<title>Sohu SMC Schedule System</title>
<style type="text/css">
    .table-b table td{border:1px solid black}
    /* css注释：只对table td标签设置红色边框样式 */
</style>
<script type="text/javascript">
  function modify(){
    if(confirm("修改后将在系统中立即生效,确定要修改该配置项吗？")){
      document.getElementById("modifyForm").submit();
    }
  }
</script>
</head>
<body>
    <center>
    <h1>SMC Configuration System</h1>
    <p><%= message %></p>
    <table width="80%" class="table-b" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td colspan="4" align="left">
                <a href="modifyConfig.jsp?act=init">创建</a>
            </td>
            <td colspan="4" align="right">
                <a href="config.jsp">返回</a>
            </td>
        </tr>
    </table>
    <form id="modifyForm" action="modifyConfig.jsp">
        <table width="80%" class="table-b" border="1" cellspacing="0" cellpadding="0">
            <tr width="100%">
                <td align="center" width="10%">配置Key</td>
                <td width="50%"><input type="text" name="key" size="20" value="<%= key %>"></td>
                <td width="40%">配置的Key，并且是全站唯一的，如果相同会覆盖之前人值，格式为：smc.系统名.prefix.key,如：smc.configuration.zk.server</td>
            </tr>
            <tr>
                <td align="center">配置的Value</td>
                <td><textarea name="value"  rows="5" cols="60"><%= value %></textarea></td>
                <td>配置项的值，不能为空</td>
            </tr>
            <tr>
                <td align="center">操作</td>
                <td colspan="2"><input type="hidden" name="act"
                    <%
                    if(StringUtils.equals(action, "init") || StringUtils.equals(action, "add"))
                    {
                        out.println(" value=\"add\">");
                        out.println("<input type=\"submit\" value=\"添加\">");
                    }
                    else if(StringUtils.equals(action, "show") || StringUtils.equals(action, "modify"))
                    {
                        out.println(" value=\"modify\">");
                        out.println("<button type=\"button\" onclick=\"modify()\">修改</button>");
                    }

                    %>
                </td>
            </tr>
        </table>
    </form>
    </center>
</body>
</html>