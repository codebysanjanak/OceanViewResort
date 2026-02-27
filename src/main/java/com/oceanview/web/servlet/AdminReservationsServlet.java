package com.oceanview.web.servlet;

import com.oceanview.dao.DAOFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/admin/reservations")
public class AdminReservationsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("reservations", DAOFactory.reservationDAO().findAll());
        req.getRequestDispatcher("admin-reservations.jspx").forward(req, resp);
    }
}