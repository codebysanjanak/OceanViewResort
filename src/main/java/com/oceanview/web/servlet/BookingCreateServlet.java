package com.oceanview.web.servlet;

import com.oceanview.dao.DAOFactory;
import com.oceanview.facade.BookingFacade;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/guest/booking/create")
public class BookingCreateServlet extends HttpServlet {
    private final BookingFacade bookingFacade = new BookingFacade();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("rooms", DAOFactory.roomTypeDAO().findAllActive());
        req.getRequestDispatcher("booking-create.jspx").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("rooms", DAOFactory.roomTypeDAO().findAllActive());
        try {
            int guestId = (int) req.getSession().getAttribute("guestId");
            int roomTypeId = Integer.parseInt(req.getParameter("roomTypeId"));
            LocalDate in = LocalDate.parse(req.getParameter("checkIn"));
            LocalDate out = LocalDate.parse(req.getParameter("checkOut"));

            String reservationNo = bookingFacade.createBooking(guestId, roomTypeId, in, out);
            req.setAttribute("success", "Booking created! Reservation No: " + reservationNo);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher("booking-create.jspx").forward(req, resp);
    }
}