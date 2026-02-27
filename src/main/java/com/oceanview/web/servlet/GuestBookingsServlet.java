package com.oceanview.web.servlet;

import com.oceanview.facade.BookingFacade;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/guest/bookings")
public class GuestBookingsServlet extends HttpServlet {
    private final BookingFacade bookingFacade = new BookingFacade();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int guestId = (int) req.getSession().getAttribute("guestId");

        String action = req.getParameter("action");
        String reservationNo = req.getParameter("reservationNo");
        if ("cancel".equals(action) && reservationNo != null && !reservationNo.isBlank()) {
            bookingFacade.cancelBooking(guestId, reservationNo);
            req.setAttribute("success", "Booking cancelled: " + reservationNo);
        }

        req.setAttribute("bookings", bookingFacade.getGuestBookings(guestId));
        req.getRequestDispatcher("/WEB-INF/views/guest-bookings.jspx").forward(req, resp);
    }
}