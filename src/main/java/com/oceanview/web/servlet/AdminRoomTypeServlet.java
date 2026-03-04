package com.oceanview.web.servlet;

import com.oceanview.dao.DAOFactory;
import com.oceanview.model.RoomType;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@WebServlet("/admin/roomtypes")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,       // 1MB
        maxFileSize = 10 * 1024 * 1024,        // 10MB
        maxRequestSize = 20 * 1024 * 1024      // 20MB
)
public class AdminRoomTypeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        String idStr = req.getParameter("id");

        if (action != null && idStr != null) {
            int id = Integer.parseInt(idStr);

            switch (action) {

                case "toggle" -> {
                    RoomType rt = DAOFactory.roomTypeDAO().findById(id);
                    if (rt != null) DAOFactory.roomTypeDAO().setActive(id, !rt.isActive());
                    resp.sendRedirect(req.getContextPath() + "/admin/roomtypes");
                    return;
                }

                case "delete" -> {
                    DAOFactory.roomTypeDAO().delete(id);
                    resp.sendRedirect(req.getContextPath() + "/admin/roomtypes");
                    return;
                }

                case "edit" -> {
                    RoomType rt = DAOFactory.roomTypeDAO().findById(id);
                    if (rt != null) req.setAttribute("form", rt);
                }
            }
        }

        List<RoomType> rooms = DAOFactory.roomTypeDAO().findAll();
        req.setAttribute("rooms", rooms);

        req.getRequestDispatcher("/admin-roomtypes.jspx").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            RoomType rt = new RoomType();

            String idStr = req.getParameter("roomTypeId");

            rt.setTypeName(req.getParameter("typeName"));
            rt.setNightlyRate(new BigDecimal(req.getParameter("rate")));
            rt.setAdultsCount(Integer.parseInt(req.getParameter("adultsCount")));
            rt.setChildrenCount(Integer.parseInt(req.getParameter("childrenCount")));
            rt.setRoomsCount(Integer.parseInt(req.getParameter("roomsCount")));
            rt.setDescription(req.getParameter("description"));
            rt.setActive("true".equals(req.getParameter("active")));

            // Keep existing if no new upload
            String existingPhotoPath = req.getParameter("existingPhotoPath");
            rt.setPhotoPath((existingPhotoPath != null && !existingPhotoPath.isBlank()) ? existingPhotoPath : null);

            // =========================
            // PHOTO UPLOAD
            // =========================
            Part photoPart = req.getPart("photo"); // name="photo" in JSPX
            if (photoPart != null && photoPart.getSize() > 0) {

                String submitted = photoPart.getSubmittedFileName();
                String ext = getExtension(submitted);

                // allow only safe extensions
                if (!isAllowedImageExt(ext)) {
                    throw new IllegalArgumentException("Only jpg, jpeg, png, webp allowed");
                }

                String fileName = "rt_" + UUID.randomUUID() + ext;

                // Save under /images/roomtypes/
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

                // store web path in DB
                rt.setPhotoPath(relativeDir + "/" + fileName);
            }

            // =========================
            // CREATE / UPDATE
            // =========================
            if (idStr != null && !idStr.isBlank()) {
                rt.setRoomTypeId(Integer.parseInt(idStr));
                DAOFactory.roomTypeDAO().update(rt);
                req.setAttribute("success", "Room type updated successfully");
            } else {
                DAOFactory.roomTypeDAO().create(rt);
                req.setAttribute("success", "Room type saved successfully");
            }

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }

        List<RoomType> rooms = DAOFactory.roomTypeDAO().findAll();
        req.setAttribute("rooms", rooms);

        req.getRequestDispatcher("/admin-roomtypes.jspx").forward(req, resp);
    }

    private static String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot < 0) return "";
        return filename.substring(dot).toLowerCase(); // includes dot
    }

    private static boolean isAllowedImageExt(String ext) {
        return ".jpg".equals(ext) || ".jpeg".equals(ext) || ".png".equals(ext) || ".webp".equals(ext);
    }
}