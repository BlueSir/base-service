<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="base.jsp"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.sohu.smc.config.service.SmcConfiguration" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.sohu.smc.base.server.action.Log" %>
<html lang="en" class="fuelux">
<head>
	<meta charset="utf-8">
	<title></title>
 <style>
        table tr.even {
            background: #ebebeb;
        }
        table tr.odd {
            background: #ffffff;
        }
    </style>
    <script type="text/javascript">
      function modify(){
        if(confirm("修改后将在系统中立即生效,确定要修改该配置项吗？")){
          document.getElementById("modifyForm").submit();
        }
      }
    </script>
</head>
<%
  request.setCharacterEncoding("utf-8");
  response.setCharacterEncoding("utf-8");
  String action = request.getParameter("act");
  String key = request.getParameter("key");
  String prefix = request.getParameter("prefix");
  String auth = (String)request.getSession().getAttribute("auth");
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
                String oldValue = request.getParameter("oldValue");
                boolean isSucc = SmcConfiguration.setProperty(key, value);

                if(isSucc){
                    message = "配置修改成功！(key="+ key + ",value="+value+")";
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = df.format(new Date());
                    Log.log(key, date + " "+ auth +"修改了配置"+key+",old="+oldValue+",new="+value);
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
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String date = df.format(new Date());
                        Log.log(key, date + " "+ auth +"新增了配置" + key + ",value="+value);
                    } else{
                        message = "配置创建失败，请稍候再试！(key="+ key + ",value="+value+")";
                    }
                }
            }
            message = URLEncoder.encode(message, "UTF-8");
            response.sendRedirect("config.jsp?message="+message+"&prefix="+prefix);

        }
    }

  }catch (Exception e){
      message = e.getMessage();
  }

%>
<body>
<div class="container" id="mainDiv">
    </br>
    </br>
    <form id="modifyForm" action="modifyConfig.jsp">
	    <table width="80%"  id="MyStretchGrid" class="table table-bordered datagrid">
            <thead>
            <tr>
                <th colspan="8">
                    <div class="datagrid-header-left">
                        <span style="font-size: 17px" ><a href="config.jsp">返回</a></span>
                    </div>
                </th>
            </tr>
            </thead>
            <tr width="100%">
                <td align="center" width="10%">配置Key</td>
                <td width="50%"><input type="text" name="key" size="20" value="<%= key %>" <% if(StringUtils.equals(action, "show")) {out.print("readonly='readonly'");}%>></td>
                <td width="40%">配置的Key，并且是全站唯一的，如果相同会覆盖之前人值，格式为：smc.系统名.prefix.key,如：smc.configuration.zk.server</td>
            </tr>
            <tr>
                <td align="center">配置的Value</td>
                <td><textarea style="cols=100" name="value"  rows="5" cols="100"><%= value %></textarea>
                <input type="hidden" name="oldValue" value="<%=value%>">
                <input type="hidden" name="prefix" value="<%=prefix%>">
                </td>
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
        <h3>日志：</h3>
        <ul>
            <%
                List<String> log = Log.getLog(key);
                if(log != null){
                    for(String each : log){
            %>
            <li><%=each%></li>
            <%
                    }
                }
            %>
        </ul>
	</div>
</div>
</body>
</html>