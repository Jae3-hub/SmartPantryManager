package com.example.mypantry;

//imports
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewPantry;
    private PantryAdapter pantryAdapter;
    private Button btnViewRecipes;
    private FloatingActionButton fabAddIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //connect this java code to activity_main.xml

        //database helper instance
        databaseHelper = new DatabaseHelper(this);

        //get XML views
        recyclerViewPantry = findViewById(R.id.recyclerViewPantry);
        btnViewRecipes = findViewById(R.id.btnViewRecipes);
        fabAddIngredient = findViewById(R.id.fabAddIngredient);

        //recycler view requires a layout manager to render items in a vertical list
        recyclerViewPantry.setLayoutManager(new LinearLayoutManager(this));

        //load pantry items from the database into the adapter
        loadPantryData();

        //floating button to add new ingredient (opens ingredient screen)
        fabAddIngredient.setOnClickListener(v -> {
            // We will create AddEditIngredientActivity next
            Toast.makeText(this, "Add Ingredient screen coming up next!", Toast.LENGTH_SHORT).show();
        });

        //button to view suggested recipes (opens suggested recipes screen)
        btnViewRecipes.setOnClickListener(v -> {
            // We will create SuggestedRecipesActivity next
            Toast.makeText(this, "Suggested Recipes screen coming up next!", Toast.LENGTH_SHORT).show();
        });
    }

    //helper method to fetch cursor data and attach or update the adapter
    private void loadPantryData() {
        Cursor cursor = databaseHelper.getAllPantryItems();
        if (pantryAdapter == null) {
            //instantiate the adapter and define the delete logic
            pantryAdapter = new PantryAdapter(this, cursor, id -> {
                //delete item
                boolean success = databaseHelper.deletePantryItem(id);
                if (success) {
                    Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                    loadPantryData(); // Refresh list
                } else {
                    Toast.makeText(MainActivity.this, "Error deleting item", Toast.LENGTH_SHORT).show();
                }
            });
            recyclerViewPantry.setAdapter(pantryAdapter);
        } else {
            //swapping the cursor if it already exists avoids memory leaks
            pantryAdapter.swapCursor(cursor);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryData(); //this ensures the list updates automatically when returning to MainActivity.java
    }
}