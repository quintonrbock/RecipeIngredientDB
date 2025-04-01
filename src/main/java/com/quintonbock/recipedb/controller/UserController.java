package com.yourname.recipedb.controller;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.yourname.recipedb.DataFetcher;
import com.yourname.recipedb.DataUpdater;
import com.yourname.recipedb.model.Ingredient;
import com.yourname.recipedb.model.User;
import com.yourname.recipedb.view.MainFrame;

public class UserController {

    private final DefaultTableModel userTableModel;
    private final List<JPanel> userIngredientPanels;
    private final List<JTable> userIngredientTables;
    private final List<DefaultTableModel> userIngredientTableModels;

    private int currentUserId = -1; // Keeps track of the selected user

    public UserController(JTable userTable, DefaultTableModel userTableModel) {
        this.userTableModel = userTableModel;
        this.userIngredientPanels = new ArrayList<>();
        this.userIngredientTables = new ArrayList<>();
        this.userIngredientTableModels = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        loadUsers();
    }

    public void loadUsers() {
        userTableModel.setRowCount(0);
        List<User> users = DataFetcher.getAllUsers();
        for (User user : users) {
            userTableModel.addRow(new Object[]{
                user.getId(), user.getUsername()
            });
        }
    }

    public void handleChangeUser(Frame parent) {
        List<User> users = DataFetcher.getAllUsers();
        String[] userNames = new String[users.size() + 1];
        userNames[0] = "Admin"; // Add admin option
        for (int i = 0; i < users.size(); i++) {
            userNames[i + 1] = users.get(i).getUsername();
        }

        String selectedUser = (String) JOptionPane.showInputDialog(parent, "Select a user:", "Change User",
                JOptionPane.QUESTION_MESSAGE, null, userNames, userNames.length > 0 ? userNames[0] : null);

        if (selectedUser != null) {
            if (selectedUser.equals("Admin")) {
                currentUserId = -1; // Admin view
            } else {
                User user = DataFetcher.getUserByName(selectedUser);
                if (user != null) {
                    currentUserId = user.getId();
                }
            }
            loadUserTables();
        }
    }

    public void editUserIngredients(Frame parent) {
        if (currentUserId == -1) {
            JOptionPane.showMessageDialog(parent, "Select a user first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Ingredient> ingredients = DataFetcher.getAllIngredients();
        List<Ingredient> userIngredients = DataFetcher.getUserIngredients(currentUserId);

        DefaultListModel<String> availableModel = new DefaultListModel<>();
        DefaultListModel<String> userModel = new DefaultListModel<>();

        for (Ingredient ingredient : ingredients) {
            availableModel.addElement(ingredient.getName());
        }

        for (Ingredient ingredient : userIngredients) {
            userModel.addElement(ingredient.getName());
        }

        JList<String> availableList = new JList<>(availableModel);
        JList<String> userList = new JList<>(userModel);

        JPanel panel = new JPanel(new GridLayout(1, 3));
        panel.add(new JScrollPane(availableList));
        panel.add(new JScrollPane(userList));

        int option = JOptionPane.showConfirmDialog(parent, panel, "Edit User Ingredients", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            List<String> selectedUserIngredients = userList.getSelectedValuesList();
            List<Ingredient> updatedIngredients = new ArrayList<>();

            for (String ingredientName : selectedUserIngredients) {
                for (Ingredient ingredient : ingredients) {
                    if (ingredient.getName().equals(ingredientName)) {
                        updatedIngredients.add(ingredient);
                    }
                }
            }

            DataUpdater.updateUserIngredients(currentUserId, updatedIngredients);
            loadUserTables();
        }
    }

    public void loadUserTables() {
        userIngredientPanels.clear();
        userIngredientTables.clear();
        userIngredientTableModels.clear();

        List<User> users = DataFetcher.getAllUsers();

        for (User user : users) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createTitledBorder(user.getUsername() + "'s Ingredients"));

            DefaultTableModel model = new DefaultTableModel(new String[]{"Ingredient ID", "Ingredient Name", "Quantity"}, 0);
            JTable table = new JTable(model);

            List<Ingredient> ingredients = DataFetcher.getUserIngredients(user.getId());
            for (Ingredient ingredient : ingredients) {
                model.addRow(new Object[]{ingredient.getId(), ingredient.getName(), ingredient.getQuantity()});
            }

            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            userIngredientPanels.add(panel);
            userIngredientTables.add(table);
            userIngredientTableModels.add(model);
        }
    }

    public List<JPanel> getUserTables() {
        return userIngredientPanels;
    }

    public static void switchUser(JFrame parentFrame) {
        List<User> users = DataFetcher.getAllUsers();
        String[] userNames = new String[users.size() + 1];
        userNames[0] = "Admin"; // Add admin option
        for (int i = 0; i < users.size(); i++) {
            userNames[i + 1] = users.get(i).getUsername();
        }

        String selectedUser = (String) JOptionPane.showInputDialog(
            parentFrame,
            "Select a user:",
            "Change User",
            JOptionPane.QUESTION_MESSAGE,
            null,
            userNames,
            userNames.length > 0 ? userNames[0] : null
        );

        if (selectedUser != null) {
            if (selectedUser.equals("Admin")) {
                ActiveUserManager.setActiveUser(null); // Admin view
                System.out.println("Switched to Admin view");
            } else {
                for (User user : users) {
                    if (user.getUsername().equals(selectedUser)) {
                        ActiveUserManager.setActiveUser(user);
                        System.out.println("Switched to user: " + user.getUsername());
                        break;
                    }
                }
            }
            parentFrame.dispose();  // Close current window
            MainFrame mainFrame = new MainFrame(); // Reload UI with new user context
            mainFrame.setVisible(true);
        }
    }
}
