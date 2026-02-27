package com.oceanview.service;

import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.BillDAO;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomTypeDAO;
import com.oceanview.model.Bill;
import com.oceanview.model.Reservation;
import com.oceanview.model.RoomType;
import com.oceanview.pricing.PricingStrategy;
import com.oceanview.pricing.StandardPricingStrategy;

import java.time.temporal.ChronoUnit;

public class BillingService {
    private final ReservationDAO reservationDAO = DAOFactory.reservationDAO();
    private final RoomTypeDAO roomTypeDAO = DAOFactory.roomTypeDAO();
    private final BillDAO billDAO = DAOFactory.billDAO();

    private PricingStrategy strategy = new StandardPricingStrategy();

    public Bill generateBill(String reservationNo) {
        if (reservationNo == null || reservationNo.isBlank()) throw new IllegalArgumentException("Reservation number required");
        Reservation r = reservationDAO.findByReservationNo(reservationNo.trim());
        if (r == null) throw new IllegalArgumentException("Reservation not found");

        Bill existing = billDAO.findByReservationId(r.getReservationId());
        if (existing != null) return existing;

        RoomType rt = roomTypeDAO.findById(r.getRoomTypeId());
        if (rt == null) throw new IllegalArgumentException("Room type not found");

        int nights = (int) ChronoUnit.DAYS.between(r.getCheckIn(), r.getCheckOut());
        if (nights <= 0) throw new IllegalArgumentException("Invalid nights calculation");

        Bill bill = new Bill();
        bill.setReservationId(r.getReservationId());
        bill.setNights(nights);
        bill.setRate(rt.getNightlyRate());
        bill.setTotal(strategy.calculateTotal(nights, rt.getNightlyRate()));

        billDAO.save(bill);
        return bill;
    }
}