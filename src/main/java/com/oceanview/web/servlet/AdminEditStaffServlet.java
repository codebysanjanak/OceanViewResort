package com.oceanview.web.servlet;

import com.oceanview.model.Admin;
import com.oceanview.service.AdminService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/admin/staff/edit")
public class AdminEditStaffServlet extends HttpServlet {

    private final AdminService service = new AdminService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");

        try {
            int id = Integer.parseInt(idStr);
            Admin staff = service.getAdmin(id);

            req.setAttribute("editStaff", staff);
            req.setAttribute("staff", service.getAllStaff());

            req.getRequestDispatcher("/staff.jspx").forward(req, resp);

        } catch (Exception ex) {
            String err = URLEncoder.encode("Invalid staff id", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/admin/staff?error=" + err);
        }
    }
}