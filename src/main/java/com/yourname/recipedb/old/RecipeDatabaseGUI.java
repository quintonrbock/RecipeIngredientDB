package com.yourname.recipedb.old;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class RecipeDatabaseGUI {
    private static final String URL = "jdbc:oracle:thin:@//10.0.0.7:1521/XE"; // Updated JDBC URL
    private static final String USER = "system";
    private static final String PASSWORD = "your_new_password";

    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public RecipeDatabaseGUI() {
        frame = new JFrame("Recipe Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"recipe_id", "name", "instructions"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Buttons
        JButton refreshButton = new JButton("Refresh Recipes");
        refreshButton.addActionListener(e -> loadRecipes());

        JButton addButton = new JButton("Add Recipe");
        addButton.addActionListener(e -> addRecipe());

        JButton deleteButton = new JButton("Delete Recipe");
        deleteButton.addActionListener(e -> deleteRecipe());

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        // Layout
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        loadRecipes();  // Load recipes initially
    }

    private void loadRecipes() {
        tableModel.setRowCount(0); // Clear the table

        try {
            // Force JDBC driver loading
            Class.forName("oracle.jdbc.OracleDriver");

            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT recipe_id, name, instructions FROM recipes")) {

                while (resultSet.next()) {
                    int recipe_id = resultSet.getInt("recipe_id");
                    String name = resultSet.getString("name");
                    String instructions = resultSet.getString("instructions");
                    tableModel.addRow(new Object[]{recipe_id, name, instructions});
                }

            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Oracle JDBC Driver not found!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRecipe() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            int newRecipeId = getNextRecipeId(statement);
            String insertRecipeSQL = "INSERT INTO recipes (recipe_id, name, instructions) VALUES (" + newRecipeId + ", '', '')";
            statement.executeUpdate(insertRecipeSQL);
            System.out.println("Blank recipe added with recipe_id: " + newRecipeId);
            loadRecipes(); // Refresh the table

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getNextRecipeId(Statement statement) throws SQLException {
        String getMaxIdSQL = "SELECT NVL(MAX(recipe_id), 0) AS max_id FROM recipes";
        try (ResultSet resultSet = statement.executeQuery(getMaxIdSQL)) {
            resultSet.next();
            return resultSet.getInt("max_id") + 1;
        }
    }

    private void deleteRecipe() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String deleteRecipeSQL = "DELETE FROM recipes WHERE recipe_id = (SELECT MAX(recipe_id) FROM recipes)";
            statement.executeUpdate(deleteRecipeSQL);
            System.out.println("Last recipe deleted.");
            loadRecipes(); // Refresh the table

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        System.out.println("Launching GUI...");
        SwingUtilities.invokeLater(RecipeDatabaseGUICopy::new);
    }
}
