package com.oceanview.web.servlet;

import com.oceanview.service.GuestAuthService;
import com.oceanview.service.ValidationException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/guest/register")
public class GuestRegisterServlet extends HttpServlet {
    private final GuestAuthService authService = new GuestAuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/guest-register.jspx").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> form = new HashMap<>();
        form.put("name", req.getParameter("name"));
        form.put("email", req.getParameter("email"));
        form.put("phone", req.getParameter("phone"));
        form.put("address", req.getParameter("address"));

        req.setAttribute("form", form);

        try {
            authService.register(
                    form.get("name"),
                    form.get("email"),
                    form.get("phone"),
                    form.get("address"),
                    req.getParameter("password"),
                    req.getParameter("confirmPassword")
            );

            // stay on register page, show toast, then JS will redirect
            req.setAttribute("success", "Registered successfully!, You'll redirect to login..");
            req.getRequestDispatcher("/guest-register.jspx").forward(req, resp);

        } catch (ValidationException ve) {
            req.setAttribute("errors", ve.getErrors());
            req.getRequestDispatcher("/guest-register.jspx").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/guest-register.jspx").forward(req, resp);
        }
    }
}