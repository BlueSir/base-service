<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="base.jsp"%>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.sohu.smc.schedule.core.model.Schedule" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.sohu.smc.schedule.core.service.SchedulerService" %>
<%@ page import="org.quartz.*" %>
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
        if(confirm("确定要修改该计划吗？")){
          document.getElementById("modifyForm").submit();
        }
      }
    </script>
</head>
<%
  request.setCharacterEncoding("utf-8");
  response.setCharacterEncoding("utf-8");
  String action = request.getParameter("act");
  String scheduleName = request.getParameter("scheduleName");
  if(scheduleName == null){
    scheduleName = "";
  }
  String serverName = request.getParameter("serverName");
  if(serverName == null){
    serverName = "";
  }
  String scheduleCron = request.getParameter("scheduleCron");
  if(scheduleCron == null){
    scheduleCron = "";
  }

  String scheduleType = request.getParameter("scheduleType");
  String strategy = request.getParameter("strategy");

  String message = "";
  try{
    if(StringUtils.equals(action, "modify") || StringUtils.equals(action, "add")){
        if(StringUtils.isBlank(scheduleName)){
            message = "计划名称不能为空！";
        }else if(StringUtils.isBlank(serverName)){
            message = "目标服务器名称不能为空！";
        }else if(StringUtils.isBlank(scheduleCron)){
            message = "计划表达式不能为空！";
        }else {
            if(StringUtils.equals(action, "modify")){
                boolean isSucc = SchedulerService.getInstance().modifySchedule(scheduleName, serverName, scheduleCron, scheduleType, strategy);

                if(isSucc){
                    message = "计划修改成功！";
                }else{
                    message = "计划修改失败，请稍候再试！";
                }
            }else if( StringUtils.equals(action, "add") ){
                boolean isSucc = SchedulerService.getInstance().addSchedule(scheduleName, serverName, scheduleCron, scheduleType, strategy);
                if(isSucc){
                    message = "计划创建成功！";
                } else{
                    message = "计划创建失败，请稍候再试！";
                }
            }
            message = URLEncoder.encode(message, "UTF-8");
            response.sendRedirect("schedule.jsp?message="+message);

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
    <form id="modifyForm" action="modifySchedule.jsp">
	    <table width="80%"  id="MyStretchGrid" class="table table-bordered datagrid">
            <thead>
            <tr>
                <th colspan="8">
                    <div class="datagrid-header-left">
                        <span style="font-size: 17px" ><a href="schedule.jsp">返回</a></span>
                    </div>
                </th>
            </tr>
            </thead>
            <tr width="100%">
                <td align="center">计划名称</td>
                <td><input type="text" name="scheduleName" value="<%= scheduleName %>"></td>
                <td>计划的名称，用来唯一标识一个计划，名称格式为：系统名_功能；如：SmcApi_DeleteCache</td>
            </tr>
            <tr>
                <td align="center">目标服务器名称</td>
                <td><input type="text" name="serverName" value="<%= serverName %>"></td>
                <td>即执行该计划的服务器名称，该名称必须与系统在SmcDiscovery中注册的名称相同。</td>
            </tr>
            <tr>
                <td align="center">计划类型</td>
                <td>
                  <input type="radio" name="scheduleType" value="single" <% if(StringUtils.equals(scheduleType, "single") || StringUtils.isBlank(scheduleType)) out.print("checked=\"checked\""); %>/> Single
                  <br />
                  <input type="radio" name="scheduleType" value="all"  <% if(StringUtils.equals(scheduleType, "all")) out.print("checked=\"checked\""); %>/> All
                </td>
                <td>计划的类型包括两种，一种是服务器集群中某一台执行，即Single，另一种是服务器集群全部执行，即All;</td>
            </tr>
            <tr>
                <td align="center">计划策略</td>
                <td>
                  <input type="radio" name="strategy" value="polling" <% if(StringUtils.equals(strategy, "polling") || StringUtils.isBlank(strategy)) out.print("checked=\"checked\""); %>/> Polling
                  <br />
                  <input type="radio" name="strategy" value="random" <% if(StringUtils.equals(strategy, "random")) out.print("checked=\"checked\""); %>/> Random
                  <br />
                  <input type="radio" name="strategy" value="sticky" <% if(StringUtils.equals(strategy, "sticky")) out.print("checked=\"checked\""); %>/> Sticky
                </td>
                <td>
                    该策略只有在计划类型为Single时生效，有三种策略，１、轮询执行：Polling ２、随机执行：Random，３、固定执行：Sticky
                </td>
            </tr>
            <tr>
                <td align="center">计划表达式</td>
                <td><input type="text" name="scheduleCron" value="<%= scheduleCron %>"></td>
                <td>
                  计划表达式采用的是Quartz Cron Expression. 如果配置请看 <a href="cron.html" target="_blank">详细说明</a>
                </td>
            </tr>
            <tr>
                <td align="center">操作</td>
                <td><input type="hidden" name="act"
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

                    else {

                    }
                    %>
                </td>
            </tr>
        </table>
        </form>
	</div>
</div>
</body>
</html>