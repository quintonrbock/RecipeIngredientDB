package com.yourname.recipedb.model;

public class RecipeIngredient {
    private int recipeId;
    private int ingredientId;
    private int quantity;
    private String unit;

    public RecipeIngredient(int recipeId, int ingredientId, int quantity, String unit) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getters
    public int getRecipeId() {
        return recipeId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    // Setters
    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    // Override toString for debugging or display purposes
    @Override
    public String toString() {
        return "RecipeIngredient{" +
                "recipeId=" + recipeId +
                ", ingredientId=" + ingredientId +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                '}';
    }
}
