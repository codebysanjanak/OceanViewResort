package com.oceanview.facade;

import com.oceanview.service.BookingService;
import com.oceanview.model.Reservation;

import java.time.LocalDate;
import java.util.List;

public class BookingFacade {

    private final BookingService bookingService = new BookingService();

    public String createBooking(int guestId, int roomTypeId,
                                LocalDate checkIn, LocalDate checkOut) {

        return bookingService.createBooking(guestId, roomTypeId, checkIn, checkOut);
    }

    public List<Reservation> getGuestBookings(int guestId) {
        return bookingService.getGuestBookings(guestId);
    }

    public void cancelBooking(int guestId, String reservationNo) {
        bookingService.cancelBooking(guestId, reservationNo);
    }
}