package com.oceanview.dao;

import com.oceanview.model.Bill;

public interface BillDAO {
    int save(Bill bill);

    Bill findByReservationId(int reservationId);
}