package com.oceanview.model;

public class AvailabilityResult {
    private RoomType roomType;
    private boolean available;
    private int overlaps;

    public AvailabilityResult(RoomType roomType, boolean available, int overlaps) {
        this.roomType = roomType;
        this.available = available;
        this.overlaps = overlaps;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getOverlaps() {
        return overlaps;
    }
}