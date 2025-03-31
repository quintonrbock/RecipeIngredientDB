package com.yourname.recipedb.util;

import javax.swing.JOptionPane;

public class ErrorHandler {

    public static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void logError(String message, Exception e) {
        System.err.println("ERROR: " + message);
        java.util.logging.Logger.getLogger(ErrorHandler.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
    }

    public static void showDatabaseError(Exception e) {
        showErrorMessage("A database error occurred: " + e.getMessage());
        logError("Database error", e);
    }
}
