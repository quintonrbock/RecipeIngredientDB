package com.yourname.recipedb.model;

import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private String category;
    private int servings;
    private String instructions;
    private List<CookingMethod> cookingMethods;

    public Recipe(int id, String name, String category, int servings, String instructions, List<CookingMethod> cookingMethods) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.servings = servings;
        this.instructions = instructions;
        this.cookingMethods = cookingMethods;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() { 
        return category; 
    }

    public int getServings() { 
        return servings; 
    }

    public String getInstructions() {
        return instructions;
    }

    public List<CookingMethod> getCookingMethods() { 
        return cookingMethods; 
    }

    public int getRecipeIdForCookingMethod() {
        return this.id;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) { 
        this.category = category; 
    }

    public void setServings(int servings) { 
        this.servings = servings; 
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setCookingMethods(List<CookingMethod> cookingMethods) { 
        this.cookingMethods = cookingMethods; 
    }

    public void addCookingMethod(CookingMethod method) {
        this.cookingMethods.add(method);
    }

    // Override toString for debugging or display purposes
    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", servings=" + servings +
                ", instructions='" + instructions + '\'' +
                ", cookingMethods=" + cookingMethods +
                '}';
    }
}
