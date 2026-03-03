package com.oceanview.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebFilter(urlPatterns = {"/guest/*", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

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

        // IMPORTANT: do not create a new session if not logged in
        HttpSession session = req.getSession(false);
        String role = (session == null) ? null : (String) session.getAttribute("role");

        // If not logged in, redirect based on area
        if (role == null) {
            if (path.startsWith("/admin/")) {
                resp.sendRedirect(req.getContextPath() + "/admin/login");
            } else {
                resp.sendRedirect(req.getContextPath() + "/guest/login");
            }
            return;
        }

        // Role based protection
        if (path.startsWith("/admin/") && !"ADMIN".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }

        if (path.startsWith("/guest/") && !"GUEST".equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }

        chain.doFilter(request, response);
    }
}