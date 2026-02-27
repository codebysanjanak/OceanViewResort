package com.oceanview.web.servlet;

import com.oceanview.service.RoomService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import java.io.IOException;

@WebServlet("/rooms")
public class RoomServlet extends HttpServlet {
    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("rooms", roomService.listActiveRoomTypes());
        req.getRequestDispatcher("/WEB-INF/views/rooms.jspx").forward(req, resp);
    }
}