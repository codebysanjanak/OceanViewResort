package com.oceanview.service;

import com.oceanview.dao.AdminDAO;
import com.oceanview.dao.DAOFactory;
import com.oceanview.model.Admin;
import com.oceanview.security.PasswordHasher;

import java.util.List;

public class AdminService {

    private final AdminDAO adminDAO = DAOFactory.adminDAO();
    private final PasswordHasher hasher = new PasswordHasher();

    public List<Admin> getAllStaff() {
        return adminDAO.findAll();
    }

    public void createStaff(String username, String password, String role) {

        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username required");

        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password must be 6+ characters");

        if (role == null || role.isBlank())
            role = "MANAGER";

        Admin a = new Admin();
        a.setUsername(username.trim());
        a.setPasswordHash(hasher.hash(password));
        a.setRole(role.trim());

        adminDAO.create(a);
    }

    public Admin getAdmin(int id) {
        Admin a = adminDAO.findById(id);
        if (a == null) throw new IllegalArgumentException("Staff not found");
        return a;
    }

    // Update username/role + optionally password
    public void updateStaff(int id, String username, String role, String newPassword) {

        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username required");

        if (role == null || role.isBlank())
            role = "MANAGER";

        Admin a = getAdmin(id);

        a.setUsername(username.trim());
        a.setRole(role.trim());
        adminDAO.update(a);

        // update password only if provided
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 6)
                throw new IllegalArgumentException("Password must be 6+ characters");

            adminDAO.updatePassword(id, hasher.hash(newPassword));
        }
    }
}