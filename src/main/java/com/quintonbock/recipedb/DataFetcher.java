package com.yourname.recipedb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yourname.recipedb.model.CookingMethod;
import com.yourname.recipedb.model.Ingredient;
import com.yourname.recipedb.model.Recipe;
import com.yourname.recipedb.model.RecipeIngredient;
import com.yourname.recipedb.model.User;


public class DataFetcher {

    // Retrieve all users from the database
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, first_name, last_name, username, password FROM users";
    
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
    
            while (resultSet.next()) {
                users.add(new User(
                    resultSet.getInt("user_id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    new ArrayList<>()
                ));
            }
    
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }

    // Retrieve all ingredients from the database
    public static List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        String query = "SELECT ingredient_id, name FROM ingredients";

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("ingredient_id");
                String name = resultSet.getString("name");
                ingredients.add(new Ingredient(id, name, 0, "")); // Quantity and unit are set later
            }

        } catch (SQLException e) {
            System.err.println("Error fetching ingredients: " + e.getMessage());
        }

        return ingredients;
    }

    // Retrieve all ingredients from the database
    public static List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        Map<Integer, Recipe> recipeMap = new HashMap<>();
    
        String query = "SELECT r.RECIPE_ID AS RECIPE_ID, r.NAME AS NAME, r.CATEGORY AS CATEGORY, " +
                       "r.SERVINGS AS SERVINGS, r.INSTRUCTIONS AS INSTRUCTIONS, " +
                       "m.ID AS ID, m.COOK_METHOD AS COOK_METHOD, " +
                       "m.COOK_TEMP AS COOK_TEMP, m.MINIMUM_COOK_TIME AS MINIMUM_COOK_TIME, " +
                       "m.MAXIMUM_COOK_TIME AS MAXIMUM_COOK_TIME " +
                       "FROM RECIPE r " +
                       "LEFT JOIN RECIPE_COOKING_METHODS m ON r.RECIPE_ID = m.RECIPE_ID " +
                       "ORDER BY r.RECIPE_ID";
    
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
    
            System.out.println("Executing query...");
    
            while (rs.next()) {
                int recipeId = rs.getInt("RECIPE_ID"); // Ensure correct case
                String name = rs.getString("NAME");
                System.out.println("Retrieved recipe: " + recipeId + ", " + name);
    
                Recipe recipe = recipeMap.get(recipeId);
                if (recipe == null) {
                    recipe = new Recipe(
                        recipeId,
                        name,
                        rs.getString("CATEGORY"),
                        rs.getInt("SERVINGS"),
                        rs.getString("INSTRUCTIONS"),
                        new ArrayList<>()
                    );
                    recipeMap.put(recipeId, recipe);
                    recipes.add(recipe);
                }
    
                // Add cooking method to the recipe
                if (rs.getString("COOK_METHOD") != null) {
                    CookingMethod method = new CookingMethod(
                        rs.getInt("ID"),
                        recipeId,
                        rs.getString("COOK_METHOD"),
                        rs.getString("COOK_TEMP"),
                        rs.getInt("MINIMUM_COOK_TIME"),
                        rs.getInt("MAXIMUM_COOK_TIME")
                    );
                    recipe.getCookingMethods().add(method);
                }
            }
    
        } catch (SQLException e) {
            System.err.println("Error fetching recipes: " + e.getMessage());
        }
    
        System.out.println("Recipes retrieved: " + recipes.size());
        return recipes;
    }      

    public static List<CookingMethod> getCookingMethodsForRecipe(int recipeId) {
        List<CookingMethod> cookingMethods = new ArrayList<>();
        String query = "SELECT ID, COOK_METHOD, COOK_TEMP, MINIMUM_COOK_TIME, MAXIMUM_COOK_TIME FROM RECIPE_COOKING_METHODS WHERE RECIPE_ID = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, recipeId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                cookingMethods.add(new CookingMethod(
                    rs.getInt("ID"),
                    recipeId,
                    rs.getString("COOK_METHOD"),
                    rs.getString("COOK_TEMP"),
                    rs.getInt("MINIMUM_COOK_TIME"),
                    rs.getInt("MAXIMUM_COOK_TIME")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cooking methods: " + e.getMessage());
        }
        return cookingMethods;
    }  

    // Retrieve all ingredients for a specific user
    public static List<Ingredient> getUserIngredients(int userId) {
        List<Ingredient> ingredients = new ArrayList<>();
        String query = "SELECT i.ingredient_id, i.name, ui.quantity " +
                       "FROM user_ingredients ui " +
                       "JOIN ingredients i ON ui.ingredient_id = i.ingredient_id " +
                       "WHERE ui.user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
             
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int ingredientId = resultSet.getInt("ingredient_id");
                String name = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");

                ingredients.add(new Ingredient(ingredientId, name, quantity, ""));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user ingredients: " + e.getMessage());
        }

        return ingredients;
    }

    // Retrieve all ingredients for a specific recipe
    public static List<RecipeIngredient> getRecipeIngredients(int recipeId) {
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        String query = "SELECT ingredient_id, quantity, unit FROM recipe_ingredients WHERE recipe_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, recipeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int ingredientId = resultSet.getInt("ingredient_id");
                int quantity = resultSet.getInt("quantity");
                String unit = resultSet.getString("unit");

                recipeIngredients.add(new RecipeIngredient(recipeId, ingredientId, quantity, unit));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching recipe ingredients: " + e.getMessage());
        }

        return recipeIngredients;
    }

    // Retrieve a single user by username
    public static User getUserByName(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String password = resultSet.getString("password");
                List<Ingredient> ingredientInventory = getUserIngredients(userId);

                return new User(userId, firstName, lastName, username, password, ingredientInventory);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }
        return null;
    }

    // Retrieve a single recipe by name
    public static Recipe getRecipeByName(String name) {
        String query = "SELECT r.recipe_id, r.name, r.category, r.servings, r.instructions," + 
                        "m.cook_method, m.cook_temp, m.minimum_cook_time, m.maximum_cook_time" +
                        "FROM Recipe r" +
                        "LEFT JOIN Recipe_Cooking_Methods m ON r.recipe_id = m.recipe_id" +
                        "WHERE name = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("recipe_id");
                String category = resultSet.getString("category");
                int servings = resultSet.getInt("servings");
                String instructions = resultSet.getString("instructions");
                List<CookingMethod> cookingMethods = new ArrayList<>();
                cookingMethods.add(new CookingMethod(
                    resultSet.getInt("id"),
                    resultSet.getInt("recipe_id"),
                    resultSet.getString("cook_method"),
                    resultSet.getString("cook_temp"),
                    resultSet.getInt("minimum_cook_time"),
                    resultSet.getInt("maximum_cook_time")
                ));
                

                return new Recipe(id, name, category, servings, instructions, cookingMethods);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching recipe: " + e.getMessage());
        }
        return null;
    }

    // Retrieve the next available ingredient ID
    public static int getNextIngredientId() {
        String query = "SELECT NVL(MAX(ingredient_id), 0) + 1 AS next_id FROM ingredients";
    
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
    
            if (resultSet.next()) {
                return resultSet.getInt("next_id");  // Ensure it's an integer
            }
    
        } catch (SQLException e) {
            System.err.println("Error fetching next ingredient ID: " + e.getMessage());
        }
        return -1;
    }
    
    

    // Retrieve the next available recipe ID using the sequence
    public static int getNextRecipeId() {
        String query = "SELECT recipe_id_seq.NEXTVAL AS next_id FROM dual";
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
    
            if (resultSet.next()) {
                return resultSet.getInt("next_id");
            }
    
        } catch (SQLException e) {
            System.err.println("Error fetching next recipe ID: " + e.getMessage());
        }
        return -1;
    }    
 

    // Retrieve the next available recipe ID
    public static int getNextRecipeMethodId() {
        String query = "SELECT NVL(MAX(id), 0) + 1 AS next_id FROM recipe_cooking_methods";
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                return resultSet.getInt("next_id");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching next method ID: " + e.getMessage());
        }
        return -1;
    }

    // Retrieve the next available recipe ID
    public static int getNextUserId() {
        String query = "SELECT NVL(MAX(user_id), 0) + 1 AS next_id FROM users";
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                return resultSet.getInt("next_id");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching next user ID: " + e.getMessage());
        }
        return -1;
    }

    public static boolean isUsernameTaken(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        return false;
    }
    
}
