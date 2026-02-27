package com.oceanview.service;

import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomTypeDAO;
import com.oceanview.model.AvailabilityResult;
import com.oceanview.model.RoomType;

import java.time.LocalDate;
import java.util.List;

public class RoomService {
    private final RoomTypeDAO roomTypeDAO = DAOFactory.roomTypeDAO();
    private final ReservationDAO reservationDAO = DAOFactory.reservationDAO();

    public List<RoomType> listActiveRoomTypes() {
        return roomTypeDAO.findAllActive();
    }

    public void validateDates(LocalDate in, LocalDate out) {
        if (in == null || out == null) throw new IllegalArgumentException("Dates are required");
        if (!out.isAfter(in)) throw new IllegalArgumentException("Check-out must be after check-in");
    }

    public AvailabilityResult filterAvailability(int roomTypeId, LocalDate in, LocalDate out) {
        validateDates(in, out);

        RoomType rt = roomTypeDAO.findById(roomTypeId);
        if (rt == null || !rt.isActive()) throw new IllegalArgumentException("Invalid room type");

        int overlaps = reservationDAO.countOverlaps(roomTypeId, in, out);
        boolean available = overlaps < rt.getCapacity();
        return new AvailabilityResult(rt, available, overlaps);
    }
}