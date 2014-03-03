<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="base.jsp"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.sohu.smc.config.service.SmcConfiguration" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<html lang="en" class="fuelux">
<head>
	<meta charset="utf-8">
	<title>配置管理</title>
 <style>
        table tr.even {
            background: #ebebeb;
        }
        table tr.odd {
            background: #ffffff;
        }
    </style>
    <script type="text/javascript">
       function deleteConfirm(key, prefix){
        if(confirm("确定要删除该配置项吗？")) {
         location="config.jsp?act=del&key="+key+"&prefix="+prefix;
       }
      }

      function change_tab(prefix){
        location.href="config.jsp?prefix="+prefix;
      }
    </script>
</head>
<%
  request.setCharacterEncoding("utf-8");
  String auth = (String)request.getSession().getAttribute("auth");
  response.setCharacterEncoding("utf-8");
  String act = request.getParameter("act");
  String key = request.getParameter("key");
  String message = request.getParameter("message");
  String currKey = request.getParameter("prefix");
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
     response.sendRedirect("config.jsp?message="+message+"&prefix="+currKey);
  }

  if(message == null) message = "";
  Map<String,Map<String, Map<String, Object>>> properties = null;
  try{
      properties = SmcConfiguration.properties();
  } catch(Exception e){
      e.printStackTrace();
  }
%>
<body>
<div class="container" id="mainDiv">
	<ul id="myTab" class="nav nav-tabs">
	<%
	if(properties != null){
        Iterator<String> prefixIt = properties.keySet().iterator();
	    int counter = 0;
        while(prefixIt.hasNext()){
            String prefix = prefixIt.next();
            counter ++;
	        if(counter == 1 && StringUtils.isBlank(currKey)){
                currKey = prefix;
	        }
	%>

	  <li <% if(StringUtils.equals(currKey,prefix)) out.print("class=\"active\"");%>><a <a href="#main" onclick="change_tab('<%=prefix%>')" data-toggle="tab"><strong><%=prefix%></strong></a></li>
    <%
        }
    }
    %>
    </ul>
	<div id="main" style="height:300%;width:100%">
	    <%=message%>
		<table id="MyStretchGrid" class="table table-bordered datagrid">
			<thead>
			<tr>
				<th colspan="3">
					<div class="datagrid-header-left">
						<span style="font-size: 17px" ><a href="modifyConfig.jsp?act=add&prefix=<%=currKey%>">创建</a></span>
					</div>
				</th>
			</tr>
			</thead>
    <%
        if(properties != null){
        Map<String, Map<String, Object>> values = properties.get(currKey);
        if(values != null){
            Iterator<String> valuesIt = values.keySet().iterator();
            while(valuesIt.hasNext()){
                String subPrefix = valuesIt.next();
                Map<String,Object> subProperties = values.get(subPrefix);
                Iterator<String> propIt = subProperties.keySet().iterator();
                while(propIt.hasNext()){
                    String eachKey = propIt.next();
                    Object eachValue = subProperties.get(eachKey);
                    String modifyScript = "<a href=\"modifyConfig.jsp?act=show&key=" + eachKey +"&value=" + eachValue +"&prefix="+currKey+"\">修改</a>";
                    String delScript = "<a href=\"javascript:deleteConfirm('"+eachKey+"','"+ currKey+"');\">删除</a>";
    %>
        <tr width="100%">
            <td align="left" width="30%"><%=eachKey%></td>
            <td align="left" width="50%"><p><%=eachValue%></p></td>
            <td align="center" width="20%"><%=modifyScript%> ｜ <%=delScript%> </td>
        </tr>
    <%
                }
            }
        }
        }
    %>
		</table>
	</div>
</div>
</body>
</html>