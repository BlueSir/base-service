package com.sohu.smc.base.server.action;

import com.sohu.smc.base.server.util.MD5Util;
import com.sohu.smc.redis.SmcJedis;
import com.sohu.smc.redis.SmcJedisFactory;
import org.apache.commons.lang.StringUtils;
import sun.security.provider.MD5;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 1/7/14
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("username");
        String password = req.getParameter("passwd");
//        String md5Pass = MD5Util.Md5(password);
        boolean exist = UserService.exsist(userName);
        if(!exist){
            resp.getWriter().println("该用户名不存在！");
            return;
        }
        String login = UserService.login(userName, password);

        if(StringUtils.equals(login, "@none-verify")){
            resp.getWriter().println("账号未审核通过，等待审核！");
        } else if(StringUtils.isNotBlank(login)){
            resp.getWriter().println(100);
            if(StringUtils.equals(userName,"admin")){
                req.getSession().setAttribute("admin",login);
            }
            req.getSession().setAttribute("auth",login);
        }else{
            resp.getWriter().println("用户名与密码不匹配，请重试！");
        }
    }

}
