package com.sohu.smc.base.server.action;

import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 1/7/14
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogoutServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().removeAttribute("auth");
        req.getSession().removeAttribute("admin");
        resp.sendRedirect("/base/login.jsp");
    }

}
