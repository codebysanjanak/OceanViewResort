package com.oceanview.model;

import java.math.BigDecimal;

public class RoomType {
    private int roomTypeId;
    private String typeName;
    private BigDecimal nightlyRate;
    private boolean active;

    private String description;
    private int adultsCount;
    private int childrenCount;
    private int roomsCount;
    private String photoPath;

    public int getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(int roomTypeId) { this.roomTypeId = roomTypeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public BigDecimal getNightlyRate() { return nightlyRate; }
    public void setNightlyRate(BigDecimal nightlyRate) { this.nightlyRate = nightlyRate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAdultsCount() { return adultsCount; }
    public void setAdultsCount(int adultsCount) { this.adultsCount = adultsCount; }

    public int getChildrenCount() { return childrenCount; }
    public void setChildrenCount(int childrenCount) { this.childrenCount = childrenCount; }

    public int getRoomsCount() { return roomsCount; }
    public void setRoomsCount(int roomsCount) { this.roomsCount = roomsCount; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}