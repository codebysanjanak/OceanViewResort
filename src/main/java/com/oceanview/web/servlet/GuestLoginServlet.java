package com.oceanview.web.servlet;

import com.oceanview.model.Guest;
import com.oceanview.service.GuestAuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/guest/login")
public class GuestLoginServlet extends HttpServlet {

    private final GuestAuthService authService = new GuestAuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/guest-login.jspx").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            email = (email == null) ? "" : email.trim();
            password = (password == null) ? "" : password.trim();

            if (email.isEmpty() || password.isEmpty()) {
                req.setAttribute("error", "Email and password are required");
                req.getRequestDispatcher("/guest-login.jspx").forward(req, resp);
                return;
            }

            Guest g = authService.login(email, password);

            if (g == null) {
                req.setAttribute("error", "Invalid email or password");
                req.getRequestDispatcher("/guest-login.jspx").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("role", "GUEST");
            session.setAttribute("guestId", g.getGuestId());
            session.setAttribute("guestName", g.getName());
            session.setAttribute("guestEmail", g.getEmail());

            req.setAttribute("success", "Login successful! Redirecting...");
            req.setAttribute("redirectUrl", req.getContextPath() + "/guest/bookings");

            req.getRequestDispatcher("/guest-login.jspx").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/guest-login.jspx").forward(req, resp);
        }
    }
}