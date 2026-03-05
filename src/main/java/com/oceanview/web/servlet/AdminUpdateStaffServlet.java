package com.oceanview.web.servlet;

import com.oceanview.service.AdminService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/admin/staff/update")
public class AdminUpdateStaffServlet extends HttpServlet {

    private final AdminService service = new AdminService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String idStr = req.getParameter("adminId");
        String username = req.getParameter("username");
        String password = req.getParameter("password"); // optional in edit
        String role = req.getParameter("role");

        try {
            int id = Integer.parseInt(idStr);
            service.updateStaff(id, username, role, password);

            resp.sendRedirect(req.getContextPath() + "/admin/staff?msg=updated");

        } catch (IllegalArgumentException ex) {
            String err = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/admin/staff/edit?id=" + idStr + "&error=" + err);
        } catch (Exception ex) {
            String err = URLEncoder.encode("Update failed", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/admin/staff?error=" + err);
        }
    }
}