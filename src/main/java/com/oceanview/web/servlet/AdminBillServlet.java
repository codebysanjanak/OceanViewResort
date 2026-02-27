package com.oceanview.web.servlet;

import com.oceanview.model.Bill;
import com.oceanview.service.BillingService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/admin/bill")
public class AdminBillServlet extends HttpServlet {
    private final BillingService billingService = new BillingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reservationNo = req.getParameter("reservationNo");
        if (reservationNo != null && !reservationNo.isBlank()) {
            try {
                Bill bill = billingService.generateBill(reservationNo);
                req.setAttribute("bill", bill);
                req.setAttribute("success", "Bill generated (or loaded) for " + reservationNo);
            } catch (Exception e) {
                req.setAttribute("error", e.getMessage());
            }
        }
        req.getRequestDispatcher("/WEB-INF/views/admin-bill.jspx").forward(req, resp);
    }
}