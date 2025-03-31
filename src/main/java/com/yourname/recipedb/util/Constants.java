package com.yourname.recipedb.util;

public class Constants {
    
    // Database connection details
    public static final String DB_URL = "jdbc:oracle:thin:@//127.0.0.1:1521/XE";
    public static final String DB_USER = "newUser";
    public static final String DB_PASSWORD = "newPass";

    // Table column names
    public static final String[] RECIPE_COLUMNS = {"Recipe ID", "Name", "Estimated Time", "Instructions"};
    public static final String[] INGREDIENT_COLUMNS = {"Ingredient ID", "Name"};
    public static final String[] USER_COLUMNS = {"User ID", "Username"};
    
    // UI dimensions
    public static final int FRAME_WIDTH = 1500;
    public static final int FRAME_HEIGHT = 1000;
    
    // Messages
    public static final String ERROR_MISSING_FIELDS = "All fields must be filled.";
    public static final String ERROR_INVALID_NUMBER = "Estimated time must be a number.";
    public static final String CONFIRM_DELETE_RECIPE = "Are you sure you want to delete this recipe?";
    public static final String CONFIRM_DELETE_INGREDIENT = "Are you sure you want to delete this ingredient?";
    public static final String CONFIRM_DELETE_USER = "Are you sure you want to delete this user?";
}
