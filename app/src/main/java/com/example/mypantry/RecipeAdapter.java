package com.example.mypantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

//this file acts as a bridge between our list of matched recipe model objects and the
//recycler view on the suggested recipes screen

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<RecipeModel> recipeList; //the list of recipes that match the user's pantry

    public RecipeAdapter(Context context, List<RecipeModel> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the xml file into a view object for each row
        View view = LayoutInflater.from(context).inflate(R.layout.all_recipes, parent, false);
        return new RecipeViewHolder(view);
    }

        //fetches the specific recipe object based on its position in the list
        //set the data inside the row card
        @Override
        public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {

            //fetches the specific recipe object based on its position in the list
            RecipeModel recipe = recipeList.get(position);

            //set the data inside the row card
            holder.textName.setText(recipe.getName());

            //format ingredients so each one appears on a separate line
            String ingredients = recipe.getIngredientsFormatted();

            if (ingredients != null && !ingredients.trim().isEmpty()) {

                //split the ingredients wherever a comma separates them
                String[] ingredientList = ingredients.split("\\s*,\\s*");
                StringBuilder formattedIngredients = new StringBuilder();

                for (String ingredient : ingredientList) {
                    String item = ingredient.trim();

                    if (!item.isEmpty()) {
                        if (formattedIngredients.length() > 0) {
                            formattedIngredients.append("\n");
                        }

                        formattedIngredients.append("• ").append(item);
                    }
                }

                //display the formatted ingredients
                holder.textIngredients.setText(formattedIngredients.toString());

            } else {
                holder.textIngredients.setText("No ingredients listed");
            }
        }

    @Override
    public int getItemCount() {
        //returns the total number of items in the list (if null, returns 0 to prevent a crash)
        return recipeList != null ? recipeList.size() : 0;
    }

    //updates the recycler view data dynamically when a user changes their pantry
    public void updateList(List<RecipeModel> newList) {
        this.recipeList = newList;
        notifyDataSetChanged();
    }

    //this class caches references to the UI components inside all_recipes.xml to ensure smooth scrolling
    //without repeatedly calling findviewbyid
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textIngredients;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRecipeName);
            textIngredients = itemView.findViewById(R.id.textRecipeIngredients);
        }
    }
}
