package com.oceanview.service;

import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.AdminDAO;
import com.oceanview.dao.GuestDAO;
import com.oceanview.model.Admin;
import com.oceanview.model.Guest;
import com.oceanview.security.PasswordHasher;

public class ProfileService {
    private final GuestDAO guestDAO = DAOFactory.guestDAO();
    private final AdminDAO adminDAO = DAOFactory.adminDAO();
    private final PasswordHasher hasher = new PasswordHasher();

    public Guest getGuest(int guestId) {
        Guest g = guestDAO.findById(guestId);
        if (g == null) throw new IllegalArgumentException("Guest not found");
        return g;
    }

    public void updateGuestProfile(int guestId, String name, String phone, String address) {
        Guest g = getGuest(guestId);
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        g.setName(name.trim());
        g.setPhone(phone == null ? null : phone.trim());
        g.setAddress(address == null ? null : address.trim());
        guestDAO.update(g);
    }

    public void changeGuestPassword(int guestId, String oldPw, String newPw) {
        if (newPw == null || newPw.length() < 6) throw new IllegalArgumentException("New password must be at least 6 characters");
        Guest g = getGuest(guestId);
        if (!hasher.verify(oldPw, g.getPasswordHash())) throw new IllegalArgumentException("Old password is incorrect");
        guestDAO.updatePassword(guestId, hasher.hash(newPw));
    }

    // Optional admin profile usage
    public Admin getAdmin(int adminId) {
        Admin a = adminDAO.findById(adminId);
        if (a == null) throw new IllegalArgumentException("Admin not found");
        return a;
    }
}