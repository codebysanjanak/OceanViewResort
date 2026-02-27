package com.oceanview.dao;

import com.oceanview.model.Admin;

public interface AdminDAO {
    Admin findByUsername(String username);
    Admin findById(int adminId);
    void update(Admin admin);
    void updatePassword(int adminId, String passwordHash);
}