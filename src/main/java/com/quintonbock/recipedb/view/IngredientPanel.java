package com.yourname.recipedb.view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.yourname.recipedb.DataFetcher;
import com.yourname.recipedb.DataUpdater;
import com.yourname.recipedb.controller.ActiveUserManager;
import com.yourname.recipedb.model.Ingredient;

public class IngredientPanel extends JPanel {

    private final JTable ingredientTable;
    private final DefaultTableModel ingredientTableModel;
    private final JButton addIngredientButton, editIngredientButton, deleteIngredientButton;

    public IngredientPanel() {
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames;
        if(ActiveUserManager.isAdmin())
            columnNames = new String[]{"Ingredient ID", "Name"};
        else
            columnNames = new String[]{"Ingredient ID", "Name", "Quantity"};
        ingredientTableModel = new DefaultTableModel(columnNames, 0);
        ingredientTable = new JTable(ingredientTableModel);
        initialize();

        // Buttons setup
        addIngredientButton = new JButton("Add Ingredient");
        editIngredientButton = new JButton("Edit Ingredient");
        deleteIngredientButton = new JButton("Delete Ingredient");

        addIngredientButton.addActionListener(e -> addIngredient());
        editIngredientButton.addActionListener(e -> editIngredient());
        deleteIngredientButton.addActionListener(e -> deleteIngredient());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addIngredientButton);
        buttonPanel.add(editIngredientButton);
        buttonPanel.add(deleteIngredientButton);

        // Show buttons only if admin
        buttonPanel.setVisible(ActiveUserManager.isAdmin());

        // Add components
        add(new JScrollPane(ingredientTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initialize() {
        loadIngredients();
    }

    public void loadIngredients() {
        ingredientTableModel.setRowCount(0);
    
        List<Ingredient> ingredients;
        if (ActiveUserManager.isAdmin()) {
            ingredients = DataFetcher.getAllIngredients();
        } else {
            ingredients = DataFetcher.getUserIngredients(ActiveUserManager.getActiveUser().getId());
        }
    
        for (Ingredient ingredient : ingredients) {
            ingredientTableModel.addRow(new Object[]{
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getQuantity(),
                ingredient.getUnit()
            });
        }
    }
    

    private void addIngredient() {
        JTextField nameField = new JTextField();

        Object[] fields = {"Ingredient Name:", nameField};

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Ingredient", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                DataUpdater.addIngredient(name);
                loadIngredients();
            } else {
                JOptionPane.showMessageDialog(this, "Ingredient name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an ingredient to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int ingredientId = (int) ingredientTableModel.getValueAt(selectedRow, 0);
        String currentName = (String) ingredientTableModel.getValueAt(selectedRow, 1);

        JTextField nameField = new JTextField(currentName);

        Object[] fields = {"Ingredient Name:", nameField};

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit Ingredient", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            if (!newName.isEmpty()) {
                DataUpdater.updateIngredient(ingredientId, newName);
                loadIngredients();
            } else {
                JOptionPane.showMessageDialog(this, "Ingredient name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an ingredient to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int ingredientId = (int) ingredientTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this ingredient?", "Delete Ingredient", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DataUpdater.deleteIngredient(ingredientId);
            loadIngredients();
        }
    }

    public JTable getTable() {
        return ingredientTable;
    }
    
    public DefaultTableModel getTableModel() {
        return ingredientTableModel;
    }
    
}
