package com.oceanview.dao;

import com.oceanview.model.RoomType;
import java.util.List;

public interface RoomTypeDAO {
    List<RoomType> findAllActive();
    List<RoomType> findAll();
    RoomType findById(int id);

    int create(RoomType rt);
    void update(RoomType rt);
    void delete(int id);

    void setActive(int id, boolean active);
}