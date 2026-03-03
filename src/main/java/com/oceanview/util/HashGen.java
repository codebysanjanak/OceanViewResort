package com.oceanview.util;

import com.oceanview.security.PasswordHasher;

public class HashGen {
    public static void main(String[] args) {
        PasswordHasher hasher = new PasswordHasher();
        String hash = hasher.hash("admin");
        System.out.println("Generated hash:");
        System.out.println(hash);
    }
}