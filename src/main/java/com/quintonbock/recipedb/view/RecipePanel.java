package com.yourname.recipedb.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.yourname.recipedb.DataFetcher;
import com.yourname.recipedb.DataUpdater;
import com.yourname.recipedb.controller.ActiveUserManager;
import com.yourname.recipedb.model.CookingMethod;
import com.yourname.recipedb.model.Recipe;

public class RecipePanel extends JPanel {

    private final JTable recipeTable;
    private final DefaultTableModel recipeTableModel;
    private final JButton addRecipeButton, editRecipeButton, deleteRecipeButton;

    public RecipePanel() {
        setLayout(new BorderLayout());

        // Table setup
        String[] columnNames = {"Recipe ID", "Name", "Category", "Servings", "Instructions", "Cook Method", "Cook Temp", "Cook Time"};
        recipeTableModel = new DefaultTableModel(columnNames, 0);
        recipeTable = new JTable(recipeTableModel);
        loadRecipes();


        // Buttons setup
        addRecipeButton = new JButton("Add Recipe");
        editRecipeButton = new JButton("Edit Recipe");
        deleteRecipeButton = new JButton("Delete Recipe");

        addRecipeButton.addActionListener(e -> addRecipe());
        editRecipeButton.addActionListener(e -> editRecipe());
        deleteRecipeButton.addActionListener(e -> deleteRecipe());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addRecipeButton);
        buttonPanel.add(editRecipeButton);
        buttonPanel.add(deleteRecipeButton);

        // Show buttons only if admin
        buttonPanel.setVisible(ActiveUserManager.isAdmin());

