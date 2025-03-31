package com.yourname.recipedb;

import javax.swing.SwingUtilities;

import com.yourname.recipedb.view.MainFrame;

public class Main {
    public static void main(String[] args) {
        // Initialize the database at startup
        DatabaseManager.initializeDatabase(); 

        // Proceed with other application logic
        System.out.println("(Main) Database initialized successfully.");

        // Ensure GUI creation is done on the Event Dispatch Thread
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
