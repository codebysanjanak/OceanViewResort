package com.oceanview.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {"/guest/*", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        String role = (String) req.getSession().getAttribute("role");

        // Allow public guest auth pages
        if (path.startsWith("/guest/login") || path.startsWith("/guest/register")) {
            chain.doFilter(request, response);
            return;
        }
        // Allow admin login
        if (path.startsWith("/admin/login")) {
            chain.doFilter(request, response);
            return;
        }

        if (role == null) {
            resp.sendRedirect(req.getContextPath() + "/guest/login");
            return;
        }

        // Role based protection
        if (path.startsWith("/admin") && !"ADMIN".equals(role)) {
            resp.sendError(403, "Forbidden");
            return;
        }
        if (path.startsWith("/guest") && !"GUEST".equals(role)) {
            resp.sendError(403, "Forbidden");
            return;
        }

        chain.doFilter(request, response);
    }
}