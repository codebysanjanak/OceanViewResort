package com.oceanview.web.servlet;

import com.oceanview.dao.DAOFactory;
import com.oceanview.model.RoomType;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/admin/roomtypes")
public class AdminRoomTypeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // toggle active
        String action = req.getParameter("action");
        String idStr = req.getParameter("id");
        if ("toggle".equals(action) && idStr != null) {
            int id = Integer.parseInt(idStr);
            RoomType rt = DAOFactory.roomTypeDAO().findById(id);
            if (rt != null) DAOFactory.roomTypeDAO().setActive(id, !rt.isActive());
        }

        req.setAttribute("rooms", DAOFactory.roomTypeDAO().findAll());
        req.getRequestDispatcher("/WEB-INF/views/admin-roomtypes.jspx").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String idStr = req.getParameter("roomTypeId");

            RoomType rt = new RoomType();
            if (idStr != null && !idStr.isBlank()) rt.setRoomTypeId(Integer.parseInt(idStr));

            rt.setTypeName(req.getParameter("typeName"));
            rt.setNightlyRate(new BigDecimal(req.getParameter("rate")));
            rt.setCapacity(Integer.parseInt(req.getParameter("capacity")));
            rt.setActive(req.getParameter("active") != null);

            if (rt.getTypeName() == null || rt.getTypeName().isBlank()) throw new IllegalArgumentException("Type name required");
            if (rt.getNightlyRate().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Rate must be > 0");
            if (rt.getCapacity() <= 0) throw new IllegalArgumentException("Capacity must be > 0");

            if (rt.getRoomTypeId() > 0) DAOFactory.roomTypeDAO().update(rt);
            else DAOFactory.roomTypeDAO().create(rt);

            req.setAttribute("success", "Saved successfully");
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }

        req.setAttribute("rooms", DAOFactory.roomTypeDAO().findAll());
        req.getRequestDispatcher("/WEB-INF/views/admin-roomtypes.jspx").forward(req, resp);
    }
}