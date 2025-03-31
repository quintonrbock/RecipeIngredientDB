package com.yourname.recipedb.old;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public abstract class BasicRecipeClass_User extends BasicRecipeClass {
    @Override
    public void loadUsers() {
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

    @Override
    public void loadUserTables() {
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

    @Override
    public void loadUserIngredients(int userId, DefaultTableModel tableModel) {
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

    @Override
    public void refreshUserTables(JPanel bottomPanel) {
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

    @Override
    public void refreshOtherTables(JPanel topPanel) {
        SwingUtilities.invokeLater(() -> {
            topPanel.removeAll();
            topPanel.setLayout(new GridLayout(1, 3));

            // Ensure the recipe table is always visible
            recipeTablePanel.setVisible(true);
            topPanel.add(recipeTablePanel);

            if (currentUserId == -1) {
                // Admin view: show all tables
                for (int i = 0; i < otherTables.size(); i++) {
                    JPanel otherTablePanel = otherTables.get(i);
                    otherTablePanel.setVisible(true);
                    topPanel.add(otherTablePanel);
                }
            }

            frame.revalidate();
            frame.repaint();
        });
    }

    @Override
    public void showAddUserPopup() {
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

    @Override
    public void addUser(String name) {
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

    @Override
    public void showEditUserPopup(int userId) {
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

    @Override
    public void editUser(int userId, String newName) {
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

    @Override
    public void handleEditUser() {
        int selectedRow = userTable.getSelectedRow();
        int userId;
        if (selectedRow == -1) {
            userId = showSelectUserPopup();
        } else {
            userId = (int) userTableModel.getValueAt(selectedRow, 0);
        }
        showEditUserPopup(userId);
    }

    @Override
    public void deleteUser(int userId) {
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

    @Override
    public int showSelectUserPopup() {
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
        while (selectedUser == null) {
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

    @Override
    public void editUserIngredients(int userId, String ingredientName, int quantity) {
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

    @Override
    public void handleEditUserIngredients() {
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

    @Override
    public void updateUIForCurrentUser() {
        SwingUtilities.invokeLater(() -> {
            topPanel.removeAll();
            topPanel.setLayout(new GridLayout(1, 3));

            // Ensure the recipe table is always visible at the top
            recipeTablePanel.setVisible(true);
            topPanel.add(recipeTablePanel);

            if (currentUserId == -1) {
                // Admin view: show all tables and buttons
                userTablePanel.setVisible(true); // Show the users table for admin
                topPanel.add(userTablePanel);

                for (JPanel tablePanel : otherTables) {
                    tablePanel.setVisible(true);
                    topPanel.add(tablePanel);
                }

                addIngredientButton.setVisible(true);
                editIngredientButton.setVisible(true);
                addRecipeButton.setVisible(true);
                editRecipeButton.setVisible(true);
                addUserButton.setVisible(true);
                editUserButton.setVisible(true);
                editUserIngredientsButton.setVisible(true);
            } else {
                // User view: Hide the Users table, but keep Recipes visible
                userTablePanel.setVisible(false);

                // Show only the user's ingredient table
                int userIndex = currentUserId - 1; // Assuming user IDs start from 1
                if (userIndex >= 0 && userIndex < userIngredientsTables.size()) {
                    JPanel userIngredientsTablePanel = new JPanel(new BorderLayout());
                    userIngredientsTablePanel.add(userIngredientsLabels.get(userIndex), BorderLayout.NORTH);
                    userIngredientsTablePanel.add(new JScrollPane(userIngredientsTables.get(userIndex)), BorderLayout.CENTER);
                    topPanel.add(userIngredientsTablePanel);
                }

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
            loadRecipes(); // Ensure the recipe table is repopulated

            frame.revalidate();
            frame.repaint();
        });
    }

    @Override
    public void handleChangeUser() {
        JComboBox<String> userComboBox = new JComboBox<>();
        userComboBox.addItem("Admin"); // Add admin option

        List<String> userNames = getUserNames(); // Get correctly sorted list
        for (String user : userNames) {
            userComboBox.addItem(user);
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
                    currentUserId = getUserIdByName(selectedUser); // Get correct user ID
                }
                updateUIForCurrentUser();
            }
        }
    }

    @Override
    public List<String> getUserNames() {
        List<String> userNames = new ArrayList<>();
        String query = "SELECT username FROM users ORDER BY user_id"; // Ensure ordered list

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

    @Override
    public int getUserIdByName(String name) {
        int userId = -1;
        String query = "SELECT user_id FROM users WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            showError(e);
        }
        return userId;
    }

    @Override
    public String getUserNameById(int id) {
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

    @Override
    public int getNextUserId(Statement statement) throws SQLException {
        String getMaxIdSQL = "SELECT NVL(MAX(user_id), 0) AS max_id FROM users";
        try (ResultSet resultSet = statement.executeQuery(getMaxIdSQL)) {
            resultSet.next();
            return resultSet.getInt("max_id") + 1;
        }
    }

    @Override
    public void showError(SQLException e) {
        JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}