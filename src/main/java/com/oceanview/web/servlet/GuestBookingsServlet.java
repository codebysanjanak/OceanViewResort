package com.oceanview.web.servlet;

import com.oceanview.facade.BookingFacade;
import com.oceanview.model.Reservation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/guest/bookings")
public class GuestBookingsServlet extends HttpServlet {
    private final BookingFacade bookingFacade = new BookingFacade();

    private boolean isValidReservation(Reservation r) {
        if (r == null) return false;

        String rn = r.getReservationNo();
        if (rn == null || rn.isBlank()) return false;

        // optional: make it stricter so dummy objects never pass
        // if (!rn.startsWith("RSV-")) return false;

        if (r.getRoomTypeId() <= 0) return false;
        if (r.getCheckIn() == null) return false;
        if (r.getCheckOut() == null) return false;

        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Integer guestId = (session == null) ? null : (Integer) session.getAttribute("guestId");

        if (guestId == null) {
            resp.sendRedirect(req.getContextPath() + "/guest/login");
            return;
        }

        String action = req.getParameter("action");
        String reservationNo = req.getParameter("reservationNo");

        if ("cancel".equals(action) && reservationNo != null && !reservationNo.isBlank()) {
            bookingFacade.cancelBooking(guestId, reservationNo);
            req.setAttribute("success", "Booking cancelled: " + reservationNo);
        }

        // ✅ get bookings
        List<Reservation> raw = bookingFacade.getGuestBookings(guestId);

        // ✅ KEEP ONLY REAL BOOKINGS (this removes the blank Cancel row)
        List<Reservation> bookings = new ArrayList<>();
        if (raw != null) {
            for (Reservation r : raw) {
                if (isValidReservation(r)) bookings.add(r);
            }
        }

        req.setAttribute("bookings", bookings); // this will be EMPTY if no real bookings
        req.getRequestDispatcher("/guest-bookings.jspx").forward(req, resp);
    }
}