package com.oceanview.service;

import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.GuestDAO;
import com.oceanview.model.Guest;

import java.util.ArrayList;
import java.util.List;

public class GuestService {

    private final GuestDAO guestDAO = DAOFactory.guestDAO();

    public List<Guest> getAllGuests() {
        return guestDAO.findAll();
    }
}