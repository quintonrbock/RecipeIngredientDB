package com.yourname.recipedb.old;

import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class BasicRecipeClass_Ingredient extends BasicRecipeClass_User {
    @Override
    public void loadIngredients() {
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

    @Override
    public void showSelectIngredientPopup() {
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

    @Override
    public void showAddIngredientPopup() {
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

    @Override
    public void addIngredient(String name) {
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

    @Override
    public void showEditIngredientPopup(int ingredientId) {
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

    @Override
    public void editIngredient(int ingredientId, String newName) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String updateIngredientSQL = "UPDATE ingredients SET name = '" + newName + "' WHERE ingredient_id = " + ingredientId;
            statement.executeUpdate(updateIngredientSQL);
            loadIngredients(); // Refresh the table

        } catch (SQLException e) {
            showError(e);
        }
    }

    @Override
    public void handleEditIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow == -1) {
            showSelectIngredientPopup();
        } else {
            int ingredientId = (int) ingredientTableModel.getValueAt(selectedRow, 0);
            showEditIngredientPopup(ingredientId);
        }
    }

    @Override
    public void deleteIngredient(int ingredientId) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String deleteIngredientSQL = "DELETE FROM ingredients WHERE ingredient_id = " + ingredientId;
            statement.executeUpdate(deleteIngredientSQL);
            loadIngredients(); // Refresh the table

        } catch (SQLException e) {
            showError(e);
        }
    }

    @Override
    public int getNextIngredientId(Statement statement) throws SQLException {
        String getMaxIdSQL = "SELECT NVL(MAX(ingredient_id), 0) AS max_id FROM ingredients";
        try (ResultSet resultSet = statement.executeQuery(getMaxIdSQL)) {
            resultSet.next();
            return resultSet.getInt("max_id") + 1;
        }
    }

    @Override
    public int getIngredientIdByName(String name) {
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
    
    @Override
    public void showError(SQLException e) {
        JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
