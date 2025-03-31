package com.yourname.recipedb.model;

import java.util.List;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private List<Ingredient> ingredientInventory; // Ingredients owned by the user

    public User(int id, String firstName, String lastName, String username, String password, List<Ingredient> ingredientInventory) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.ingredientInventory = ingredientInventory;
    }

    // Getters
    public int getId() { 
        return id; 
    }

    public String getFirstName() { 
        return firstName; 
    }

    public String getLastName() { 
        return lastName; 
    }

    public String getUsername() { 
        return username; 
    }

    public String getPassword() { 
        return password; 
    }

    public List<Ingredient> getIngredientInventory() {
        return ingredientInventory;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }

    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }

    public void setPassword(String password) { 
        this.password = password; 
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setIngredientInventory(List<Ingredient> ingredientInventory) {
        this.ingredientInventory = ingredientInventory;
    }

    // Add an ingredient to the user's inventory
    public void addIngredient(Ingredient ingredient) {
        this.ingredientInventory.add(ingredient);
    }

    // Remove an ingredient from the user's inventory
    public void removeIngredient(Ingredient ingredient) {
        this.ingredientInventory.remove(ingredient);
    }

    // Override toString for debugging or display purposes
    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", ingredientInventory=" + ingredientInventory +
            '}';
    }
}
