package com.oceanview.web.servlet;

import com.oceanview.service.RoomService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/availability")
public class AvailabilityServlet extends HttpServlet {
    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("rooms", roomService.listActiveRoomTypes());

        String roomTypeIdStr = req.getParameter("roomTypeId");
        String inStr = req.getParameter("checkIn");
        String outStr = req.getParameter("checkOut");

        if (roomTypeIdStr != null && inStr != null && outStr != null &&
                !roomTypeIdStr.isBlank() && !inStr.isBlank() && !outStr.isBlank()) {
            try {
                int roomTypeId = Integer.parseInt(roomTypeIdStr);
                LocalDate in = LocalDate.parse(inStr);
                LocalDate out = LocalDate.parse(outStr);

                req.setAttribute("result", roomService.filterAvailability(roomTypeId, in, out));
            } catch (Exception e) {
                req.setAttribute("error", e.getMessage());
            }
        }

        req.getRequestDispatcher("availability.jspx").forward(req, resp);
    }
}