package com.yourname.recipedb.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.yourname.recipedb.controller.ActiveUserManager;
import com.yourname.recipedb.controller.UserController;

public class MainFrame extends JFrame {

    private final IngredientPanel ingredientPanel;
    private final RecipePanel recipePanel;
    private final UserPanel userPanel;
    private final JPanel mainPanel;
    private final JPanel buttonPanel;

    public MainFrame() {
        // Set up main frame properties
        if (ActiveUserManager.isAdmin()) {
            setTitle("Recipe Database   User: Admin");
        } else {
            setTitle("Recipe Database   User: " + ActiveUserManager.getActiveUser().getUsername());
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 800);
        setLayout(new BorderLayout());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getSize().width / 2, screenSize.height / 2 - getSize().height / 2);

        // Initialize panels
        ingredientPanel = new IngredientPanel();
        recipePanel = new RecipePanel();
        userPanel = new UserPanel();
        mainPanel = new JPanel(new CardLayout());
        buttonPanel = new JPanel(new GridLayout(1, 4, 20, 0));

        // Add panels to mainPanel
        mainPanel.add(ingredientPanel, "Ingredients");
        mainPanel.add(recipePanel, "Recipes");
        mainPanel.add(userPanel, "Users");

        // Create navigation buttons
        JButton ingredientButton;
        if(ActiveUserManager.isAdmin())
            ingredientButton = new JButton("All Ingredients");
        else
            ingredientButton = new JButton("My Ingredients");
        JButton recipeButton = new JButton("All Recipes");
        JButton userButton = new JButton("Users");
        JButton changeUserButton = new JButton("Change User");

        // Button click actions to switch views
        ingredientButton.addActionListener(e -> switchPanel("Ingredients"));
        recipeButton.addActionListener(e -> switchPanel("Recipes"));
        userButton.addActionListener(e -> switchPanel("Users"));
        changeUserButton.addActionListener(e -> UserController.switchUser(this));

        // Add buttons to button panel
        buttonPanel.add(ingredientButton);
        buttonPanel.add(recipeButton);
        if(ActiveUserManager.isAdmin())
            buttonPanel.add(userButton);
        buttonPanel.add(new JPanel()); // Add an empty panel to create space
        buttonPanel.add(changeUserButton);

        // Add components to main frame
        add(buttonPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Set initial visibility
        setVisible(true);
    }

    // Switch between panels
    private void switchPanel(String panelName) {
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, panelName);
    }

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
