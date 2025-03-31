package com.yourname.recipedb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.yourname.recipedb.util.Constants;

public class DatabaseManager {
    private static final String URL = Constants.DB_URL;
    private static final String USER = Constants.DB_USER;
    private static final String PASSWORD = Constants.DB_PASSWORD;

    private static Connection connection;

    // Get database connection (Reconnect if closed)
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
        return connection;
    }

    // Close the connection when the application stops
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    // Initialize the database tables if they don't exist
    public static void initializeDatabase() {
        try (Statement stmt = getConnection().createStatement()) {
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                                      "user_id NUMBER PRIMARY KEY, " +
                                      "first_name VARCHAR2(255), " +
                                      "last_name VARCHAR2(255), " +
                                      "username VARCHAR2(255) UNIQUE NOT NULL, " +
                                      "password VARCHAR2(255) NOT NULL)";

            String createIngredientsTable = "CREATE TABLE IF NOT EXISTS ingredients (" +
                                            "ingredient_id NUMBER PRIMARY KEY, " +
                                            "name VARCHAR2(255) UNIQUE NOT NULL)";

            String createRecipesTable = "CREATE TABLE Recipe (" +
                                            "recipe_id NUMBER PRIMARY KEY, " +
                                            "name VARCHAR2(255) UNIQUE NOT NULL, " +
                                            "category VARCHAR2(255), " +
                                            "servings NUMBER, " +
                                            "instructions CLOB)";
                
            String createRecipeCookingMethodsTable = "CREATE TABLE Recipe_Cooking_Methods (" +
                                                    "id NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                                                    "recipe_id NUMBER, " +
                                                    "cook_method VARCHAR2(255) NOT NULL, " +
                                                    "cook_temp VARCHAR2(50) NOT NULL, " +
                                                    "minimum_cook_time NUMBER DEFAULT 0, " +
                                                    "maximum_cook_time NUMBER NOT NULL, " +
                                                    "FOREIGN KEY (recipe_id) REFERENCES Recipe(recipe_id) ON DELETE CASCADE)";
                

            String createRecipeIngredientsTable = "CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                                                  "recipe_id NUMBER, " +
                                                  "ingredient_id NUMBER, " +
                                                  "quantity NUMBER, " +
                                                  "unit VARCHAR2(50), " +
                                                  "PRIMARY KEY (recipe_id, ingredient_id), " +
                                                  "FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id), " +
                                                  "FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id))";

            String createUserIngredientsTable = "CREATE TABLE IF NOT EXISTS user_ingredients (" +
                                                "user_id NUMBER, " +
                                                "ingredient_id NUMBER, " +
                                                "quantity NUMBER, " +
                                                "PRIMARY KEY (user_id, ingredient_id), " +
                                                "FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                                                "FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id))";

            String resetSequenceSQL = "DECLARE max_id NUMBER; " +
                                        "BEGIN " +
                                        "SELECT COALESCE(MAX(recipe_id), 0) + 1 INTO max_id FROM recipe; " +
                                        "EXECUTE IMMEDIATE 'DROP SEQUENCE recipe_id_seq'; " +
                                        "EXECUTE IMMEDIATE 'CREATE SEQUENCE recipe_id_seq START WITH ' || TO_CHAR(max_id) || ' INCREMENT BY 1 NOCACHE'; " +
                                        "END;";

            // Execute all table creation queries
            stmt.executeUpdate(createUsersTable);
            stmt.executeUpdate(createIngredientsTable);
            stmt.executeUpdate(createRecipesTable);
            stmt.executeUpdate(createRecipeCookingMethodsTable);
            stmt.executeUpdate(createRecipeIngredientsTable);
            stmt.executeUpdate(createUserIngredientsTable);
            stmt.executeUpdate(resetSequenceSQL);
            System.out.println("Recipe ID sequence reset successfully.");

            System.out.println("(Database Manager) Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
