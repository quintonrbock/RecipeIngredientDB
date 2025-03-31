package com.yourname.recipedb.old;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class BasicRecipe {

    public BasicRecipe() {
        BasicRecipeClass basicRecipe = new BasicRecipeClass_Recipe();

        BasicRecipeClass.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BasicRecipeClass.frame.setSize(1600, 800);
        BasicRecipeClass.frame.setLayout(new BorderLayout());
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        BasicRecipeClass.frame.setLocation(screenSize.width / 2 - BasicRecipeClass.frame.getSize().width / 2, screenSize.height / 2 - BasicRecipeClass.frame.getSize().height / 2);

        // Initialize buttons
        BasicRecipeClass.addIngredientButton.addActionListener(e -> basicRecipe.showAddIngredientPopup());
        BasicRecipeClass.editIngredientButton.addActionListener(e -> basicRecipe.handleEditIngredient());
        BasicRecipeClass.addRecipeButton.addActionListener(e -> basicRecipe.showAddRecipePopup());
        BasicRecipeClass.editRecipeButton.addActionListener(e -> basicRecipe.handleEditRecipe());
        BasicRecipeClass.addUserButton.addActionListener(e -> basicRecipe.showAddUserPopup());
        BasicRecipeClass.editUserButton.addActionListener(e -> basicRecipe.handleEditUser());
        BasicRecipeClass.editUserIngredientsButton.addActionListener(e -> basicRecipe.handleEditUserIngredients());
        BasicRecipeClass.changeUserButton.addActionListener(e -> {
            basicRecipe.handleChangeUser();
            BasicRecipeClass.topPanel.revalidate();
            BasicRecipeClass.topPanel.repaint();
        });

        // Add buttons to button panel
        BasicRecipeClass.buttonPanel.add(BasicRecipeClass.addIngredientButton);
        BasicRecipeClass.buttonPanel.add(BasicRecipeClass.editIngredientButton);
        BasicRecipeClass.buttonPanel.add(BasicRecipeClass.addRecipeButton);
        BasicRecipeClass.buttonPanel.add(BasicRecipeClass.editRecipeButton);
        BasicRecipeClass.buttonPanel.add(BasicRecipeClass.addUserButton);
        BasicRecipeClass.buttonPanel.add(BasicRecipeClass.editUserButton);
        BasicRecipeClass.buttonPanel.add(BasicRecipeClass.editUserIngredientsButton);
        BasicRecipeClass.buttonPanel.add(BasicRecipeClass.changeUserButton);
        
        // Recipe Table
        JScrollPane recipeScrollPane = new JScrollPane(BasicRecipeClass.recipeTable);
        BasicRecipeClass.recipeTablePanel.add(BasicRecipeClass.recipeTableLabel, BorderLayout.NORTH);
        BasicRecipeClass.recipeTablePanel.add(recipeScrollPane, BorderLayout.CENTER);
        BasicRecipeClass.topPanel.add(BasicRecipeClass.recipeTablePanel);
        BasicRecipeClass.otherTables.add(BasicRecipeClass.recipeTablePanel);
        BasicRecipeClass.otherTablesModels.add(BasicRecipeClass.recipeTableModel);
        BasicRecipeClass.otherTablesLabels.add(BasicRecipeClass.recipeTableLabel);

        // Ingredient Table
        JScrollPane ingredientScrollPane = new JScrollPane(BasicRecipeClass.ingredientTable);
        BasicRecipeClass.ingredientTablePanel.add(BasicRecipeClass.ingredientTableLabel, BorderLayout.NORTH);
        BasicRecipeClass.ingredientTablePanel.add(ingredientScrollPane, BorderLayout.CENTER);
        BasicRecipeClass.topPanel.add(BasicRecipeClass.ingredientTablePanel);
        BasicRecipeClass.otherTables.add(BasicRecipeClass.ingredientTablePanel);
        BasicRecipeClass.otherTablesModels.add(BasicRecipeClass.ingredientTableModel);
        BasicRecipeClass.otherTablesLabels.add(BasicRecipeClass.ingredientTableLabel);

        // User Table
        JScrollPane userScrollPane = new JScrollPane(BasicRecipeClass.userTable);
        BasicRecipeClass.userTablePanel.add(BasicRecipeClass.userTableLabel, BorderLayout.NORTH);
        BasicRecipeClass.userTablePanel.add(userScrollPane, BorderLayout.CENTER);
        BasicRecipeClass.topPanel.add(BasicRecipeClass.userTablePanel);
        BasicRecipeClass.otherTables.add(BasicRecipeClass.userTablePanel);
        BasicRecipeClass.otherTablesModels.add(BasicRecipeClass.userTableModel);
        BasicRecipeClass.otherTablesLabels.add(BasicRecipeClass.userTableLabel);

        // Initialize bottom panel with user ingredient tables
        basicRecipe.refreshUserTables(BasicRecipeClass.bottomPanel);

        // Main panel to hold top and bottom panels
        BasicRecipeClass.mainPanel.add(BasicRecipeClass.topPanel);
        BasicRecipeClass.mainPanel.add(BasicRecipeClass.bottomPanel);

        BasicRecipeClass.frame.add(BasicRecipeClass.mainPanel, BorderLayout.CENTER);
        BasicRecipeClass.frame.add(BasicRecipeClass.buttonPanel, BorderLayout.SOUTH);
        
        BasicRecipeClass.frame.setVisible(true);
        basicRecipe.loadRecipes();
        basicRecipe.loadIngredients();
        basicRecipe.loadUsers();
        basicRecipe.loadUserTables(); // Load user tables after initializing the frame
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BasicRecipe::new);
    }
}
