package com.oceanview.dao;

import com.oceanview.dao.jdbc.*;

public final class DAOFactory {
    private DAOFactory() {
    }

    public static GuestDAO guestDAO() {
        return new JDBCGuestDAO();
    }

    public static AdminDAO adminDAO() {
        return new JDBCAdminDAO();
    }

    public static RoomTypeDAO roomTypeDAO() {
        return new JDBCRoomTypeDAO();
    }

    public static ReservationDAO reservationDAO() {
        return new JDBCReservationDAO();
    }

    public static BillDAO billDAO() {
        return new JDBCBillDAO();
    }
}