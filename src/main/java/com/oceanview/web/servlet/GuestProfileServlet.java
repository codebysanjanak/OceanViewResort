package com.oceanview.web.servlet;

import com.oceanview.facade.ProfileFacade;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/guest/profile")
public class GuestProfileServlet extends HttpServlet {
    private final ProfileFacade profileFacade = new ProfileFacade();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int guestId = (int) req.getSession().getAttribute("guestId");
        req.setAttribute("guest", profileFacade.getGuestProfile(guestId));
        req.getRequestDispatcher("guest-profile.jspx").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int guestId = (int) req.getSession().getAttribute("guestId");
        try {
            profileFacade.updateGuestProfile(
                    guestId,
                    req.getParameter("name"),
                    req.getParameter("phone"),
                    req.getParameter("address")
            );
            req.setAttribute("success", "Profile updated successfully");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        req.setAttribute("guest", profileFacade.getGuestProfile(guestId));
        req.getRequestDispatcher("guest-profile.jspx").forward(req, resp);
    }
}