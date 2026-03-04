package com.oceanview.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/guest/bookings")
public class GuestBookingsServlet extends HttpServlet {

    private static final String VIEW = "/guest-bookings.jspx";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        Integer guestId = (session == null)
                ? null
                : (Integer) session.getAttribute("guestId");

        // if not logged in
        if (guestId == null) {
            resp.sendRedirect(req.getContextPath() + "/guest/login");
            return;
        }

        // get success/error messages from PRG
        req.setAttribute("success", req.getParameter("success"));
        req.setAttribute("error", req.getParameter("error"));

        // forward to JSPX page
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}