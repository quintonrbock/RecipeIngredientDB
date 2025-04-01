package com.yourname.recipedb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.yourname.recipedb.model.CookingMethod;
import com.yourname.recipedb.model.Ingredient;

public class DataUpdater {

    // Add a new user
    public static void addUser(String firstName, String lastName, String username, String password) {
        String query = "INSERT INTO users (user_id, first_name, last_name, username, password) VALUES (?, ?, ?, ?, ?)";
        int nextUserId = DataFetcher.getNextUserId();
    
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setInt(1, nextUserId);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            preparedStatement.setString(4, username);
            preparedStatement.setString(5, password);
            preparedStatement.executeUpdate();
            System.out.println("User added successfully.");

        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    // Add a new ingredient
    public static void addIngredient(String name) {
        String query = "INSERT INTO ingredients (ingredient_id, name) VALUES (?, ?)";
        int ingredientId = DataFetcher.getNextIngredientId();

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, ingredientId);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
            System.out.println("Ingredient added successfully.");

        } catch (SQLException e) {
            System.err.println("Error adding ingredient: " + e.getMessage());
        }
    }


    // Add a new recipe with category, servings, and multiple cooking methods
    public static void addRecipe(int nextRecipeId, String name, String category, int servings, List<CookingMethod> cookingMethods, String instructions) {
        String recipeQuery = "INSERT INTO Recipe (recipe_id, name, category, servings, instructions) VALUES (?, ?, ?, ?, ?)";
        String methodQuery = "INSERT INTO Recipe_Cooking_Methods (recipe_id, cook_method, cook_temp, minimum_cook_time, maximum_cook_time) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            // Insert recipe into the Recipe table
            try (PreparedStatement recipeStmt = connection.prepareStatement(recipeQuery)) {
                recipeStmt.setInt(1, nextRecipeId);
                recipeStmt.setString(2, name);
                recipeStmt.setString(3, category);
                recipeStmt.setInt(4, servings);
                recipeStmt.setString(5, instructions);
                recipeStmt.executeUpdate();
            }

            // Insert each cooking method into Recipe_Cooking_Methods table
            try (PreparedStatement methodStmt = connection.prepareStatement(methodQuery)) {
                for (CookingMethod method : cookingMethods) {
                    methodStmt.setInt(1, nextRecipeId);
                    methodStmt.setString(2, method.getCookMethod());
                    methodStmt.setString(3, method.getCookTemp());
                    methodStmt.setInt(4, method.getMinCookTime());
                    methodStmt.setInt(5, method.getMaxCookTime());
                    System.out.println("Inserting cooking method for recipe ID: " + nextRecipeId + ", Method: " + method.getCookMethod());
                    methodStmt.addBatch(); // Batch insert for efficiency
                }
                methodStmt.executeBatch();
            }

            connection.commit(); // Commit transaction
            System.out.println("Recipe added successfully with cooking methods.");

        } catch (SQLException e) {
            System.err.println("Error adding recipe: " + e.getMessage());
        }
    }

    // Add an ingredient to a recipe
    public static void addRecipeIngredient(int recipeId, int ingredientId, int quantity, String unit) {
        String query = "INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, recipeId);
            preparedStatement.setInt(2, ingredientId);
            preparedStatement.setInt(3, quantity);
            preparedStatement.setString(4, unit);
            preparedStatement.executeUpdate();
            System.out.println("Recipe ingredient added successfully.");

        } catch (SQLException e) {
            System.err.println("Error adding recipe ingredient: " + e.getMessage());
        }
    }

    // Update an existing user's name
    public static void updateUser(int userId, String newFirstName, String newLastName, String newUsername, String newPassword) {
        String query = "UPDATE users SET first_name = ?, last_name = ?, username = ?, password = ? WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newFirstName);
            preparedStatement.setString(2, newLastName);
            preparedStatement.setString(3, newUsername);
            preparedStatement.setString(4, newPassword);
            preparedStatement.setInt(5, userId);
            preparedStatement.executeUpdate();
            System.out.println("Ingredient updated successfully.");

        } catch (SQLException e) {
            System.err.println("Error updating ingredient: " + e.getMessage());
        }
    }

    // Update an existing ingredient's name
    public static void updateIngredient(int ingredientId, String newName) {
        String query = "UPDATE ingredients SET name = ? WHERE ingredient_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, ingredientId);
            preparedStatement.executeUpdate();
            System.out.println("Ingredient updated successfully.");

        } catch (SQLException e) {
            System.err.println("Error updating ingredient: " + e.getMessage());
        }
    }

    // Update an existing recipe with category, servings, and multiple cooking methods
    public static void updateRecipe(int recipeId, String newName, String newCategory, int newServings, List<CookingMethod> newCookingMethods, String newInstructions) {
        String recipeQuery = "UPDATE Recipe SET name = ?, category = ?, servings = ?, instructions = ? WHERE recipe_id = ?";
        String deleteMethodsQuery = "DELETE FROM Recipe_Cooking_Methods WHERE recipe_id = ?";
        String insertMethodQuery = "INSERT INTO Recipe_Cooking_Methods (recipe_id, cook_method, cook_temp, minimum_cook_time, maximum_cook_time) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            // Update the Recipe table
            try (PreparedStatement recipeStmt = connection.prepareStatement(recipeQuery)) {
                recipeStmt.setString(1, newName);
                recipeStmt.setString(2, newCategory);
                recipeStmt.setInt(3, newServings);
                recipeStmt.setString(4, newInstructions);
                recipeStmt.setInt(5, recipeId);
                recipeStmt.executeUpdate();
            }

            // Remove existing cooking methods for this recipe
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteMethodsQuery)) {
                deleteStmt.setInt(1, recipeId);
                deleteStmt.executeUpdate();
            }

            // Insert new cooking methods
            try (PreparedStatement methodStmt = connection.prepareStatement(insertMethodQuery)) {
                for (CookingMethod method : newCookingMethods) {
                    methodStmt.setInt(1, recipeId);
                    methodStmt.setString(2, method.getCookMethod());
                    methodStmt.setString(3, method.getCookTemp());
                    methodStmt.setInt(4, method.getMinCookTime());
                    methodStmt.setInt(5, method.getMaxCookTime());
                    methodStmt.addBatch();
                }
                methodStmt.executeBatch();
            }

            connection.commit(); // Commit transaction
            System.out.println("Recipe updated successfully with new cooking methods.");

        } catch (SQLException e) {
            System.err.println("Error updating recipe: " + e.getMessage());
        }
    }

    public static void updateUserIngredients(int userId, List<Ingredient> updatedIngredients) {
        String deleteQuery = "DELETE FROM user_ingredients WHERE user_id = ?";
        String insertQuery = "INSERT INTO user_ingredients (user_id, ingredient_id, quantity) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            // Delete existing user ingredients
            deleteStatement.setInt(1, userId);
            deleteStatement.executeUpdate();

            // Insert new user ingredients
            for (Ingredient ingredient : updatedIngredients) {
                insertStatement.setInt(1, userId);
                insertStatement.setInt(2, ingredient.getId());
                insertStatement.setInt(3, ingredient.getQuantity());
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();

            System.out.println("User ingredients updated successfully.");

        } catch (SQLException e) {
            System.err.println("Error updating user ingredients: " + e.getMessage());
        }
    }


    // Delete a user
    public static void deleteUser(int userId) {
        String query = "DELETE FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
            System.out.println("User deleted successfully.");

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    // Delete an ingredient
    public static void deleteIngredient(int ingredientId) {
        String query = "DELETE FROM ingredients WHERE ingredient_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, ingredientId);
            preparedStatement.executeUpdate();
            System.out.println("Ingredient deleted successfully.");

        } catch (SQLException e) {
            System.err.println("Error deleting ingredient: " + e.getMessage());
        }
    }

    // Delete a recipe
    public static void deleteRecipe(int recipeId) {
        String query = "DELETE FROM recipe WHERE recipe_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, recipeId);
            preparedStatement.executeUpdate();
            System.out.println("Recipe deleted successfully.");

        } catch (SQLException e) {
            System.err.println("Error deleting recipe: " + e.getMessage());
        }
    }
}
