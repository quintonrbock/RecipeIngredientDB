package com.yourname.recipedb.old;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class RecipeDatabaseGUICopy {
    private static final String URL = "jdbc:oracle:thin:@//10.0.0.7:1521/XE"; // Updated JDBC URL
    private static final String USER = "system";
    private static final String PASSWORD = "your_new_password";

    private final JFrame frame;
    private final JTable recipeTable;
    private final DefaultTableModel recipeTableModel;
    private final JTable ingredientTable;
    private final DefaultTableModel ingredientTableModel;

    public RecipeDatabaseGUICopy() {
        frame = new JFrame("Recipe Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600); // Larger size
        frame.setLayout(new BorderLayout());

        // Center the window on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width / 2 - frame.getSize().width / 2, screenSize.height / 2 - frame.getSize().height / 2);

        // Recipe Table setup
        String[] recipeColumnNames = {"recipe_id", "name", "instructions"};
        recipeTableModel = new DefaultTableModel(recipeColumnNames, 0);
        recipeTable = new JTable(recipeTableModel);
        JScrollPane recipeScrollPane = new JScrollPane(recipeTable);

        // Ingredient Table setup
        String[] ingredientColumnNames = {"ingredient_id", "name"};
        ingredientTableModel = new DefaultTableModel(ingredientColumnNames, 0);
        ingredientTable = new JTable(ingredientTableModel);
        JScrollPane ingredientScrollPane = new JScrollPane(ingredientTable);

        // Font for buttons
        Font buttonFont = new Font("Arial", Font.BOLD, 12);

        // Buttons for Recipes
        JButton refreshRecipesButton = new JButton("Refresh Recipes");
        refreshRecipesButton.setPreferredSize(new Dimension(100, 50));
        refreshRecipesButton.setFont(buttonFont);
        refreshRecipesButton.addActionListener(e -> loadRecipes());

        JButton addRecipeButton = new JButton("Add Recipe");
        addRecipeButton.setPreferredSize(new Dimension(100, 50));
        addRecipeButton.setFont(buttonFont);
        addRecipeButton.addActionListener(e -> addRecipe());

        JButton deleteRecipeButton = new JButton("Delete Recipe");
        deleteRecipeButton.setPreferredSize(new Dimension(100, 50));
        deleteRecipeButton.setFont(buttonFont);
        deleteRecipeButton.addActionListener(e -> deleteRecipe());

        // Buttons for Ingredients
        JButton refreshIngredientsButton = new JButton("Refresh Ingredients");
        refreshIngredientsButton.setPreferredSize(new Dimension(100, 50));
        refreshIngredientsButton.setFont(buttonFont);
        refreshIngredientsButton.addActionListener(e -> loadIngredients());

        JButton addIngredientButton = new JButton("Add Ingredient");
        addIngredientButton.setPreferredSize(new Dimension(100, 50));
        addIngredientButton.setFont(buttonFont);
        addIngredientButton.addActionListener(e -> addIngredient());

        JButton deleteIngredientButton = new JButton("Delete Ingredient");
        deleteIngredientButton.setPreferredSize(new Dimension(100, 50));
        deleteIngredientButton.setFont(buttonFont);
        deleteIngredientButton.addActionListener(e -> deleteIngredient());

        // Button Panels
        JPanel recipeButtonPanel = new JPanel(new GridLayout(1, 3, 20, 0)); // Increased horizontal gap
        recipeButtonPanel.add(refreshRecipesButton);
        recipeButtonPanel.add(addRecipeButton);
        recipeButtonPanel.add(deleteRecipeButton);

        JPanel ingredientButtonPanel = new JPanel(new GridLayout(1, 3, 20, 0)); // Increased horizontal gap
        ingredientButtonPanel.add(refreshIngredientsButton);
        ingredientButtonPanel.add(addIngredientButton);
        ingredientButtonPanel.add(deleteIngredientButton);

        // Main Button Panel
        JPanel mainButtonPanel = new JPanel(new GridLayout(1, 2, 40, 0)); // Increased horizontal gap
        mainButtonPanel.setBorder(new EmptyBorder(new Insets(10, 40, 10, 40))); // Add padding
        mainButtonPanel.add(recipeButtonPanel);
        mainButtonPanel.add(ingredientButtonPanel);

        // Recipe Table
        JPanel recipeTablePanel = new JPanel(new GridLayout(1, 1));
        recipeTablePanel.setBorder(new EmptyBorder(new Insets(10, 40, 10, 40))); // Add padding
        recipeTablePanel.add(recipeScrollPane);

        // Ingredient Table
        JPanel ingredientTablePanel = new JPanel(new GridLayout(1, 1));
        ingredientTablePanel.setBorder(new EmptyBorder(new Insets(10, 40, 10, 40))); // Add padding
        ingredientTablePanel.add(ingredientScrollPane);

        frame.add(recipeTablePanel, BorderLayout.CENTER);
        frame.add(ingredientTablePanel, BorderLayout.CENTER);
        frame.add(mainButtonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        loadRecipes();  // Load recipes initially
        loadIngredients();  // Load ingredients initially
    }

    private void loadRecipes() {
        recipeTableModel.setRowCount(0); // Clear the table

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
                    recipeTableModel.addRow(new Object[]{recipe_id, name, instructions});
                }

            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Oracle JDBC Driver not found!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadIngredients() {
        ingredientTableModel.setRowCount(0); // Clear the table

        try {
            // Force JDBC driver loading
            Class.forName("oracle.jdbc.OracleDriver");

            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT ingredient_id, name FROM ingredients")) {

                while (resultSet.next()) {
                    int ingredient_id = resultSet.getInt("ingredient_id");
                    String name = resultSet.getString("name");
                    ingredientTableModel.addRow(new Object[]{ingredient_id, name});
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

    private void addIngredient() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            int newIngredientId = getNextIngredientId(statement);
            String insertIngredientSQL = "INSERT INTO ingredients (ingredient_id, name) VALUES (" + newIngredientId + ", '')";
            statement.executeUpdate(insertIngredientSQL);
            System.out.println("Blank ingredient added with ingredient_id: " + newIngredientId);
            loadIngredients(); // Refresh the table

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getNextIngredientId(Statement statement) throws SQLException {
        String getMaxIdSQL = "SELECT NVL(MAX(ingredient_id), 0) AS max_id FROM ingredients";
        try (ResultSet resultSet = statement.executeQuery(getMaxIdSQL)) {
            resultSet.next();
            return resultSet.getInt("max_id") + 1;
        }
    }

    private void deleteIngredient() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String deleteIngredientSQL = "DELETE FROM ingredients WHERE ingredient_id = (SELECT MAX(ingredient_id) FROM ingredients)";
            statement.executeUpdate(deleteIngredientSQL);
            System.out.println("Last ingredient deleted.");
            loadIngredients(); // Refresh the table

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        System.out.println("Launching GUI...");
        SwingUtilities.invokeLater(RecipeDatabaseGUICopy::new);
    }
}
