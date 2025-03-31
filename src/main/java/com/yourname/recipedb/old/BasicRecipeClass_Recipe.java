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

public class BasicRecipeClass_Recipe extends BasicRecipeClass_Ingredient {
    @Override
    public void loadRecipes() {
        recipeTableModel.setRowCount(0); // Clear table before loading
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

        // Force the table to be visible
        recipeTablePanel.setVisible(true);
        frame.revalidate();
        frame.repaint();
    }

    @Override
    public void showSelectRecipePopup() {
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

    @Override
    public void showAddRecipePopup() {
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

    @Override
    public void addRecipe(String name, String instructions) {
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

    @Override
    public void showEditRecipePopup(int recipeId) {
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

    @Override
    public void editRecipe(int recipeId, String newName, String newInstructions) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String updateRecipeSQL = "UPDATE recipes SET name = '" + newName + "', instructions = '" + newInstructions + "' WHERE recipe_id = " + recipeId;
            statement.executeUpdate(updateRecipeSQL);
            loadRecipes(); // Refresh the table

        } catch (SQLException e) {
            showError(e);
        }
    }

    @Override
    public void handleEditRecipe() {
        int selectedRow = recipeTable.getSelectedRow();
        if (selectedRow == -1) {
            showSelectRecipePopup();
        } else {
            int recipeId = (int) recipeTableModel.getValueAt(selectedRow, 0);
            showEditRecipePopup(recipeId);
        }
    }

    @Override
    public void deleteRecipe(int recipeId) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String deleteRecipeSQL = "DELETE FROM recipes WHERE recipe_id = " + recipeId;
            statement.executeUpdate(deleteRecipeSQL);
            loadRecipes(); // Refresh the table

        } catch (SQLException e) {
            showError(e);
        }
    }

    @Override
    public int getNextRecipeId(Statement statement) throws SQLException {
        String getMaxIdSQL = "SELECT NVL(MAX(recipe_id), 0) AS max_id FROM recipes";
        try (ResultSet resultSet = statement.executeQuery(getMaxIdSQL)) {
            resultSet.next();
            return resultSet.getInt("max_id") + 1;
        }
    }

    @Override
    public int getRecipeIdByName(String name) {
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

    @Override
    public void showError(SQLException e) {
        JOptionPane.showMessageDialog(frame, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
