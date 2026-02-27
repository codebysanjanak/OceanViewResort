package com.oceanview.web.servlet;

import com.oceanview.service.GuestAuthService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/guest/register")
public class GuestRegisterServlet extends HttpServlet {
    private final GuestAuthService authService = new GuestAuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/guest-register.jspx").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = authService.register(
                    req.getParameter("name"),
                    req.getParameter("email"),
                    req.getParameter("phone"),
                    req.getParameter("address"),
                    req.getParameter("password")
            );
            req.setAttribute("success", "Registered successfully! Please login.");
            req.getRequestDispatcher("/WEB-INF/views/guest-login.jspx").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/guest-register.jspx").forward(req, resp);
        }
    }
}