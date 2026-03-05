package com.oceanview.web.servlet;

import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomTypeDAO;
import com.oceanview.dao.GuestDAO;

import com.oceanview.model.Reservation;
import com.oceanview.model.ReservationView;
import com.oceanview.model.RoomType;
import com.oceanview.model.Guest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/reservations")
public class AdminReservationsServlet extends HttpServlet {

    private final ReservationDAO reservationDAO = DAOFactory.reservationDAO();
    private final GuestDAO guestDAO = DAOFactory.guestDAO();
    private final RoomTypeDAO roomTypeDAO = DAOFactory.roomTypeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Reservation> reservations = reservationDAO.findAll();

        List<ReservationView> viewList = new ArrayList<>();

        for (Reservation r : reservations) {

            Guest guest = guestDAO.findById(r.getGuestId());
            RoomType roomType = roomTypeDAO.findById(r.getRoomTypeId());

            ReservationView v = new ReservationView();

            v.setReservationId(r.getReservationId());
            v.setReservationNo(r.getReservationNo());
            v.setGuestName(guest != null ? guest.getName() : "Unknown");
            v.setRoomTypeName(roomType != null ? roomType.getTypeName() : "Unknown");
            v.setCheckIn(r.getCheckIn());
            v.setCheckOut(r.getCheckOut());
            v.setStatus(r.getStatus());

            viewList.add(v);
        }

        req.setAttribute("reservations", viewList);

        req.getRequestDispatcher("/reservations.jspx")
                .forward(req, resp);
    }
}