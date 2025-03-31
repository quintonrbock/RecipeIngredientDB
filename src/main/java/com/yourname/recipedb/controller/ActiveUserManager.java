package com.yourname.recipedb.controller;

import com.yourname.recipedb.model.User;

public class ActiveUserManager {
    private static User activeUser;

    public static User getActiveUser() {
        return activeUser;
    }

    public static void setActiveUser(User user) {
        activeUser = user;
    }

    public static boolean isAdmin() {
        return activeUser == null; // Admin view when no user is selected
    }
}
