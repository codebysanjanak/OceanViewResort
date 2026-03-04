package com.oceanview.web.servlet;

import com.oceanview.service.RoomService;
import com.oceanview.model.RoomType;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet({"/", "/home"})
public class HomeServlet extends HttpServlet {

    private static final String VIEW = "/home.jspx";
    private final RoomService roomService = new RoomService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<RoomType> rooms = roomService.listActiveRoomTypes();
        req.setAttribute("rooms", rooms);

        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}