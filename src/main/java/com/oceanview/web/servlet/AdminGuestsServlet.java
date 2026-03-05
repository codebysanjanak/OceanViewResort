package com.oceanview.web.servlet;

import com.oceanview.model.Guest;
import com.oceanview.service.GuestService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/guests")
public class AdminGuestsServlet extends HttpServlet {

    private final GuestService guestService = new GuestService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Guest> guests = guestService.getAllGuests();

        req.setAttribute("guests", guests);

        req.getRequestDispatcher("/guests.jspx")
                .forward(req, resp);
    }
}