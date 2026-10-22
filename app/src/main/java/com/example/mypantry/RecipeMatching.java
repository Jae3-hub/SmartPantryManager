package com.example.mypantry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeMatching {

    //strict-matching where a recipe is only suggested if all ingredients are present in the required quantity
    public static List<RecipeModel> getStrictlyMatchedRecipes(List<RecipeModel> allRecipes, List<PantryItemModel> pantryItems) {
        List<RecipeModel> suggestedRecipes = new ArrayList<>();

        //step 1: map the user's pantry items into a normalized lookup table (key: ingredient name & value: available quantity)
        Map<String, Double> pantryMap = new HashMap<>();

        for (PantryItemModel item : pantryItems) {
            String normalisedName = normaliseIngredientName(item.getName());
            double existingQty = pantryMap.getOrDefault(normalisedName, 0.0);
            pantryMap.put(normalisedName, existingQty + item.getQuantity());
        }

        //step 2: iterate through every recipe in the database
        for (RecipeModel recipe : allRecipes) {
            boolean isEligible = true; //assume that a recipe can be made until proven otherwise

            //parse through recipe ingredients string (e.g., "pasta:200:g)
            String rawIngredients = recipe.getIngredientsFormatted();
            String[] ingredientEntries = rawIngredients.split(",");

            for (String entry : ingredientEntries) {
                String[] parts = entry.trim().split(":");
                if (parts.length < 3) continue; //skip malformed entries safely

                String reqName = normaliseIngredientName(parts[0]);
                double reqQty;
                try {
                    reqQty = Double.parseDouble(parts[1].trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                //check for rule 1: does the pantry have this required ingredient?
                if (!pantryMap.containsKey(reqName)) {
                    isEligible = false; //missing ingredients are excluded
                    break;
                }

                //check for rule 2: is the pantry quantity enough?
                double availableQty = pantryMap.get(reqName);
                if (availableQty < reqQty) {
                    isEligible = false; //insufficient quantities are excluded
                    break;
                }
            }

            //step 3: if all ingredients pass, add to suggested recipe list
            if (isEligible) {
                suggestedRecipes.add(recipe);
            }
        }

        return suggestedRecipes;
    }

    //the following normalises the ingredient names to handle real world issues (plural, case miss-match and spacing)
    //e.g., "Tomatoes" -> "tomato", "eggs" -> "egg".
    public static String normaliseIngredientName(String name) {
        if (name == null) return "";
        String normalised = name.toLowerCase().trim();

        //basic plural stripping rule
        if (normalised.endsWith("es") && normalised.length() > 3) {
            normalised = normalised.substring(0, normalised.length() - 2);
        } else if (normalised.endsWith("s") && normalised.length() > 2 && !normalised.endsWith("ss")) {
            normalised = normalised.substring(0, normalised.length() - 1);
        }

        return normalised;
    }
}