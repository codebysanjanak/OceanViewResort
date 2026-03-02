package com.oceanview.service;

import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.GuestDAO;
import com.oceanview.model.Guest;
import com.oceanview.security.PasswordHasher;

import java.util.LinkedHashMap;
import java.util.Map;

public class GuestAuthService {
    private final GuestDAO guestDAO = DAOFactory.guestDAO();
    private final PasswordHasher hasher = new PasswordHasher();

    public int register(String name, String email, String phone, String address, String password, String confirmPassword) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (name == null || name.isBlank()) errors.put("name", "Name is required");
        if (email == null || email.isBlank()) errors.put("email", "Email is required");
        if (phone == null || phone.isBlank()) errors.put("phone", "Phone is required");
        if (address == null || address.isBlank()) errors.put("address", "Address is required");
        if (password == null || password.isBlank()) errors.put("password", "Password is required");
        if (confirmPassword == null || confirmPassword.isBlank()) errors.put("confirmPassword", "Confirm password is required");

        if (email != null && !email.isBlank()) {
            String e = email.trim().toLowerCase();
            if (!e.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                errors.put("email", "Enter a valid email address");
            }
        }

        if (password != null && !password.isBlank() && password.length() < 6) {
            errors.put("password", "Password must be at least 6 characters");
        }

        if (password != null && confirmPassword != null
                && !password.isBlank() && !confirmPassword.isBlank()
                && !password.equals(confirmPassword)) {
            errors.put("confirmPassword", "Passwords do not match");
        }

        if (!errors.isEmpty()) throw new ValidationException(errors);

        String cleanName = name.trim();
        String cleanEmail = email.trim().toLowerCase();
        String cleanPhone = phone.trim();
        String cleanAddress = address.trim();

        if (guestDAO.findByEmail(cleanEmail) != null) {
            errors.put("email", "Email already registered");
            throw new ValidationException(errors);
        }

        Guest g = new Guest();
        g.setName(cleanName);
        g.setEmail(cleanEmail);
        g.setPhone(cleanPhone);
        g.setAddress(cleanAddress);
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