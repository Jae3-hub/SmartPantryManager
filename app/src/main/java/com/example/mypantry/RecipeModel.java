package com.example.mypantry;

public class RecipeModel {
    private int id;
    private String name;
    private String ingredientsFormatted;
    private String steps;

    public RecipeModel(int id, String name, String ingredientsFormatted, String steps) {
        this.id = id;
        this.name = name;
        this.ingredientsFormatted = ingredientsFormatted;
        this.steps = steps;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getIngredientsFormatted() { return ingredientsFormatted; }
    public String getSteps() { return steps; }
}
