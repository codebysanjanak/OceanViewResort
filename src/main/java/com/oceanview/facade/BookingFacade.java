package com.oceanview.facade;

import com.oceanview.service.BookingService;

import java.time.LocalDate;

public class BookingFacade {
    private final BookingService bookingService = new BookingService();

    public String createBooking(int guestId, int roomTypeId, LocalDate in, LocalDate out) {
        return bookingService.createBooking(guestId, roomTypeId, in, out);
    }

    public Object getGuestBookings(int guestId) {
        return bookingService.getGuestBookings(guestId);
    }

    public void cancelBooking(int guestId, String reservationNo) {
        bookingService.cancelBooking(guestId, reservationNo);
    }
}