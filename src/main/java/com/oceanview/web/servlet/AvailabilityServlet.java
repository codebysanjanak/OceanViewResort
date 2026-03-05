package com.oceanview.web.servlet;

import com.oceanview.dao.DAOFactory;
import com.oceanview.model.RoomType;
import com.oceanview.service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/availability")
public class AvailabilityServlet extends HttpServlet {

    private static final String VIEW = "/availability.jspx";
    private final BookingService bookingService = new BookingService();

    public static class AvailabilityRow {
        private final RoomType roomType;
        private final boolean available;
        private final String reason;

        public AvailabilityRow(RoomType roomType, boolean available, String reason) {
            this.roomType = roomType;
            this.available = available;
            this.reason = reason;
        }

        public RoomType getRoomType() {
            return roomType;
        }

        public boolean isAvailable() {
            return available;
        }

        public String getReason() {
            return reason;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // load active room types for dropdown & listing
        List<RoomType> rooms = DAOFactory.roomTypeDAO().findAllActive();
        req.setAttribute("rooms", rooms);

        // read query params
        String inStr = trim(req.getParameter("checkIn"));
        String outStr = trim(req.getParameter("checkOut"));

        req.setAttribute("checkIn", inStr);
        req.setAttribute("checkOut", outStr);

        // if dates are provided -> compute availability per room type
        if (!inStr.isBlank() && !outStr.isBlank()) {
            try {
                LocalDate in = LocalDate.parse(inStr);
                LocalDate out = LocalDate.parse(outStr);

                bookingService.validateDates(in, out);

                List<AvailabilityRow> rows = new ArrayList<>();
                for (RoomType rt : rooms) {
                    try {
                        bookingService.checkCapacity(rt.getRoomTypeId(), in, out);
                        rows.add(new AvailabilityRow(rt, true, "Available"));
                    } catch (Exception e) {
                        rows.add(new AvailabilityRow(rt, false, e.getMessage()));
                    }
                }
                req.setAttribute("results", rows);
            } catch (Exception e) {
                req.setAttribute("error", e.getMessage());
            }
        }

        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    private static String trim(String s) {
        return (s == null) ? "" : s.trim();
    }
}