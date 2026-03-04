package com.oceanview.service;

import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomTypeDAO;
import com.oceanview.model.Reservation;
import com.oceanview.model.RoomType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BookingService {

    private final ReservationDAO reservationDAO = DAOFactory.reservationDAO();
    private final RoomTypeDAO roomTypeDAO = DAOFactory.roomTypeDAO();

    public void validateDates(LocalDate in, LocalDate out) {
        if (in == null || out == null)
            throw new IllegalArgumentException("Dates are required");

        if (!out.isAfter(in))
            throw new IllegalArgumentException("Check-out must be after check-in");
    }

    public void checkCapacity(int roomTypeId, LocalDate in, LocalDate out) {
        RoomType rt = roomTypeDAO.findById(roomTypeId);

        if (rt == null || !rt.isActive())
            throw new IllegalArgumentException("Invalid room type");

        int overlaps = reservationDAO.countOverlaps(roomTypeId, in, out);

        // 🔥 use roomsCount instead of capacity
        if (overlaps >= rt.getRoomsCount())
            throw new IllegalArgumentException("Selected room type is not available for these dates");
    }

    public String createBooking(int guestId, int roomTypeId, LocalDate in, LocalDate out) {

        validateDates(in, out);
        checkCapacity(roomTypeId, in, out);

        Reservation r = new Reservation();
        r.setReservationNo("RSV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        r.setGuestId(guestId);
        r.setRoomTypeId(roomTypeId);
        r.setCheckIn(in);
        r.setCheckOut(out);
        r.setStatus("BOOKED");

        reservationDAO.create(r);

        return r.getReservationNo();
    }

    public List<Reservation> getGuestBookings(int guestId) {
        return reservationDAO.findByGuestId(guestId);
    }

    public void cancelBooking(int guestId, String reservationNo) {
        reservationDAO.cancelByGuest(guestId, reservationNo);
    }
}