package com.oceanview.facade;

import com.oceanview.model.Reservation;
import com.oceanview.service.BookingService;

import java.time.LocalDate;
import java.util.List;

public class BookingFacade {
    private final BookingService bookingService = new BookingService();

    public String createBooking(int guestId, int roomTypeId, LocalDate in, LocalDate out) {
        return bookingService.createBooking(guestId, roomTypeId, in, out);
    }

    public List<Reservation> getGuestBookings(int guestId) {
        return bookingService.getGuestBookings(guestId);
    }

    public void cancelBooking(int guestId, String reservationNo) {
        bookingService.cancelBooking(guestId, reservationNo);
    }
}