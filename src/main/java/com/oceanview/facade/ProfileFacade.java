package com.oceanview.facade;

import com.oceanview.model.Guest;
import com.oceanview.service.ProfileService;

public class ProfileFacade {
    private final ProfileService profileService = new ProfileService();

    public Guest getGuestProfile(int guestId) {
        return profileService.getGuest(guestId);
    }

    public void updateGuestProfile(int guestId, String name, String phone, String address) {
        profileService.updateGuestProfile(guestId, name, phone, address);
    }

    public void changeGuestPassword(int guestId, String oldPw, String newPw) {
        profileService.changeGuestPassword(guestId, oldPw, newPw);
    }
}