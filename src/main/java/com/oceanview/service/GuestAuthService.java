package com.oceanview.service;

import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.GuestDAO;
import com.oceanview.model.Guest;
import com.oceanview.security.PasswordHasher;

public class GuestAuthService {
    private final GuestDAO guestDAO = DAOFactory.guestDAO();
    private final PasswordHasher hasher = new PasswordHasher();

    public int register(String name, String email, String phone, String address, String password) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email required");
        if (password == null || password.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters");

        if (guestDAO.findByEmail(email) != null) throw new IllegalArgumentException("Email already registered");

        Guest g = new Guest();
        g.setName(name.trim());
        g.setEmail(email.trim().toLowerCase());
        g.setPhone(phone == null ? null : phone.trim());
        g.setAddress(address == null ? null : address.trim());
        g.setPasswordHash(hasher.hash(password));

        return guestDAO.create(g);
    }

    public Guest login(String email, String password) {
        if (email == null || password == null) throw new IllegalArgumentException("Email and password required");
        Guest g = guestDAO.findByEmail(email.trim().toLowerCase());
        if (g == null) return null;
        return hasher.verify(password, g.getPasswordHash()) ? g : null;
    }
}