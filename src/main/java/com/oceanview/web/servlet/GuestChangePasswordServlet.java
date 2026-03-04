package com.oceanview.web.servlet;

import com.oceanview.service.ProfileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/guest/password")
public class GuestChangePasswordServlet extends HttpServlet {

    private final ProfileService profileService = new ProfileService();

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

            String oldPw = req.getParameter("oldPassword");
            String newPw = req.getParameter("newPassword");
            String confirm = req.getParameter("confirmPassword");

            oldPw = oldPw == null ? "" : oldPw.trim();
            newPw = newPw == null ? "" : newPw.trim();
            confirm = confirm == null ? "" : confirm.trim();

            if (!newPw.equals(confirm)) {
                throw new IllegalArgumentException("New password and confirm password do not match");
            }

            profileService.changeGuestPassword(guestId, oldPw, newPw);

            resp.sendRedirect(req.getContextPath() + "/guest/profile?success=" +
                    urlEncode("Password changed successfully"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/guest/profile?error=" + urlEncode(messageOf(e)));
        }
    }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }

    private static String messageOf(Exception e) {
        String m = (e == null) ? "" : e.getMessage();
        return (m == null || m.isBlank()) ? "Something went wrong" : m;
    }
}