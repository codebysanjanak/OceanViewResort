package com.oceanview.web.servlet;

import com.oceanview.dao.DAOFactory;
import com.oceanview.facade.BookingFacade;
import com.oceanview.model.RoomType;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/guest/booking/create")
public class GuestCreateBookingServlet extends HttpServlet {

    private static final String VIEW = "/booking-create.jspx";
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

        req.setAttribute("success", trim(req.getParameter("success")));
        req.setAttribute("error", trim(req.getParameter("error")));

        List<RoomType> rooms = DAOFactory.roomTypeDAO().findAll();
        req.setAttribute("rooms", rooms);

        req.setAttribute("selectedRoomTypeId", trim(req.getParameter("roomTypeId")));
        req.setAttribute("checkInVal", trim(req.getParameter("checkIn")));
        req.setAttribute("checkOutVal", trim(req.getParameter("checkOut")));

        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Integer guestId = (session == null) ? null : (Integer) session.getAttribute("guestId");
        if (guestId == null) {
            resp.sendRedirect(req.getContextPath() + "/guest/login");
            return;
        }

        try {
            req.setCharacterEncoding("UTF-8");

            String roomTypeIdStr = trim(req.getParameter("roomTypeId"));
            String checkInStr = trim(req.getParameter("checkIn"));
            String checkOutStr = trim(req.getParameter("checkOut"));

            // server-side required validation (same style as admin pages)
            if (roomTypeIdStr.isBlank()) throw new IllegalArgumentException("Room Type is required");
            if (checkInStr.isBlank()) throw new IllegalArgumentException("Check-in date is required");
            if (checkOutStr.isBlank()) throw new IllegalArgumentException("Check-out date is required");

            int roomTypeId = Integer.parseInt(roomTypeIdStr);
            LocalDate checkIn = LocalDate.parse(checkInStr);
            LocalDate checkOut = LocalDate.parse(checkOutStr);

            String reservationNo = bookingFacade.createBooking(guestId, roomTypeId, checkIn, checkOut);

            resp.sendRedirect(req.getContextPath() + "/guest/bookings?success=" +
                    urlEncode("Booking created: " + reservationNo));
            return;

        } catch (Exception e) {

            String roomTypeIdStr = trim(req.getParameter("roomTypeId"));
            String checkInStr = trim(req.getParameter("checkIn"));
            String checkOutStr = trim(req.getParameter("checkOut"));

            resp.sendRedirect(req.getContextPath() + "/guest/booking/create"
                    + "?error=" + urlEncode(messageOf(e))
                    + "&roomTypeId=" + urlEncode(roomTypeIdStr)
                    + "&checkIn=" + urlEncode(checkInStr)
                    + "&checkOut=" + urlEncode(checkOutStr));
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