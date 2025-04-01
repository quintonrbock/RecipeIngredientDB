package com.yourname.recipedb.model;

public class CookingMethod {
    private int id;
    private int recipeId;
    private String cookMethod;
    private String cookTemp;
    private int minCookTime;
    private int maxCookTime;

    public CookingMethod(int id, int recipeId, String cookMethod, String cookTemp, int minCookTime, int maxCookTime) {
        this.id = id;
        this.recipeId = recipeId;
        this.cookMethod = cookMethod;
        this.cookTemp = cookTemp;
        this.minCookTime = minCookTime;
        this.maxCookTime = maxCookTime;
    }

    // Getters
    public int getId() { return id; }
    public int getRecipeId() { return recipeId; }
    public String getCookMethod() { return cookMethod; }
    public String getCookTemp() { return cookTemp; }
    public int getMinCookTime() { return minCookTime; }
    public int getMaxCookTime() { return maxCookTime; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }
    public void setCookMethod(String cookMethod) { this.cookMethod = cookMethod; }
    public void setCookTemp(String cookTemp) { this.cookTemp = cookTemp; }
    public void setMinCookTime(int minCookTime) { this.minCookTime = minCookTime; }
    public void setMaxCookTime(int maxCookTime) { this.maxCookTime = maxCookTime; }

    @Override
    public String toString() {
        return "Recipe ID: " + recipeId + " | Method " + id + ": " + cookMethod + " at " + cookTemp + 
               " for " + minCookTime + "-" + maxCookTime + " minutes";
    }
}
