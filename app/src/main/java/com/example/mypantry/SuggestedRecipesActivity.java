package com.example.mypantry;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

//this file controls the screen that evaluates the user's pantry against
//all the database recipes using strict-matching

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewSuggested;
    private TextView textEmptySuggestions; //this friendly message is shown when zero recipes match
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggested_recipes);

        //database helper to query pantry items and recipes
        databaseHelper = new DatabaseHelper(this);

        //find XML elements
        recyclerViewSuggested = findViewById(R.id.recyclerViewSuggested);
        textEmptySuggestions = findViewById(R.id.textEmptySuggestions);

        //set the layout manager so the recycler view arranges items vertically
        recyclerViewSuggested.setLayoutManager(new LinearLayoutManager(this));

        //load the suggested recipes only
        loadSuggestedRecipes();

        //button to return to the pantry items page
        Button btnBackToPantry = findViewById(R.id.btnBackToPantry);

        btnBackToPantry.setOnClickListener(v -> finish());
    }

    //fetches data, runs the matching algorithm and updates the UI
    private void loadSuggestedRecipes() {
        // step 1: load all the pantry items from the database
        List<PantryItemModel> pantryItems = new ArrayList<>();
        Cursor pantryCursor = databaseHelper.getAllPantryItems();

        if (pantryCursor != null) {
            while (pantryCursor.moveToNext()) {
                //get the ingredient properties from the database columns
                String name = pantryCursor.getString(pantryCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_NAME));
                double qty = pantryCursor.getDouble(pantryCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_QTY));
                String unit = pantryCursor.getString(pantryCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_UNIT));

                //add these to a list of pantry items
                pantryItems.add(new PantryItemModel(name, qty, unit));
            }
            pantryCursor.close(); //closing the cursor prevents memory leaks
        }

        //step 2: load all pre-loaded recipes from the database
        List<RecipeModel> allRecipes = new ArrayList<>();
        Cursor recipeCursor = databaseHelper.getAllRecipes();

        if (recipeCursor != null) {
            while (recipeCursor.moveToNext()) {
                //get recipe data
                int id = recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_ID));
                String name = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_NAME));
                String ingredients = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_INGREDIENTS));
                String steps = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_STEPS));

                //add these to a list of recipe items
                allRecipes.add(new RecipeModel(id, name, ingredients, steps));
            }
            recipeCursor.close(); //always close the cursor
        }

        //step 3: run the matching algorithm which passes all recipes and pantry items
        List<RecipeModel> matchedRecipes = RecipeMatching.getStrictlyMatchedRecipes(allRecipes, pantryItems);

        //step 4: handle UI display (either a populated list or an empty state)
        if (matchedRecipes.isEmpty()) {
            //if zero recipes match, hide the list and show the message
            recyclerViewSuggested.setVisibility(View.GONE);
            textEmptySuggestions.setVisibility(View.VISIBLE);
        } else {
            //if matching recipes succeeds, show the list and hide the empty state message
            recyclerViewSuggested.setVisibility(View.VISIBLE);
            textEmptySuggestions.setVisibility(View.GONE);

            //initialize the adapter if it's null, or update the existing adapter with fresh matches
            if (recipeAdapter == null) {
                recipeAdapter = new RecipeAdapter(this, matchedRecipes);
                recyclerViewSuggested.setAdapter(recipeAdapter);
            } else {
                recipeAdapter.updateList(matchedRecipes);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onResume() ensures that whenever the user adds or deletes an ingredient in MainActivity
        // and navigates back here, the suggestions recalculate and persist instantly!
        loadSuggestedRecipes();
    }
}
