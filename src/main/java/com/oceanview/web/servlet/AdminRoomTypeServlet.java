package com.oceanview.web.servlet;

import com.oceanview.dao.DAOFactory;
import com.oceanview.model.RoomType;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.math.BigDecimal;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.oceanview.model.RoomType;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/admin/roomtypes")
public class AdminRoomTypeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        String idStr = req.getParameter("id");

        if ("toggle".equals(action) && idStr != null) {
            int id = Integer.parseInt(idStr);
            RoomType rt = DAOFactory.roomTypeDAO().findById(id);
            if (rt != null) {
                DAOFactory.roomTypeDAO().setActive(id, !rt.isActive());
            }
        }

        req.setAttribute("rooms", DAOFactory.roomTypeDAO().findAll());

        req.getRequestDispatcher("/admin-roomtypes.jspx").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Map<String,String> form = new HashMap<>();
        form.put("typeName", req.getParameter("typeName"));
        form.put("rate", req.getParameter("rate"));
        form.put("capacity", req.getParameter("capacity"));
        form.put("active", req.getParameter("active"));

        req.setAttribute("form", form);

        Map<String,String> errors = new LinkedHashMap<>();

        if (form.get("typeName") == null || form.get("typeName").isBlank())
            errors.put("typeName", "Room name is required");

        if (form.get("rate") == null || form.get("rate").isBlank())
            errors.put("rate", "Rate is required");

        if (form.get("capacity") == null || form.get("capacity").isBlank())
            errors.put("capacity", "Capacity is required");

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("rooms", DAOFactory.roomTypeDAO().findAll());
            req.getRequestDispatcher("/admin-roomtypes.jspx").forward(req, resp);
            return;
        }

        try {
            RoomType rt = new RoomType();
            rt.setTypeName(form.get("typeName").trim());
            rt.setNightlyRate(new BigDecimal(form.get("rate")));
            rt.setCapacity(Integer.parseInt(form.get("capacity")));
            rt.setActive("true".equals(form.get("active")));

            DAOFactory.roomTypeDAO().create(rt);

            req.setAttribute("success", "Room type saved successfully");

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }

        req.setAttribute("rooms", DAOFactory.roomTypeDAO().findAll());
        req.getRequestDispatcher("/admin-roomtypes.jspx").forward(req, resp);
    }
}