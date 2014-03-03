<html lang="utf-8"><head>
<meta charset="utf-8">
<title>后台管理系统V1.0</title>
<%@ page language="java"%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<link rel="stylesheet" type="text/css" href="css/_bootstrap.css">
<style id="clearly_highlighting_css" type="text/css">/* selection */ html.clearly_highlighting_enabled ::-moz-selection { background: rgba(246, 238, 150, 0.99); } html.clearly_highlighting_enabled ::selection { background: rgba(246, 238, 150, 0.99); } /* cursor */ html.clearly_highlighting_enabled {    /* cursor and hot-spot position -- requires a default cursor, after the URL one */    cursor: url("chrome-extension://pioclpoplcdbaefihamjohnefbikjilc/clearly/images/highlight--cursor.png") 14 16, text; } /* highlight tag */ em.clearly_highlight_element {    font-style: inherit !important; font-weight: inherit !important;    background-image: url("chrome-extension://pioclpoplcdbaefihamjohnefbikjilc/clearly/images/highlight--yellow.png");    background-repeat: repeat-x; background-position: top left; background-size: 100% 100%; } /* the delete-buttons are positioned relative to this */ em.clearly_highlight_element.clearly_highlight_first { position: relative; } /* delete buttons */ em.clearly_highlight_element a.clearly_highlight_delete_element {    display: none; cursor: pointer;    padding: 0; margin: 0; line-height: 0;    position: absolute; width: 34px; height: 34px; left: -17px; top: -17px;    background-image: url("chrome-extension://pioclpoplcdbaefihamjohnefbikjilc/clearly/images/highlight--delete-sprite.png"); background-repeat: no-repeat; background-position: 0px 0px; } em.clearly_highlight_element a.clearly_highlight_delete_element:hover { background-position: -34px 0px; } /* retina */ @media (min--moz-device-pixel-ratio: 2), (-webkit-min-device-pixel-ratio: 2), (min-device-pixel-ratio: 2) {    em.clearly_highlight_element { background-image: url("chrome-extension://pioclpoplcdbaefihamjohnefbikjilc/clearly/images/highlight--yellow@2x.png"); }    em.clearly_highlight_element a.clearly_highlight_delete_element { background-image: url("chrome-extension://pioclpoplcdbaefihamjohnefbikjilc/clearly/images/highlight--delete-sprite@2x.png"); background-size: 68px 34px; } } </style><style>[touch-action="none"]{ -ms-touch-action: none; touch-action: none; }[touch-action="pan-x"]{ -ms-touch-action: pan-x; touch-action: pan-x; }[touch-action="pan-y"]{ -ms-touch-action: pan-y; touch-action: pan-y; }[touch-action="scroll"],[touch-action="pan-x pan-y"],[touch-action="pan-y pan-x"]{ -ms-touch-action: pan-x pan-y; touch-action: pan-x pan-y; }</style></head>
<body>
<!-- NavBar -->
<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container-fluid">
            <a class="brand" href="index.html">
                <strong>SMC后台管理系统</strong>
            </a>
        </div>
    </div>
</div>

<div class="container-fluid ">
    <table width="900" border="0" align="center" cellpadding="0" cellspacing="0">
        <tbody>
        <tr>
            <td height="158" colspan="3">&nbsp;</td>
        </tr>
        <tr>
            <td width="280" height="210">&nbsp;</td>
            <td style="background-color:#2C2C2C">
                <table width="100%" border="0" align="center" cellpadding="0" cellspacing="6">
                    <form></form>
                    <tbody>

                    <tr>
                        <td width="29%" height="50" align="right" style="font-size:14px;color:#FFF">用户名：</td>
                        <td width="41%"><input type="text" id="username" placeholder="用户名" style="WIDTH: 150px; HEIGHT: 25px;"></td>
                        <td width="30%" height="50" align="left" style="font-size:14px;color:#FFF">用户名为字母数字组成</td>
                    </tr>
                    <tr>
                        <td height="50" align="right" style="font-size:14px;color:#FFF">密&nbsp;&nbsp;码：</td>
                        <td><input type="password" id="password" placeholder="密码" style="WIDTH: 150px; HEIGHT: 25px"></td>
                        <td height="50" align="left" style="font-size:14px;color:#FFF">密码不能为空</td>
                    </tr>
                    <tr>
                        <td height="50" align="right" style="font-size:14px;color:#FFF">再输一次密码：</td>
                        <td><input type="password" id="password_again" placeholder="再输一次密码" style="WIDTH: 150px; HEIGHT: 25px"></td>
                        <td height="50" align="left" style="font-size:14px;color:#FFF">与第一次输入的密码一致</td>
                    </tr>
                    <tr>
                        <td height="50" align="right" style="font-size:14px;color:#FFF">姓&nbsp;&nbsp;名：</td>
                        <td><input type="text" id="name" placeholder="姓名" style="WIDTH: 150px; HEIGHT: 25px"></td>
                        <td height="50" align="left" style="font-size:14px;color:#FFF">姓名不能为空</td>
                    </tr>
                    <tr>
                        <td height="40" colspan="3" align="center" style="padding-top:10px;">
                            <button class="btn" id="register" style="width:100px">注册</button>
                        </td>
                    </tr>

                    </tbody>
                    </table>
                </td>
            <td width="87">&nbsp;</td>
        </tr>
        <tr>
            <td height="53" colspan="3"><p>&nbsp;</p></td>
        </tr>
        </tbody></table>
</div>
<!-- End Container -->

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/jquery.cookie.js"></script>
<script type="text/javascript">
    $("#register").click(function(){
        var username= $('#username').val();
        var password= $("#password").val();
        var name= $("#name").val();
        var action = $("#action").val();
        if(username=='' || password=='' || name == '') {
            alert('请输入用户名、密码或姓名');
            return false;
        }
        $.ajax({
            url: 'user.do?username='+username+'&passwd='+password+'&name='+name+'&action=ADD',
            dataType: "text",
            success: function(data){
                if(data == 100) {
                    location.href="/base/login.jsp";
                }else{
                    alert(data);
                }
            },
            error:function(data){
                return false;
            }
        });
        return false;
    });
</script>

</body>
</html>