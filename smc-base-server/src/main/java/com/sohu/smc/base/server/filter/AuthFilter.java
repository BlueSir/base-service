package com.sohu.smc.base.server.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 1/7/14
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        if (session.getAttribute("auth") != null || session.getAttribute("admin") != null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            res.sendRedirect("/base/login.jsp");
        }
    }

    @Override
    public void destroy() {

    }
}
