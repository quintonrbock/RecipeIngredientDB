package com.yourname.recipedb.old;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public abstract class BasicRecipeClass {
    public static final String URL = "jdbc:oracle:thin:@//127.0.0.1:1521/XE";
    public static final String USER = "oldUser";
    public static final String PASSWORD = "oldPass";

    public static final JFrame frame = new JFrame("Recipe Database");

    public static final String[] recipeColumnNames = {"Recipe ID", "Name", "Instructions"};
    public static final DefaultTableModel recipeTableModel = new DefaultTableModel(recipeColumnNames, 0);
    public static final JTable recipeTable = new JTable(BasicRecipeClass.recipeTableModel);

    public static final String[] ingredientColumnNames = {"Ingredient ID", "Name"};
    public static final DefaultTableModel ingredientTableModel = new DefaultTableModel(ingredientColumnNames, 0);
    public static final JTable ingredientTable = new JTable(BasicRecipeClass.ingredientTableModel);

    public static final String[] userColumnNames = {"User ID", "Username"};
    public static final DefaultTableModel userTableModel = new DefaultTableModel(userColumnNames, 0);
    public static final JTable userTable = new JTable(BasicRecipeClass.userTableModel);

    public static final List<DefaultTableModel> userIngredientsTableModels = new ArrayList<>();
    public static final List<JTable> userIngredientsTables = new ArrayList<>();
    public static final List<JLabel> userIngredientsLabels = new ArrayList<>();

    public static final List<DefaultTableModel> otherTablesModels = new ArrayList<>();
    public static final List<JPanel> otherTables = new ArrayList<>();
    public static final List<JLabel> otherTablesLabels = new ArrayList<>();

    public static int currentUserId = -1; // -1 indicates admin view

    public static final JPanel buttonPanel = new JPanel(new GridLayout(1, 10, 20, 0));
    public static final JButton addIngredientButton = new JButton("Add Ingredient");
    public static final JButton editIngredientButton = new JButton("Edit Ingredient");
    public static final JButton addRecipeButton = new JButton("Add Recipe");
    public static final JButton editRecipeButton = new JButton("Edit Recipe");
    public static final JButton addUserButton = new JButton("Add User");
    public static final JButton editUserButton = new JButton("Edit User");
    public static final JButton editUserIngredientsButton = new JButton("Edit User Ingredients");
    public static final JButton changeUserButton = new JButton("Change User");

    public static final JPanel topPanel = new JPanel(new GridLayout(1, 3));
    public static final JPanel bottomPanel = new JPanel(new GridLayout(1, 1));
    public static final JPanel mainPanel = new JPanel(new GridLayout(2, 1));
    public static final JPanel userTablePanel = new JPanel(new BorderLayout());
    public static final JPanel ingredientTablePanel = new JPanel(new BorderLayout());
    public static final JPanel recipeTablePanel = new JPanel(new BorderLayout());

    public static final JLabel userTableLabel = new JLabel("Users");
    public static final JLabel ingredientTableLabel = new JLabel("Ingredients");
    public static final JLabel recipeTableLabel = new JLabel("Recipes");

    public abstract void loadRecipes();
    public abstract void showSelectRecipePopup();
    public abstract void showAddRecipePopup();
    public abstract void showEditRecipePopup(int recipeId);
    public abstract void addRecipe(String name, String instructions);
    public abstract void editRecipe(int recipeId, String newName, String newInstructions);
    public abstract void deleteRecipe(int recipeId);
    public abstract void handleEditRecipe();
    public abstract int getNextRecipeId(Statement statement) throws SQLException;
    public abstract int getRecipeIdByName(String name);

    public abstract void loadIngredients();
    public abstract void showSelectIngredientPopup();
    public abstract void showAddIngredientPopup();
    public abstract void showEditIngredientPopup(int ingredientId);
    public abstract void addIngredient(String name);
    public abstract void editIngredient(int ingredientId, String newName);
    public abstract void deleteIngredient(int ingredientId);
    public abstract void handleEditIngredient();
    public abstract int getNextIngredientId(Statement statement) throws SQLException;
    public abstract int getIngredientIdByName(String name);

    public abstract void loadUsers();
    public abstract void loadUserTables();
    public abstract void loadUserIngredients(int userId, DefaultTableModel tableModel);
    public abstract void refreshUserTables(JPanel bottomPanel);
    public abstract void refreshOtherTables(JPanel topPanel);
    public abstract void updateUIForCurrentUser();
    public abstract int showSelectUserPopup();
    public abstract void showAddUserPopup();
    public abstract void showEditUserPopup(int userId);
    public abstract void addUser(String name);
    public abstract void editUser(int userId, String newName);
    public abstract void editUserIngredients(int userId, String ingredientName, int quantity);
    public abstract void deleteUser(int userId);
    public abstract void handleEditUser();
    public abstract void handleEditUserIngredients();
    public abstract void handleChangeUser();
    public abstract int getNextUserId(Statement statement) throws SQLException;
    public abstract int getUserIdByName(String name);
    public abstract String getUserNameById(int id);
    public abstract List<String> getUserNames();

    public abstract void showError(SQLException e);
}
