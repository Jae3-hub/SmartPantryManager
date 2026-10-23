package com.example.mypantry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_NAME = "recipe_name";
    public static final String EXTRA_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String EXTRA_RECIPE_STEPS = "recipe_steps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView textName = findViewById(R.id.textDetailRecipeName);
        TextView textIngredients = findViewById(R.id.textDetailIngredients);
        TextView textSteps = findViewById(R.id.textDetailSteps);
        Button btnBack = findViewById(R.id.btnBackToRecipes);

        // Receive data passed via Intent from RecipeAdapter
        String name = getIntent().getStringExtra(EXTRA_RECIPE_NAME);
        String ingredients = getIntent().getStringExtra(EXTRA_RECIPE_INGREDIENTS);
        String steps = getIntent().getStringExtra(EXTRA_RECIPE_STEPS);

        if (name != null) textName.setText(name);
        if (ingredients != null) textIngredients.setText("• " + ingredients.replace(",", "\n• "));
        if (steps != null) textSteps.setText(steps);

        btnBack.setOnClickListener(v -> finish());
    }
}
