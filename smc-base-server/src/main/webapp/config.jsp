<!DOCTYPE HTML>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.sohu.smc.config.service.SmcConfiguration" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<html>
<%@ page language="java"%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
  request.setCharacterEncoding("utf-8");
  response.setCharacterEncoding("utf-8");
  String act = request.getParameter("act");
  String key = request.getParameter("key");
  String message = request.getParameter("message");
  if(StringUtils.equals(act, "del")){
     try{
        boolean isSucc = SmcConfiguration.remove(key);
        if(isSucc) message = "删除成功！key=" + key;
        else message = "删除失败，请稍等再试！" + key;
     }catch(Exception e){
        message = e.getMessage();
     }
  }
  if(StringUtils.isNotBlank(act)){
     message = URLEncoder.encode(message, "UTF-8");
     response.sendRedirect("config.jsp?message="+message);
  }

  if(message == null) message = "";
%>
<head>
<meta http-equiv="Content-Type"content="text/html;charset=utf-8"/>
<meta name="viewport"content="width=device-width,user-scalable=no,minimum-scale=1.0,maximum-scale=1.0"/>
<meta http-equiv="X-UA-Compatible"content="IE=edge, chrome=1"/>
<meta http-equiv="Cache-Control"content="no-cache"/>
<meta http-equiv="Expires"content="0"/>
<title>Sohu Configuration System</title>
<style type="text/css">
    body {
        word-wrap: break-word;
        word-break: break-all;
    }
    th,td {
        padding:5px;
    }
    .table-b table td{border:1px solid black}
    /* css注释：只对table td标签设置红色边框样式 */

</style>
<script type="text/javascript">
  function deleteConfirm(key){
   if(confirm("确定要删除该配置项吗？")) {
     window.location="http://"+window.location.host + "/base/config.jsp?act=del&key="+key;
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
                <a href="index.jsp">首页</a>
            </td>
        </tr>
    </table>

    <%

        Map<String, Map<String,Object>> properties = null;
        try{
            properties = SmcConfiguration.properties();
        } catch(Exception e){
            e.printStackTrace();
        }
        if(properties != null){
        Iterator<String> prefixIt = properties.keySet().iterator();
        while(prefixIt.hasNext()){
            String prefix = prefixIt.next();
    %>

    <table width="80%" class="table-b" border="1" cellspacing="0" cellpadding="0">
        <th colspan="3"><%= prefix %></th>
        <%
            Map<String, Object> values = properties.get(prefix);
            if(values != null){
            Iterator<String> valuesIt = values.keySet().iterator();
            while(valuesIt.hasNext()){
                String eachKey = valuesIt.next();
                Object eachValue = values.get(eachKey);
                String modifyScript = "<a href=\"modifyConfig.jsp?act=show&key=" + eachKey +"&value=" + eachValue +"\">修改</a>";
                String delScript = "<a href=\"javascript:deleteConfirm('"+eachKey+"');\">删除</a>";
        %>
        <tr width="100%">
            <td align="center" width="40%"><%=eachKey%></td>
            <td align="center" width="40%"><p><%=eachValue%></p></td>
            <td align="center" width="20%"><%=modifyScript%> ｜ <%=delScript%> </td>
        </tr>

    <%
        }
        }
    %>
        </table>
        <br/>
        <br/>
    <%
      }
      }
    %>
    </center>
</body>
</html>