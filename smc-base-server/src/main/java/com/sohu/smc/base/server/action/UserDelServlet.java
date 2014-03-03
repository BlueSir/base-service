package com.sohu.smc.base.server.action;

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
public class UserDelServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("userName");
        UserService.del(userName);
        resp.sendRedirect("/base/comm/usermanage.jsp");
    }

}
