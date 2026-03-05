package com.oceanview.dao;

import com.oceanview.model.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationDAO {
    int create(Reservation r);

    Reservation findByReservationNo(String reservationNo);

    List<Reservation> findByGuestId(int guestId);

    List<Reservation> findAll();

    void cancelByGuest(int guestId, String reservationNo);

    int countOverlaps(int roomTypeId, LocalDate checkIn, LocalDate checkOut);
}