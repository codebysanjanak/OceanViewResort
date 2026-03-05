package com.oceanview.web.servlet;

import com.oceanview.service.AdminService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/admin/staff/add")
public class AdminAddStaffServlet extends HttpServlet {

    private final AdminService service = new AdminService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        try {
            service.createStaff(username, password, role);
            resp.sendRedirect(req.getContextPath() + "/admin/staff?msg=created");
        } catch (IllegalArgumentException ex) {
            String err = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/admin/staff?error=" + err);
        }
    }
}