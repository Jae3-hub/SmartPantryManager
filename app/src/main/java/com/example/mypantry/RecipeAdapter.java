package com.example.mypantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<RecipeModel> recipeList;
    private OnRecipeClickListener listener;

    //interface to handle clicks on recipe cards
    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeModel recipe);
    }

    public RecipeAdapter(Context context, List<RecipeModel> recipeList, OnRecipeClickListener listener) {
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_recipes, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeModel recipe = recipeList.get(position);
        holder.textName.setText(recipe.getName());

        String ingredients = recipe.getIngredientsFormatted();
        if (ingredients != null && !ingredients.trim().isEmpty()) {
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
            holder.textIngredients.setText(formattedIngredients.toString());
        } else {
            holder.textIngredients.setText("No ingredients listed");
        }

        // Trigger click listener when user taps the recipe card
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    public void updateList(List<RecipeModel> newList) {
        this.recipeList = newList;
        notifyDataSetChanged();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textIngredients;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRecipeName);
            textIngredients = itemView.findViewById(R.id.textRecipeIngredients);
        }
    }
}