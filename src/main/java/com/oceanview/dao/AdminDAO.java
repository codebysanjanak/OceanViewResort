package com.oceanview.dao;

import java.util.List;

import com.oceanview.model.Admin;

public interface AdminDAO {
	Admin findByUsername(String username);

    Admin findById(int adminId);

    List<Admin> findAll();

    int create(Admin admin);

    void update(Admin admin);

    void updatePassword(int adminId, String passwordHash);
}