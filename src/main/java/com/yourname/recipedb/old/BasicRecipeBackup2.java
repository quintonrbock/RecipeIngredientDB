package com.yourname.recipedb.old;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class BasicRecipeBackup2 {
    private static final String URL = "jdbc:oracle:thin:@//127.0.0.1:1521/XE";
    private static final String USER = "system";
    private static final String PASSWORD = "your_new_password";

    private final JFrame frame;
    private final JTable recipeTable;
    private final DefaultTableModel recipeTableModel;
    private final JTable ingredientTable;
    private final DefaultTableModel ingredientTableModel;
    private final JTable userTable;
    private final DefaultTableModel userTableModel;
    private final List<JTable> userIngredientsTables = new ArrayList<>();
    private final List<DefaultTableModel> userIngredientsTableModels = new ArrayList<>();
    private final List<JLabel> userIngredientsLabels = new ArrayList<>();
    private final List<JPanel> otherTables = new ArrayList<>();
    private final List<DefaultTableModel> otherTablesModels = new ArrayList<>();
    private final List<JLabel> otherTablesLabels = new ArrayList<>();

    private int currentUserId = -1; // -1 indicates admin view

    private final JPanel topPanel;
    private final JPanel bottomPanel;
    private final JPanel mainPanel;
    private final JButton addIngredientButton;
    private final JButton editIngredientButton;
    private final JButton addRecipeButton;
    private final JButton editRecipeButton;
    private final JButton addUserButton;
    private final JButton editUserButton;
    private final JButton editUserIngredientsButton;
    private final JButton changeUserButton;
    private final JPanel userTablePanel;
    private final JPanel ingredientTablePanel;
    private final JPanel recipeTablePanel;

    public BasicRecipeBackup2() {
        frame = new JFrame("Recipe Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 800);
        frame.setLayout(new BorderLayout());
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width / 2 - frame.getSize().width / 2, screenSize.height / 2 - frame.getSize().height / 2);

        // Initialize buttons
        addIngredientButton = new JButton("Add Ingredient");
        addIngredientButton.addActionListener(e -> showAddIngredientPopup());

        editIngredientButton = new JButton("Edit Ingredient");
        editIngredientButton.addActionListener(e -> handleEditIngredient());

        addRecipeButton = new JButton("Add Recipe");
        addRecipeButton.addActionListener(e -> showAddRecipePopup());

        editRecipeButton = new JButton("Edit Recipe");
        editRecipeButton.addActionListener(e -> handleEditRecipe());

        addUserButton = new JButton("Add User");
        addUserButton.addActionListener(e -> showAddUserPopup());

        editUserButton = new JButton("Edit User");
        editUserButton.addActionListener(e -> handleEditUser());

        editUserIngredientsButton = new JButton("Edit User Ingredients");
        editUserIngredientsButton.addActionListener(e -> handleEditUserIngredients());

        changeUserButton = new JButton("Change User");
        changeUserButton.addActionListener(e -> handleChangeUser());

        // Add buttons to button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 10, 20, 0));
        buttonPanel.add(addIngredientButton);
        buttonPanel.add(editIngredientButton);
        buttonPanel.add(addRecipeButton);
        buttonPanel.add(editRecipeButton);
        buttonPanel.add(addUserButton);
        buttonPanel.add(editUserButton);
        buttonPanel.add(editUserIngredientsButton);
        buttonPanel.add(changeUserButton);

        // Initialize top panel with original tables
        topPanel = new JPanel(new GridLayout(1, 3));
        
        // Recipe Table
        String[] recipeColumnNames = {"Recipe ID", "Name", "Instructions"};
        recipeTableModel = new DefaultTableModel(recipeColumnNames, 0);
        recipeTable = new JTable(recipeTableModel);
        JScrollPane recipeScrollPane = new JScrollPane(recipeTable);
        recipeTablePanel = new JPanel(new BorderLayout());
        recipeTablePanel.add(new JLabel("Recipes"), BorderLayout.NORTH);
        recipeTablePanel.add(recipeScrollPane, BorderLayout.CENTER);
        topPanel.add(recipeTablePanel);
        otherTables.add(recipeTablePanel);
        otherTablesModels.add(recipeTableModel);
        otherTablesLabels.add(new JLabel("Recipes"));

        // Ingredient Table
        String[] ingredientColumnNames = {"Ingredient ID", "Name"};
        ingredientTableModel = new DefaultTableModel(ingredientColumnNames, 0);
        ingredientTable = new JTable(ingredientTableModel);
        JScrollPane ingredientScrollPane = new JScrollPane(ingredientTable);
        ingredientTablePanel = new JPanel(new BorderLayout());
        ingredientTablePanel.add(new JLabel("Ingredients"), BorderLayout.NORTH);
        ingredientTablePanel.add(ingredientScrollPane, BorderLayout.CENTER);
        topPanel.add(ingredientTablePanel);
        otherTables.add(ingredientTablePanel);
        otherTablesModels.add(ingredientTableModel);
        otherTablesLabels.add(new JLabel("Ingredients"));

        // User Table
        String[] userColumnNames = {"User ID", "Username"};
        userTableModel = new DefaultTableModel(userColumnNames, 0);
        userTable = new JTable(userTableModel);
        JScrollPane userScrollPane = new JScrollPane(userTable);
        userTablePanel = new JPanel(new BorderLayout());
        userTablePanel.add(new JLabel("Users"), BorderLayout.NORTH);
        userTablePanel.add(userScrollPane, BorderLayout.CENTER);
        topPanel.add(userTablePanel);
        otherTables.add(userTablePanel);
        otherTablesModels.add(userTableModel);
        otherTablesLabels.add(new JLabel("Users"));

        // Initialize bottom panel with user ingredient tables
        bottomPanel = new JPanel(new GridLayout(1, 1));
        refreshUserTables(bottomPanel);

        // Main panel to hold top and bottom panels
        mainPanel = new JPanel(new GridLayout(2, 1));
        mainPanel.add(topPanel);
        mainPanel.add(bottomPanel);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
        loadRecipes();
        loadIngredients();
        loadUsers();
        loadUserTables(); // Load user tables after initializing the frame
    }

    private void loadUserTables() {
        userIngredientsTables.clear();
        userIngredientsTableModels.clear();
        userIngredientsLabels.clear();

        List<String> userNames = getUserNames();
        for (String userName : userNames) {
            JLabel userIngredientsLabel = new JLabel(userName + "'s Ingredients");
            userIngredientsLabels.add(userIngredientsLabel);

            DefaultTableModel userIngredientsTableModel = new DefaultTableModel(new String[]{"Ingredient ID", "Ingredient Name", "Quantity"}, 0);
            userIngredientsTableModels.add(userIngredientsTableModel);

            JTable userIngredientsTable = new JTable(userIngredientsTableModel);
            userIngredientsTables.add(userIngredientsTable);
        }

        refreshUserTables(bottomPanel);
    }

    private void refreshUserTables(JPanel bottomPanel) {
        SwingUtilities.invokeLater(() -> {
            bottomPanel.removeAll();
            bottomPanel.setLayout(new GridLayout(1, userIngredientsTables.size()));

            if (currentUserId == -1) {
                // Admin view: show all user ingredient tables
                for (int i = 0; i < userIngredientsTables.size(); i++) {
                    JPanel userIngredientsTablePanel = new JPanel(new BorderLayout());
                    userIngredientsTablePanel.add(userIngredientsLabels.get(i), BorderLayout.NORTH);
                    userIngredientsTablePanel.add(new JScrollPane(userIngredientsTables.get(i)), BorderLayout.CENTER);
                    bottomPanel.add(userIngredientsTablePanel);
                }
            } else {
                // User view: show only the current user's ingredient table
                int userIndex = currentUserId - 1; // Assuming user IDs start from 1 and are sequential
                if (userIndex >= 0 && userIndex < userIngredientsTables.size()) {
                    JPanel userIngredientsTablePanel = new JPanel(new BorderLayout());
                    userIngredientsTablePanel.add(userIngredientsLabels.get(userIndex), BorderLayout.NORTH);
                    userIngredientsTablePanel.add(new JScrollPane(userIngredientsTables.get(userIndex)), BorderLayout.CENTER);
                    bottomPanel.add(userIngredientsTablePanel);
                }
            }

            if (userIngredientsTables.isEmpty()) {
                bottomPanel.add(new JLabel("No users found"));
            }

            frame.revalidate();
            frame.repaint();

            for (int i = 0; i < userIngredientsTables.size(); i++) {
                loadUserIngredients(i + 1, userIngredientsTableModels.get(i));
            }
        });
    }

    private void refreshOtherTables(JPanel topPanel) {
        SwingUtilities.invokeLater(() -> {
            topPanel.removeAll();
            topPanel.setLayout(new GridLayout(1, otherTablesModels.size()));

            if (currentUserId == -1) {
                // Admin view: show all user ingredient tables
                for (int i = 0; i < otherTables.size(); i++) {
                    JPanel otherTablePanel = new JPanel(new BorderLayout());
                    otherTablePanel.add(otherTablesLabels.get(i), BorderLayout.NORTH);
                    otherTablePanel.add(new JScrollPane(otherTables.get(i)), BorderLayout.CENTER);
                    topPanel.add(otherTablePanel);
                }
            } else {
                // User view: show only the current user's ingredient table
                int userIndex = currentUserId - 1; // Assuming user IDs start from 1 and are sequential
                if (userIndex >= 0 && userIndex < userIngredientsTables.size()) {
                    JPanel otherTablePanel = new JPanel(new BorderLayout());
                    otherTablePanel.add(otherTablesLabels.get(userIndex), BorderLayout.NORTH);
                    otherTablePanel.add(new JScrollPane(otherTables.get(userIndex)), BorderLayout.CENTER);
                    topPanel.add(otherTablePanel);
                }
            }

            if (otherTables.isEmpty()) {
                topPanel.add(new JLabel("No users found"));
            }
            

            frame.revalidate();
            frame.repaint();
        });
    }

    private List<String> getUserNames() {
        List<String> userNames = new ArrayList<>();
        String query = "SELECT username FROM users";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                userNames.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            showError(e);
        }

        return userNames;
    }

    private void loadRecipes() {
        recipeTableModel.setRowCount(0);
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT recipe_id, name, instructions FROM recipes")) {
            while (resultSet.next()) {
                recipeTableModel.addRow(new Object[]{
                    resultSet.getInt("recipe_id"),
                    resultSet.getString("name"),
                    resultSet.getString("instructions")
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void loadIngredients() {
        ingredientTableModel.setRowCount(0);
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT ingredient_id, name FROM ingredients")) {
            while (resultSet.next()) {
                ingredientTableModel.addRow(new Object[]{
                    resultSet.getInt("ingredient_id"),
                    resultSet.getString("name")
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void loadUsers() {
        userTableModel.setRowCount(0);
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT user_id, username FROM users")) {
            while (resultSet.next()) {
                userTableModel.addRow(new Object[]{
                    resultSet.getInt("user_id"),
                    resultSet.getString("username")
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void loadUserIngredients(int userId, DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        String query = "SELECT ui.ingredient_id, i.name AS ingredient_name, ui.quantity " +
                       "FROM user_ingredients ui " +
                       "JOIN ingredients i ON ui.ingredient_id = i.ingredient_id " +
                       "WHERE ui.user_id = " + userId;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                    resultSet.getInt("ingredient_id"),
                    resultSet.getString("ingredient_name"),
                    resultSet.getInt("quantity")
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void showAddIngredientPopup() {
        JTextField ingredientNameField = new JTextField();
        Object[] message = {
            "Ingredient Name:", ingredientNameField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Add Ingredient", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String ingredientName = ingredientNameField.getText();
            if (!ingredientName.trim().isEmpty()) {
                addIngredient(ingredientName);
            } else {
                JOptionPane.showMessageDialog(frame, "Ingredient name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addIngredient(String name) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            int newIngredientId = getNextIngredientId(statement);
            String insertIngredientSQL = "INSERT INTO ingredients (ingredient_id, name) VALUES (" + newIngredientId + ", '" + name + "')";
            statement.executeUpdate(insertIngredientSQL);
            loadIngredients(); // Refresh the table

        } catch (SQLException e) {
            showError(e);
        }
    }

    private int getNextRecipeId(Statement statement) throws SQLException {
        String getMaxIdSQL = "SELECT NVL(MAX(recipe_id), 0) AS max_id FROM recipes";
        try (ResultSet resultSet = statement.executeQuery(getMaxIdSQL)) {
            resultSet.next();
            return resultSet.getInt("max_id") + 1;
        }
    }

    private int getNextIngredientId(Statement statement) throws SQLException {
        String getMaxIdSQL = "SELECT NVL(MAX(ingredient_id), 0) AS max_id FROM ingredients";
        try (ResultSet resultSet = statement.executeQuery(getMaxIdSQL)) {
            resultSet.next();
            return resultSet.getInt("max_id") + 1;
        }
    }

    private void handleEditIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow == -1) {
            showSelectIngredientPopup();
        } else {
            int ingredientId = (int) ingredientTableModel.getValueAt(selectedRow, 0);
            showEditIngredientPopup(ingredientId);
        }
    }

    private void showSelectIngredientPopup() {
        JComboBox<String> ingredientComboBox = new JComboBox<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT name FROM ingredients")) {

            while (resultSet.next()) {
                ingredientComboBox.addItem(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            showError(e);
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select Ingredient:"));
        panel.add(ingredientComboBox);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Select Ingredient", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String selectedIngredient = (String) ingredientComboBox.getSelectedItem();
            if (selectedIngredient != null) {
                int ingredientId = getIngredientIdByName(selectedIngredient);
                showEditIngredientPopup(ingredientId);
            }
        }
    }

    private int getIngredientIdByName(String name) {
        int ingredientId = -1;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT ingredient_id FROM ingredients WHERE name = '" + name + "'")) {

            if (resultSet.next()) {
                ingredientId = resultSet.getInt("ingredient_id");
            }
        } catch (SQLException e) {
            showError(e);
        }
        return ingredientId;
    }

    private void showEditIngredientPopup(int ingredientId) {
        String currentName = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT name FROM ingredients WHERE ingredient_id = " + ingredientId)) {

            if (resultSet.next()) {
                currentName = resultSet.getString("name");
            }
        } catch (SQLException e) {
            showError(e);
        }

        if (currentName != null) {
            JTextField ingredientNameField = new JTextField(currentName);
            
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Ingredient Name:"));
            panel.add(ingredientNameField);

            int option = JOptionPane.showOptionDialog(frame, panel, "Edit Ingredient", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Okay", "Delete", "Cancel"}, "Cancel");

            if (option == 0) { // Okay
                String newName = ingredientNameField.getText();
                if (!newName.trim().isEmpty()) {
                    editIngredient(ingredientId, newName);
                } else {
                    JOptionPane.showMessageDialog(frame, "Ingredient name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (option == 1) { // Delete
                int deleteOption = JOptionPane.showConfirmDialog(frame, "Do you want to delete this ingredient?", "Delete Ingredient", JOptionPane.OK_CANCEL_OPTION);
                if (deleteOption == JOptionPane.OK_OPTION) {
                    deleteIngredient(ingredientId);
                } else {
                    showEditIngredientPopup(ingredientId); // Reopen the edit popup
                }
            }
        }
    }

    private void editIngredient(int ingredientId, String newName) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String updateIngredientSQL = "UPDATE ingredients SET name = '" + newName + "' WHERE ingredient_id = " + ingredientId;
            statement.executeUpdate(updateIngredientSQL);
            loadIngredients(); // Refresh the table

        } catch (SQLException e) {
            showError(e);
        }
    }

    private void deleteIngredient(int ingredientId) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String deleteIngredientSQL = "DELETE FROM ingredients WHERE ingredient_id = " + ingredientId;
            statement.executeUpdate(deleteIngredientSQL);
            loadIngredients(); // Refresh the table

        } catch (SQLException e) {
            showError(e);
        }
    }

    private void showAddRecipePopup() {
        JTextField recipeNameField = new JTextField();
        JTextField recipeInstructionsField = new JTextField();
        Object[] message = {
            "Recipe Name:", recipeNameField,
            "Instructions:", recipeInstructionsField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Add Recipe", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String recipeName = recipeNameField.getText();
            String instructions = recipeInstructionsField.getText();
            if (!recipeName.trim().isEmpty() && !instructions.trim().isEmpty()) {
                addRecipe(recipeName, instructions);
            } else {
                JOptionPane.showMessageDialog(frame, "Recipe name and instructions cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addRecipe(String name, String instructions) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            int newRecipeId = getNextRecipeId(statement);
            String insertRecipeSQL = "INSERT INTO recipes (recipe_id, name, instructions) VALUES (" + newRecipeId + ", '" + name + "', '" + instructions + "')";
            statement.executeUpdate(insertRecipeSQL);
            loadRecipes(); // Refresh the table

        } catch (SQLException e) {
            showError(e);
        }
    }

    private void handleEditRecipe() {
        int selectedRow = recipeTable.getSelectedRow();
        if (selectedRow == -1) {
            showSelectRecipePopup();
        } else {
            int recipeId = (int) recipeTableModel.getValueAt(selectedRow, 0);
            showEditRecipePopup(recipeId);
        }
    }

    private void showSelectRecipePopup() {
        JComboBox<String> recipeComboBox = new JComboBox<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT name FROM recipes")) {

            while (resultSet.next()) {
                recipeComboBox.addItem(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            showError(e);
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select Recipe:"));
        panel.add(recipeComboBox);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Select Recipe", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String selectedRecipe = (String) recipeComboBox.getSelectedItem();
            if (selectedRecipe != null) {
                int recipeId = getRecipeIdByName(selectedRecipe);
                showEditRecipePopup(recipeId);
            }
        }
    }

    private int getRecipeIdByName(String name) {
        int recipeId = -1;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT recipe_id FROM recipes WHERE name = '" + name + "'")) {

            if (resultSet.next()) {
                recipeId = resultSet.getInt("recipe_id");
            }
        } catch (SQLException e) {
            showError(e);
        }
        return recipeId;
    }

    private void showEditRecipePopup(int recipeId) {
        String currentName = null;
        String currentInstructions = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT name, instructions FROM recipes WHERE recipe_id = " + recipeId)) {

            if (resultSet.next()) {
                currentName = resultSet.getString("name");
                currentInstructions = resultSet.getString("instructions");
            }
        } catch (SQLException e) {
            showError(e);
        }

        if (currentName != null && currentInstructions != null) {
            JTextField recipeNameField = new JTextField(currentName);
            JTextField recipeInstructionsField = new JTextField(currentInstructions);

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Recipe Name:"));
            panel.add(recipeNameField);
            panel.add(new JLabel("Instructions:"));
            panel.add(recipeInstructionsField);

            int option = JOptionPane.showOptionDialog(frame, panel, "Edit Recipe", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Okay", "Delete", "Cancel"}, "Cancel");

            if (option == 0) { // Okay
                String newName = recipeNameField.getText();
                String newInstructions = recipeInstructionsField.getText();
                if (!newName.trim().isEmpty() && !newInstructions.trim().isEmpty()) {
                    editRecipe(recipeId, newName, newInstructions);
                } else {
                    JOptionPane.showMessageDialog(frame, "Recipe name and instructions cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (option == 1) { // Delete
                int deleteOption = JOptionPane.showConfirmDialog(frame, "Do you want to delete this recipe?", "Delete Recipe", JOptionPane.OK_CANCEL_OPTION);
                if (deleteOption == JOptionPane.OK_OPTION) {
                    deleteRecipe(recipeId);
                } else {
                    showEditRecipePopup(recipeId); // Reopen the edit popup
                }
            }
        }
    }

    private void editRecipe(int recipeId, String newName, String newInstructions) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String updateRecipeSQL = "UPDATE recipes SET name = '" + newName + "', instructions = '" + newInstructions + "' WHERE recipe_id = " + recipeId;
            statement.executeUpdate(updateRecipeSQL);
            loadRecipes(); // Refresh the table

        } catch (SQLException e) {
            showError(e);
        }
    }

    private void deleteRecipe(int recipeId) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String deleteRecipeSQL = "DELETE FROM recipes WHERE recipe_id = " + recipeId;
            statement.executeUpdate(deleteRecipeSQL);
            loadRecipes(); // Refresh the table

        } catch (SQLException e) {
            showError(e);
        }
    }

    private void showAddUserPopup() {
        JTextField userNameField = new JTextField();
        Object[] message = {
            "User Name:", userNameField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String userName = userNameField.getText();
            if (!userName.trim().isEmpty()) {
                addUser(userName);
            } else {
                JOptionPane.showMessageDialog(frame, "User name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addUser(String name) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            int newUserId = getNextUserId(statement);
            String insertUserSQL = "INSERT INTO users (user_id, username) VALUES (" + newUserId + ", '" + name + "')";
            statement.executeUpdate(insertUserSQL);
            loadUsers(); // Refresh the table
            loadUserTables(); // Reload user tables

        } catch (SQLException e) {
            showError(e);
        }
    }

    private void handleEditUser() {
        int selectedRow = userTable.getSelectedRow();
        int userId;
        if (selectedRow == -1) {
            userId = showSelectUserPopup();
        } else {
            userId = (int) userTableModel.getValueAt(selectedRow, 0);
        }
        showEditUserPopup(userId);
    }

    private int showSelectUserPopup() {
        int userId;
        JComboBox<String> userComboBox = new JComboBox<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT username FROM users")) {

            while (resultSet.next()) {
                userComboBox.addItem(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            showError(e);
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select User:"));
        panel.add(userComboBox);

        String selectedUser = null;
        while(selectedUser == null)
        {
                int option = JOptionPane.showConfirmDialog(frame, panel, "Select User", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                        selectedUser = (String) userComboBox.getSelectedItem();
                        if (selectedUser != null) {
                                userId = getUserIdByName(selectedUser);
                                return userId;
                        }
                }
        }
        return -1;
    }

    private int getUserIdByName(String name) {
        int userId = -1;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT user_id FROM users WHERE username = '" + name + "'")) {

            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            showError(e);
        }
        return userId;
    }

    private String getUserNameById(int id) {
        String userName = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT username FROM users WHERE user_id = '" + id + "'")) {

            if (resultSet.next()) {
                userName = resultSet.getString("username");
            }
        } catch (SQLException e) {
            showError(e);
        }
        return userName;
    }

    private void showEditUserPopup(int userId) {
        String currentName = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT username FROM users WHERE user_id = " + userId)) {

            if (resultSet.next()) {
                currentName = resultSet.getString("username");
            }
        } catch (SQLException e) {
            showError(e);
        }

        if (currentName != null) {
            JTextField userNameField = new JTextField(currentName);

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("User Name:"));
            panel.add(userNameField);

            int option = JOptionPane.showOptionDialog(frame, panel, "Edit User", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Okay", "Delete", "Cancel"}, "Cancel");

            if (option == 0) { // Okay
                String newName = userNameField.getText();
                if (!newName.trim().isEmpty()) {
                    editUser(userId, newName);
                } else {
                    JOptionPane.showMessageDialog(frame, "User name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (option == 1) { // Delete
                int deleteOption = JOptionPane.showConfirmDialog(frame, "Do you want to delete this user?", "Delete User", JOptionPane.OK_CANCEL_OPTION);
                if (deleteOption == JOptionPane.OK_OPTION) {
                    deleteUser(userId);
                } else {
                    showEditUserPopup(userId); // Reopen the edit popup
                }
            }
        }
    }

    private void editUser(int userId, String newName) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String updateUserSQL = "UPDATE users SET username = '" + newName + "' WHERE user_id = " + userId;
            statement.executeUpdate(updateUserSQL);
            loadUsers(); // Refresh the table
            loadUserTables(); // Reload user tables

        } catch (SQLException e) {
            showError(e);
        }
    }

    private void deleteUser(int userId) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String deleteUserSQL = "DELETE FROM users WHERE user_id = " + userId;
            statement.executeUpdate(deleteUserSQL);
            loadUsers(); // Refresh the table
            loadUserTables(); // Reload user tables

        } catch (SQLException e) {
            showError(e);
        }
    }

    private int getNextUserId(Statement statement) throws SQLException {
        String getMaxIdSQL = "SELECT NVL(MAX(user_id), 0) AS max_id FROM users";
        try (ResultSet resultSet = statement.executeQuery(getMaxIdSQL)) {
            resultSet.next();
            return resultSet.getInt("max_id") + 1;
        }
    }

    private void handleEditUserIngredients() {
        int selectedRow = userTable.getSelectedRow();
        int userId;
        String userName;
        if (selectedRow == -1) {
        userId = showSelectUserPopup();
        userName = getUserNameById(userId);
        } else {
            userId = (int) userTableModel.getValueAt(selectedRow, 0);
            userName = (String) userTableModel.getValueAt(selectedRow, 1);
        }


        JComboBox<String> ingredientComboBox = new JComboBox<>();
        JTextField quantityField = new JTextField();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT name FROM ingredients")) {

        while (resultSet.next()) {
                ingredientComboBox.addItem(resultSet.getString("name"));
        }
        } catch (SQLException e) {
        showError(e);
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Ingredient:"));
        panel.add(ingredientComboBox);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Edit Ingredients for " + userName, JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
        String ingredientName = (String) ingredientComboBox.getSelectedItem();
        int quantity = Integer.parseInt(quantityField.getText());
        if (ingredientName != null && !ingredientName.trim().isEmpty() && quantity > 0) {
                editUserIngredients(userId, ingredientName, quantity);
        } else {
                JOptionPane.showMessageDialog(frame, "Ingredient and quantity cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        }
    }

    private void editUserIngredients(int userId, String ingredientName, int quantity) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String getIngredientIdSQL = "SELECT ingredient_id FROM ingredients WHERE name = '" + ingredientName + "'";
            int ingredientId;
            try (ResultSet resultSet = statement.executeQuery(getIngredientIdSQL)) {
                if (resultSet.next()) {
                    ingredientId = resultSet.getInt("ingredient_id");
                } else {
                    throw new SQLException("Ingredient not found: " + ingredientName);
                }
            }

            String updateUserIngredientsSQL = "MERGE INTO user_ingredients ui " +
                                              "USING (SELECT " + userId + " AS user_id, " + ingredientId + " AS ingredient_id FROM dual) src " +
                                              "ON (ui.user_id = src.user_id AND ui.ingredient_id = src.ingredient_id) " +
                                              "WHEN MATCHED THEN " +
                                              "UPDATE SET ui.quantity = " + quantity + " " +
                                              "WHEN NOT MATCHED THEN " +
                                              "INSERT (ui.user_id, ui.ingredient_id, ui.quantity) " +
                                              "VALUES (" + userId + ", " + ingredientId + ", " + quantity + ")";

            statement.executeUpdate(updateUserIngredientsSQL);
            loadUserTables(); // Reload user tables

        } catch (SQLException e) {
            showError(e);
        }
    }

    private void handleChangeUser() {
        JComboBox<String> userComboBox = new JComboBox<>();
        userComboBox.addItem("Admin"); // Add admin option
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT username FROM users")) {

            while (resultSet.next()) {
                userComboBox.addItem(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            showError(e);
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Select User:"));
        panel.add(userComboBox);

        int option = JOptionPane.showConfirmDialog(frame, panel, "Change User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String selectedUser = (String) userComboBox.getSelectedItem();
            if (selectedUser != null) {
                if (selectedUser.equals("Admin")) {
                    currentUserId = -1; // Admin view
                } else {
                    currentUserId = getUserIdByName(selectedUser);
                }
                updateUIForCurrentUser();
            }
        }
    }

    private void updateUIForCurrentUser() {
        topPanel.removeAll();
        if (currentUserId == -1) {
            // Admin view: show all tables and buttons
            for (JPanel tablePanel : otherTables) {
                tablePanel.setVisible(true);
            }
            addIngredientButton.setVisible(true);
            editIngredientButton.setVisible(true);
            addRecipeButton.setVisible(true);
            editRecipeButton.setVisible(true);
            addUserButton.setVisible(true);
            editUserButton.setVisible(true);
            editUserIngredientsButton.setVisible(true);
        } else {
            // User view: show only user's ingredients and relevant buttons
            for (JPanel tablePanel : otherTables) {
                tablePanel.setVisible(true);
            }
            recipeTablePanel.setVisible(true); // Ensure the recipe table stays visible
            addIngredientButton.setVisible(false);
            editIngredientButton.setVisible(false);
            addRecipeButton.setVisible(false);
            editRecipeButton.setVisible(false);
            addUserButton.setVisible(false);
            editUserButton.setVisible(false);
            editUserIngredientsButton.setVisible(true);
        }
        refreshUserTables(bottomPanel);
        refreshOtherTables(topPanel);
        loadRecipes(); // Ensure the recipe table is populated
        frame.revalidate();
        frame.repaint();
    }

    private void showError(SQLException e) {
        JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BasicRecipeBackup2::new);
    }
}
