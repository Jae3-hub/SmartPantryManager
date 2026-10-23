package com.example.mypantry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity{
    public static final String EXTRA_PANTRY_ID = "pantry_item_id";
    private EditText editName, editQty, editUnit, editExpiry;
    private Button btnSave;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_ingredients);

        //database helper instance
        databaseHelper = new DatabaseHelper(this);

        //get XML elements
        editName = findViewById(R.id.editIngredientName);
        editQty = findViewById(R.id.editIngredientQuantity);
        editUnit = findViewById(R.id.editIngredientUnit);
        editExpiry = findViewById(R.id.editIngredientExpiry);
        btnSave = findViewById(R.id.btnSaveIngredient);

        //clicking the button saves the ingredient changes to the database
        btnSave.setOnClickListener(v -> saveIngredientToDatabase());
    }

    //helper function to save changes
    private void saveIngredientToDatabase() {
        String name = editName.getText().toString().trim();
        String qtyString = editQty.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();

        //input validation to match database
        if (name.isEmpty()) {
            editName.setError("Ingredient name is required");
            editName.requestFocus();
            return;
        }

        if (qtyString.isEmpty()) {
            editQty.setError("Quantity is required");
            editQty.requestFocus();
            return;
        }

        if (unit.isEmpty()) {
            editUnit.setError("Unit is required (e.g., pieces, g)");
            editUnit.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyString);
            if (quantity <= 0) {
                editQty.setError("Quantity must be greater than zero");
                editQty.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            editQty.setError("Enter a valid numeric quantity");
            editQty.requestFocus();
            return;
        }

        //save to database using our database helper
        boolean isInserted = databaseHelper.addPantryItem(name, quantity, unit, expiry);

        if (isInserted) {
            Toast.makeText(this, "Ingredient added successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity and return to MainActivity
        } else {
            Toast.makeText(this, "Error saving ingredient to database", Toast.LENGTH_SHORT).show();
        }
    }
}
