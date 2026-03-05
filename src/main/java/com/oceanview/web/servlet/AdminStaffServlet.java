package com.oceanview.web.servlet;

import com.oceanview.service.AdminService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;

import java.io.IOException;

@WebServlet("/admin/staff")
public class AdminStaffServlet extends HttpServlet {

    private final AdminService service = new AdminService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("staff", service.getAllStaff());

        req.getRequestDispatcher("/staff.jspx")
                .forward(req, resp);
    }
}