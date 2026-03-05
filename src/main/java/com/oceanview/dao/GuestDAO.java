package com.oceanview.dao;

import java.util.List;

import com.oceanview.model.Guest;

public interface GuestDAO {
    int create(Guest guest);
    Guest findByEmail(String email);
    Guest findById(int guestId);
    void update(Guest guest);
    void updatePassword(int guestId, String passwordHash);
    List<Guest> findAll();
}