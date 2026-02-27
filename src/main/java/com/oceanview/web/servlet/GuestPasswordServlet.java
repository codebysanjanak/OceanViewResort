package com.oceanview.web.servlet;

import com.oceanview.facade.ProfileFacade;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/guest/password")
public class GuestPasswordServlet extends HttpServlet {
    private final ProfileFacade profileFacade = new ProfileFacade();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("guest-password.jspx").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int guestId = (int) req.getSession().getAttribute("guestId");
        try {
            profileFacade.changeGuestPassword(
                    guestId,
                    req.getParameter("oldPassword"),
                    req.getParameter("newPassword")
            );
            req.setAttribute("success", "Password changed successfully");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher("guest-password.jspx").forward(req, resp);
    }
}