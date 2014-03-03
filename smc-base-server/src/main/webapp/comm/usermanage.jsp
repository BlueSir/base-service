<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="base.jsp"%>
<%@ page import="java.util.List"%>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.sohu.smc.base.server.modle.User" %>
<%@ page import="com.sohu.smc.base.server.action.UserService" %>
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
      function deleteConfirm(key){
       if(confirm("确定要删除该用户吗？")) {
         window.location="http://"+window.location.host + "/base/userDel.do?userName="+key;
       }
      }
    </script>
</head>
<%
  request.setCharacterEncoding("utf-8");
  response.setCharacterEncoding("utf-8");
  String auth = (String)request.getSession().getAttribute("auth");
%>
<body>
<div class="container" id="mainDiv">
    </br>
    </br>
	    <table width="80%"  id="MyStretchGrid" class="table table-bordered datagrid">
            <thead>
            <tr>
                <th colspan="8">
                    <div class="datagrid-header-center">
                        <span style="font-size: 17px" >所有用户列表</span>
                    </div>
                </th>
            </tr>
            </thead>
           <tr>
               <td width="30" align="center">用户名</td>
               <td width="20" align="center">姓名</td>
               <td width="20" align="center">状态</td>
               <td width="30" align="center">操作</td>
           </tr>
           <%
           if(StringUtils.equals(auth, "admin")){

               List<User> users = UserService.getAllUser();
               for(User each : users){
                if(StringUtils.equals(each.getUserName(), "admin")) continue;
           %>
           <tr>
               <td><%=each.getUserName()%></td>
               <td><%=each.getName()%></td>
               <td><%=(each.isVerify() ? "审核通过":"未审核通过")%></td>
               <td align="center">
               <%
                  if(!each.isVerify()){
               %>
               <a href="../userVerify.do?userName=<%=each.getUserName()%>">激活</a> |
               <%
                }
               %>
               <a href="javascript:deleteConfirm('<%=each.getUserName()%>')">删除</a></td>
           </tr>
           <%
               }
           }else {
           %>
            <tr>
                <td colspan="4">您没有这个权限。</td>
            </tr>
           <%
           }
           %>
        </table>
	</div>
</div>
</body>
</html>