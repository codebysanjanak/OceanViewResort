package com.oceanview.web.servlet;

import com.oceanview.dao.DAOFactory;
import com.oceanview.model.RoomType;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@WebServlet("/admin/roomtypes")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 20 * 1024 * 1024
)
public class AdminRoomTypeServlet extends HttpServlet {

    // Put JSPX here (recommended)
    private static final String VIEW = "/admin-roomtypes.jspx";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        String idStr = req.getParameter("id");

        if (action != null && idStr != null && !idStr.isBlank()) {
            int id = Integer.parseInt(idStr);

            switch (action) {
                case "toggle" -> {
                    RoomType rt = DAOFactory.roomTypeDAO().findById(id);
                    if (rt != null) DAOFactory.roomTypeDAO().setActive(id, !rt.isActive());
                    resp.sendRedirect(req.getContextPath() + "/admin/roomtypes?msg=toggled");
                    return;
                }

                case "delete" -> {
                    DAOFactory.roomTypeDAO().delete(id);
                    resp.sendRedirect(req.getContextPath() + "/admin/roomtypes?msg=deleted");
                    return;
                }

                case "edit" -> {
                    RoomType rt = DAOFactory.roomTypeDAO().findById(id);
                    if (rt != null) {
                        req.setAttribute("form", rt);
                    }
                }
            }
        }

        List<RoomType> rooms = DAOFactory.roomTypeDAO().findAll();
        req.setAttribute("rooms", rooms);

        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            req.setCharacterEncoding("UTF-8");

            String roomTypeId = trim(req.getParameter("roomTypeId"));
            String typeName = trim(req.getParameter("typeName"));
            String rateStr = trim(req.getParameter("rate"));
            String adultsStr = trim(req.getParameter("adultsCount"));
            String childrenStr = trim(req.getParameter("childrenCount"));
            String roomsStr = trim(req.getParameter("roomsCount"));
            String description = trim(req.getParameter("description"));
            boolean active = "true".equals(req.getParameter("active"));

            // server required validation
            if (typeName.isBlank()) throw new IllegalArgumentException("Room Name is required");
            if (rateStr.isBlank()) throw new IllegalArgumentException("Nightly Rate is required");
            if (adultsStr.isBlank()) throw new IllegalArgumentException("Adults Count is required");
            if (childrenStr.isBlank()) throw new IllegalArgumentException("Children Count is required");
            if (roomsStr.isBlank()) throw new IllegalArgumentException("Rooms Count is required");

            BigDecimal rate = new BigDecimal(rateStr);
            int adults = Integer.parseInt(adultsStr);
            int children = Integer.parseInt(childrenStr);
            int roomsCount = Integer.parseInt(roomsStr);

            if (rate.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Rate must be >= 0");
            if (adults < 0 || children < 0 || roomsCount < 0) throw new IllegalArgumentException("Counts must be >= 0");

            RoomType rt = new RoomType();
            rt.setTypeName(typeName);
            rt.setNightlyRate(rate);
            rt.setAdultsCount(adults);
            rt.setChildrenCount(children);
            rt.setRoomsCount(roomsCount);
            rt.setDescription(description);
            rt.setActive(active);

            // keep existing photo if no new photo
            String existingPhotoPath = trim(req.getParameter("existingPhotoPath"));
            if (!existingPhotoPath.isBlank()) rt.setPhotoPath(existingPhotoPath);

            // upload new photo
            Part photoPart = req.getPart("photo");
            if (photoPart != null && photoPart.getSize() > 0) {

                String submitted = photoPart.getSubmittedFileName();
                String ext = getExtension(submitted);

                if (!isAllowedImageExt(ext)) {
                    throw new IllegalArgumentException("Only jpg, jpeg, png, webp allowed");
                }

                String fileName = "rt_" + UUID.randomUUID() + ext;

                String relativeDir = "/images/roomtypes";
                String absoluteDir = getServletContext().getRealPath(relativeDir);
                if (absoluteDir == null) {
                    throw new IllegalStateException("Upload path not available. Deploy as exploded folder in Tomcat.");
                }

                Files.createDirectories(Paths.get(absoluteDir));

                Path target = Paths.get(absoluteDir, fileName);
                try (InputStream in = photoPart.getInputStream()) {
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                }

                rt.setPhotoPath(relativeDir + "/" + fileName);
            }

            // PRG redirect (toast uses msg param)
            if (!roomTypeId.isBlank()) {
                rt.setRoomTypeId(Integer.parseInt(roomTypeId));
                DAOFactory.roomTypeDAO().update(rt);
                resp.sendRedirect(req.getContextPath() + "/admin/roomtypes?msg=updated");
            } else {
                DAOFactory.roomTypeDAO().create(rt);
                resp.sendRedirect(req.getContextPath() + "/admin/roomtypes?msg=saved");
            }
            return;

        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/admin/roomtypes?error=" + urlEncode(e.getMessage()));
        }
    }

    private static String trim(String s) { return (s == null) ? "" : s.trim(); }

    private static String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s == null ? "" : s, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    private static String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot < 0) return "";
        return filename.substring(dot).toLowerCase();
    }

    private static boolean isAllowedImageExt(String ext) {
        return ".jpg".equals(ext) || ".jpeg".equals(ext) || ".png".equals(ext) || ".webp".equals(ext);
    }
}