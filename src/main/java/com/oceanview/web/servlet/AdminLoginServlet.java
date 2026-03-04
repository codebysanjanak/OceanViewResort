package com.oceanview.web.servlet;

import com.oceanview.dao.DAOFactory;
import com.oceanview.model.Admin;
import com.oceanview.security.PasswordHasher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {

    private final PasswordHasher hasher = new PasswordHasher();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/admin-login.jspx").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            username = (username == null) ? "" : username.trim();
            password = (password == null) ? "" : password.trim();

            if (username.isEmpty() || password.isEmpty()) {
                req.setAttribute("error", "Username and password are required");
                req.getRequestDispatcher("/admin-login.jspx").forward(req, resp);
                return;
            }

            Admin a = DAOFactory.adminDAO().findByUsername(username);

            if (a == null || !hasher.verify(password, a.getPasswordHash())) {
                req.setAttribute("error", "Invalid admin credentials");
                req.getRequestDispatcher("/admin-login.jspx").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("role", "ADMIN");
            session.setAttribute("adminId", a.getAdminId());
            session.setAttribute("adminUsername", a.getUsername());

            req.setAttribute("success", "Login successful! Redirecting...");
            req.setAttribute("redirectUrl", req.getContextPath() + "/admin/roomtypes");

            req.getRequestDispatcher("/admin-login.jspx").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/admin-login.jspx").forward(req, resp);
        }
    }
}