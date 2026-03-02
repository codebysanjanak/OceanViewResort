package com.oceanview.web.servlet;

import com.oceanview.model.Guest;
import com.oceanview.service.GuestAuthService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/guest/login")
public class GuestLoginServlet extends HttpServlet {
    private final GuestAuthService authService = new GuestAuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/guest-login.jspx").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Guest g = authService.login(req.getParameter("email"), req.getParameter("password"));
            if (g == null) {
                req.setAttribute("error", "Invalid email or password");
                req.getRequestDispatcher("/guest-login.jspx").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("role", "GUEST");
            session.setAttribute("guestId", g.getGuestId());

            resp.sendRedirect(req.getContextPath() + "/guest/bookings");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/guest-login.jspx").forward(req, resp);
        }
    }
}