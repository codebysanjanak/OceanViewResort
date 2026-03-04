package com.oceanview.web.servlet;

import com.oceanview.facade.BookingFacade;
import com.oceanview.model.Reservation;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/guest/bookings")
public class GuestBookingsServlet extends HttpServlet {

    private static final String VIEW = "/guest-bookings.jspx";
    private final BookingFacade bookingFacade = new BookingFacade();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Integer guestId = (session == null) ? null : (Integer) session.getAttribute("guestId");

        if (guestId == null) {
            resp.sendRedirect(req.getContextPath() + "/guest/login");
            return;
        }

        try {
            // ✅ handle cancel action (optional - your JSP has cancel link)
            String action = trim(req.getParameter("action"));
            String reservationNo = trim(req.getParameter("reservationNo"));

            if ("cancel".equalsIgnoreCase(action) && !reservationNo.isBlank()) {
                bookingFacade.cancelBooking(guestId, reservationNo);

                resp.sendRedirect(req.getContextPath() + "/guest/bookings?success=" +
                        urlEncode("Reservation cancelled: " + reservationNo));
                return;
            }

            // ✅ PRG toast messages
            req.setAttribute("success", trim(req.getParameter("success")));
            req.setAttribute("error", trim(req.getParameter("error")));

            // ✅ THIS IS THE MISSING PART (load bookings from DB)
            List<Reservation> bookings = bookingFacade.getGuestBookings(guestId);
            req.setAttribute("bookings", bookings);

            req.getRequestDispatcher(VIEW).forward(req, resp);

        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/guest/bookings?error=" + urlEncode(messageOf(e)));
        }
    }

    private static String trim(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }

    private static String messageOf(Exception e) {
        String m = (e == null) ? "" : e.getMessage();
        return (m == null || m.isBlank()) ? "Something went wrong" : m;
    }
}