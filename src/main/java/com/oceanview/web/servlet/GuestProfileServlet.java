package com.oceanview.web.servlet;

import com.oceanview.model.Guest;
import com.oceanview.service.ProfileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/guest/profile")
public class GuestProfileServlet extends HttpServlet {

    private static final String VIEW = "/guest-profile.jspx";
    private final ProfileService profileService = new ProfileService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Integer guestId = (session == null) ? null : (Integer) session.getAttribute("guestId");

        if (guestId == null || !"GUEST".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/guest/login");
            return;
        }

        try {
            Guest g = profileService.getGuest(guestId);
            req.setAttribute("guest", g);

            // PRG messages
            req.setAttribute("success", trim(req.getParameter("success")));
            req.setAttribute("error", trim(req.getParameter("error")));

            req.getRequestDispatcher(VIEW).forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/guest/profile?error=" + urlEncode(messageOf(e)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Integer guestId = (session == null) ? null : (Integer) session.getAttribute("guestId");

        if (guestId == null || !"GUEST".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/guest/login");
            return;
        }

        try {
            req.setCharacterEncoding("UTF-8");

            String name = trim(req.getParameter("name"));
            String phone = trim(req.getParameter("phone"));
            String address = trim(req.getParameter("address"));

            profileService.updateGuestProfile(guestId, name, phone, address);

            // update session name (so header greeting updates)
            session.setAttribute("guestName", name);

            resp.sendRedirect(req.getContextPath() + "/guest/profile?success=" +
                    urlEncode("Profile updated successfully"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/guest/profile?error=" + urlEncode(messageOf(e)));
        }
    }

    private static String trim(String s) { return (s == null) ? "" : s.trim(); }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }

    private static String messageOf(Exception e) {
        String m = (e == null) ? "" : e.getMessage();
        return (m == null || m.isBlank()) ? "Something went wrong" : m;
    }
}