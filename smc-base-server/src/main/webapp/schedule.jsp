<!DOCTYPE HTML>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.sohu.smc.schedule.core.model.Schedule" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.sohu.smc.schedule.core.service.SchedulerService" %>
<%@ page import="com.netflix.curator.x.discovery.ServiceInstance" %>
<%@ page import="com.sohu.smc.schedule.core.util.DiscoveryUtil" %>
<%@ page language="java"%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
  request.setCharacterEncoding("utf-8");
  response.setCharacterEncoding("utf-8");
  String act = request.getParameter("action");
  String scheduleName = request.getParameter("scheduleName");
  String serverName = request.getParameter("serverName");
  String message = request.getParameter("message");
  try{
      if(StringUtils.equalsIgnoreCase(act,"DEL")){
        if(SchedulerService.getInstance().removeSchedule(scheduleName, serverName)){
            message = "删除计划(" + scheduleName +"-"+serverName +")成功！";
        }else{
            message = "删除计划(" + scheduleName +"-"+serverName +")失败！";
        }
      } else if(StringUtils.equalsIgnoreCase(act,"START")){
          if(SchedulerService.getInstance().startSchedule(scheduleName, serverName)){
            message = "开始计划(" + scheduleName +"-"+serverName +")成功！";
          }else{
            message = "开始计划(" + scheduleName +"-"+serverName +")失败！";
          }
      } else if(StringUtils.equalsIgnoreCase(act,"PAUSE")){
          if(SchedulerService.getInstance().pauseSchedule(scheduleName, serverName)){
            message = "暂停计划(" + scheduleName +"-"+serverName +")成功！";
          }else{
            message = "暂停计划(" + scheduleName +"-"+serverName +")失败！";
          }
      }
  }catch (Exception e){
    message = e.getMessage();
  }
  if(StringUtils.isNotBlank(act)){
     message = URLEncoder.encode(message, "UTF-8");
     response.sendRedirect("schedule.jsp?message="+message);
  }
  if(message == null) message = "";
%>
<html>
<head>
<title>Sohu SMC Schedule System</title>
<style type="text/css">
    .table-b table td{border:1px solid black}
</style>
<script type="text/javascript">
  function deleteConfirm(scheduleName, serverName){
   if(confirm("确定要删除该配置项吗？")) {
     window.location="http://"+window.location.host + "/base/schedule.jsp?action=DEL&scheduleName="+ scheduleName +"&serverName=" + serverName;
   }
  }
</script>
</head>
<body>
    <center>
    <h1>SMC Schedule System</h1>
    <p><%= message %></p>
    <table width="80%" class="table-b" border="1" cellspacing="0" cellpadding="0">
        <tr>
            <td colspan="4" align="left">
                <a href="modifySchedule.jsp?act=init">创建</a>
            </td>
            <td colspan="4" align="right">
                <a href="index.jsp">首页</a>
            </td>
        </tr>
        <tr width="100%">
            <td align="center">计划名称</td>
            <td align="center">目标服务器名称</td>
            <td align="center">计划类型</td>
            <td align="center">计划策略</td>
            <td align="center">计划表达式</td>
            <td align="center">计划状态</td>
            <td align="center">服务器列表</td>
            <td align="center">操作</td>
        </tr>
        <%
            List<Schedule> scheduleList = SchedulerService.getInstance().listSchedule();
            for(Schedule each : scheduleList){
                String pauseScript = "<a href=\"schedule.jsp?action=PAUSE&scheduleName="+ each.getScheduleName() +"&serverName=" + each.getServerName() +"\">暂停</a>";
                //String deleteScript = "<a href=\"schedule.jsp?action=DEL&scheduleName="+ each.getScheduleName() +"&serverName=" + each.getServerName() +"\">删除</a>";
                String deleteScript = "<a href=\"javascript:deleteConfirm('"+ each.getScheduleName() +"','"+ each.getServerName() +"');\">删除</a>";
                String startScript = "<a href=\"schedule.jsp?action=START&scheduleName="+ each.getScheduleName() +"&serverName=" + each.getServerName() +"\">开始</a>";
                String modifyScript = "<a href=\"modifySchedule.jsp?act=show&scheduleName=" + each.getScheduleName() +"&serverName=" + each.getServerName() + "&scheduleCron=" + each.getScheduleCron()+ "&strategy=" + each.getStrategy() + "&scheduleType=" + each.getScheduleType() +"\">修改</a>";
                out.println("<tr>");
                out.println("<td align=\"center\">" + each.getScheduleName()+"</td>");
                out.println("<td align=\"center\">" + each.getServerName()+"</td>");
                out.println("<td align=\"center\">" + each.getScheduleType()+"</td>");
                out.println("<td align=\"center\">" + (StringUtils.equals(each.getScheduleType(), "single") ? each.getStrategy() : "") +"</td>");
                out.println("<td align=\"center\">" + each.getScheduleCron()+"</td>");
                String action = "";
                String status = "";
                switch(each.getStatus()){
                    case 0 : {
                        status = "<font color='#FF0000'>不存在</font>";
                        action = deleteScript;
                        break;
                    }
                    case 1 : {
                        status = "<font color='#66FF66'>运行中</font>";
                        action = pauseScript + " | " + deleteScript +" | " + modifyScript;
                        break;
                    }
                    case 2 : {
                        status = "<font color='#FF0000'>暂停</font>";
                        action = startScript + " | " + deleteScript +" | " + modifyScript;
                        break;
                    }
                    case 3 : {
                        action = deleteScript +" | " + modifyScript;
                        status = "<font color='#0000FF'>已完成</font>"; break;
                    }
                    case 4 : {
                        action = deleteScript;
                        status = "<font color='#FF9900'>出错</font>"; break;
                    }
                }
                out.println("<td align=\"center\">" + status +"</td>");
                List<ServiceInstance<String>> servers = DiscoveryUtil.getAllService(each.getServerName(), true);
                StringBuilder sb = new StringBuilder();
                for(ServiceInstance<String> server : servers){
                    sb.append(server.getAddress()).append(":").append(server.getPort()).append("</br>");
                }
                out.println("<td align=\"center\">" + sb.toString() +"</td>");
                out.println("<td align=\"center\">" + action +"</td>");
                out.println("</tr>");

            }
        %>

    </table>
    </center>
</body>
</html>