        // Add components
        add(new JScrollPane(recipeTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadRecipes() {
        recipeTableModel.setRowCount(0); // Clear existing table data
    
        List<Recipe> recipes = DataFetcher.getAllRecipes();
        System.out.println("Recipes loaded into table: " + recipes.size()); // Debugging line

    
        for (Recipe recipe : recipes) {
            for (CookingMethod method : recipe.getCookingMethods()) {
                recipeTableModel.addRow(new Object[]{
                    recipe.getId(), recipe.getName(), recipe.getCategory(), recipe.getServings(),
                    recipe.getInstructions(), method.getCookMethod(), method.getCookTemp(),
                    method.getMinCookTime() + "-" + method.getMaxCookTime() + " min"
                });
            }
    
            // If no cooking methods exist, show recipe without methods
            if (recipe.getCookingMethods().isEmpty()) {
                recipeTableModel.addRow(new Object[]{
                    recipe.getId(), recipe.getName(), recipe.getCategory(), recipe.getServings(),
                    recipe.getInstructions(), "N/A", "N/A", "N/A"
                });
            }
        }
    
        System.out.println("Recipes loaded into table: " + recipeTableModel.getRowCount());
    }
    

    private void addRecipe() {
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField servingsField = new JTextField();
        JTextArea instructionsArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(instructionsArea);

        JPanel cookingMethodsPanel = new JPanel();
        cookingMethodsPanel.setLayout(new BoxLayout(cookingMethodsPanel, BoxLayout.Y_AXIS));
        List<JTextField[]> cookingMethodFields = new ArrayList<>();
        addCookingMethodRow(cookingMethodsPanel, cookingMethodFields);

        Object[] fields = {
            "Name:", nameField,
            "Category:", categoryField,
            "Servings:", servingsField,
            "Instructions:", scrollPane,
            "Cooking Methods:", cookingMethodsPanel
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Recipe", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int id = DataFetcher.getNextRecipeId();
            String name;
            String category;
            String instructions;
            int servings;

            try {
                name = nameField.getText().trim();
                category = categoryField.getText().trim();
                instructions = instructionsArea.getText().trim();
                servings = Integer.parseInt(servingsField.getText().trim());
                List<CookingMethod> cookingMethods = collectCookingMethods(cookingMethodFields, id);
                
                if (!name.isEmpty() && !category.isEmpty() && !instructions.isEmpty() && !cookingMethods.isEmpty()) {
                    DataUpdater.addRecipe(id, name, category, servings, cookingMethods, instructions);
                    loadRecipes();
                } else {
                    JOptionPane.showMessageDialog(this, "All fields and at least one cooking method must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Servings must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void editRecipe() {
        int selectedRow = recipeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a recipe to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        int recipeId = (int) recipeTableModel.getValueAt(selectedRow, 0);
        String currentName = (String) recipeTableModel.getValueAt(selectedRow, 1);
        String currentCategory = (String) recipeTableModel.getValueAt(selectedRow, 2);
        int currentServings = (int) recipeTableModel.getValueAt(selectedRow, 3);
        String currentInstructions = (String) recipeTableModel.getValueAt(selectedRow, 4);
    
        JTextField nameField = new JTextField(currentName);
        JTextField categoryField = new JTextField(currentCategory);
        JTextField servingsField = new JTextField(String.valueOf(currentServings));
        JTextArea instructionsArea = new JTextArea(currentInstructions, 5, 20);
        JScrollPane scrollPane = new JScrollPane(instructionsArea);
    
        JPanel cookingMethodsPanel = new JPanel();
        cookingMethodsPanel.setLayout(new BoxLayout(cookingMethodsPanel, BoxLayout.Y_AXIS));
        List<JTextField[]> cookingMethodFields = new ArrayList<>();
        
        // Load existing cooking methods into UI
        List<CookingMethod> currentCookingMethods = DataFetcher.getCookingMethodsForRecipe(recipeId);
        for (CookingMethod method : currentCookingMethods) {
            addCookingMethodRowWithValues(cookingMethodsPanel, cookingMethodFields, method);
        }
    
        Object[] fields = {
            "Name:", nameField,
            "Category:", categoryField,
            "Servings:", servingsField,
            "Instructions:", scrollPane,
            "Cooking Methods:", cookingMethodsPanel
        };
    
        int option = JOptionPane.showConfirmDialog(this, fields, "Edit Recipe", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newCategory = categoryField.getText().trim();
            String newInstructions = instructionsArea.getText().trim();
            int newServings;
    
            try {
                newServings = Integer.parseInt(servingsField.getText().trim());
                List<CookingMethod> newCookingMethods = collectCookingMethods(cookingMethodFields, recipeId);
    
                if (!newName.isEmpty() && !newCategory.isEmpty() && !newInstructions.isEmpty() && !newCookingMethods.isEmpty()) {
                    DataUpdater.updateRecipe(recipeId, newName, newCategory, newServings, newCookingMethods, newInstructions);
                    loadRecipes();
                } else {
                    JOptionPane.showMessageDialog(this, "All fields and at least one cooking method must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Servings must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    private void deleteRecipe() {
        int selectedRow = recipeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a recipe to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int recipeId = (int) recipeTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this recipe?", "Delete Recipe", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            DataUpdater.deleteRecipe(recipeId);
            loadRecipes();
        }
    }

    private void addCookingMethodRow(JPanel panel, List<JTextField[]> cookingMethodFields) {
        JPanel row = new JPanel(new GridLayout(1, 5, 5, 5));

        JTextField cookMethodField = new JTextField();
        JTextField cookTempField = new JTextField();
        JTextField minCookTimeField = new JTextField();
        JTextField maxCookTimeField = new JTextField();

        row.add(new JLabel("Method:"));
        row.add(cookMethodField);
        row.add(new JLabel("Temp:"));
        row.add(cookTempField);
        row.add(new JLabel("Min Time:"));
        row.add(minCookTimeField);
        row.add(new JLabel("Max Time:"));
        row.add(maxCookTimeField);

        panel.add(row);
        cookingMethodFields.add(new JTextField[]{cookMethodField, cookTempField, minCookTimeField, maxCookTimeField});
        panel.revalidate();
        panel.repaint();
    }

    private void addCookingMethodRowWithValues(JPanel panel, List<JTextField[]> cookingMethodFields, CookingMethod method) {
        JPanel row = new JPanel(new GridLayout(1, 5, 5, 5));
    
        JTextField cookMethodField = new JTextField(method.getCookMethod());
        JTextField cookTempField = new JTextField(method.getCookTemp());
        JTextField minCookTimeField = new JTextField(String.valueOf(method.getMinCookTime()));
        JTextField maxCookTimeField = new JTextField(String.valueOf(method.getMaxCookTime()));
    
        row.add(new JLabel("Method:"));
        row.add(cookMethodField);
        row.add(new JLabel("Temp:"));
        row.add(cookTempField);
        row.add(new JLabel("Min Time:"));
        row.add(minCookTimeField);
        row.add(new JLabel("Max Time:"));
        row.add(maxCookTimeField);
    
        panel.add(row);
        cookingMethodFields.add(new JTextField[]{cookMethodField, cookTempField, minCookTimeField, maxCookTimeField});
        panel.revalidate();
        panel.repaint();
    }
    
    private List<CookingMethod> collectCookingMethods(List<JTextField[]> cookingMethodFields, int recipeId) {
        List<CookingMethod> cookingMethods = new ArrayList<>();
    
        int methodId = 1;
        for (JTextField[] fields : cookingMethodFields) {
            String cookMethod = fields[0].getText().trim();
            String cookTemp = fields[1].getText().trim();
            String minCookTimeStr = fields[2].getText().trim();
            String maxCookTimeStr = fields[3].getText().trim();
    
            if (!cookMethod.isEmpty() && !cookTemp.isEmpty() && !maxCookTimeStr.isEmpty()) {
                int minCookTime = minCookTimeStr.isEmpty() ? 0 : Integer.parseInt(minCookTimeStr);
                int maxCookTime = Integer.parseInt(maxCookTimeStr);
    
                cookingMethods.add(new CookingMethod(methodId, recipeId, cookMethod, cookTemp, minCookTime, maxCookTime));
                methodId++;
            }
        }
    
        return cookingMethods;
    }    

    public JTable getTable() {
        return recipeTable;
    }
    
    public DefaultTableModel getTableModel() {
        return recipeTableModel;
    }
    
}
