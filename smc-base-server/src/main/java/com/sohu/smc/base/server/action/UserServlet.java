package com.sohu.smc.base.server.action;

import com.sohu.smc.base.server.modle.User;
import com.sohu.smc.base.server.util.MD5Util;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 1/7/14
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserServlet implements Servlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        String userName = servletRequest.getParameter("username");
        String password = servletRequest.getParameter("passwd");
        String name = servletRequest.getParameter("name");
        String action = servletRequest.getParameter("action");
//        String md5Pass = MD5Util.Md5(password);
        User user = new User();
        user.setUserName(userName);
        user.setPassword(password);
        user.setName(name);
        if(StringUtils.equals(action, "ADD")){
            boolean isExist = UserService.exsist(userName);
            if(isExist){
                servletResponse.getWriter().println("该用户名已经存在！");
                return;
            }
            boolean isSucc = UserService.regist(user);
            if(isSucc){
                servletResponse.getWriter().println(100);
            }else{
                servletResponse.getWriter().println("注册失败，请稍候再试！");
            }
        }else if(StringUtils.equals(action, "UP")){
            String newPasswd = servletRequest.getParameter("newPasswd");
            String login = UserService.login(userName, password);
            user.setPassword(newPasswd);
            if(StringUtils.isBlank(login)){
                servletResponse.getWriter().println("账号或旧密码错误！");
                return;
            }else if (StringUtils.equals(login, "@none-verify")){
                servletResponse.getWriter().println("账号未审核通过");
                return;
            }
            boolean isSucc = UserService.modify(user);
            if(isSucc){
                servletResponse.getWriter().println(100);
            }else{
                servletResponse.getWriter().println("注册失败，请稍候再试！");
            }
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {
    }
}
