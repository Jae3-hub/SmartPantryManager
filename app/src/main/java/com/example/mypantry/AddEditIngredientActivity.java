package com.example.mypantry;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity {
    // Intent Extra keys for passing data from MainActivity
    public static final String EXTRA_PANTRY_ID = "pantry_item_id";
    public static final String EXTRA_PANTRY_NAME = "pantry_item_name";
    public static final String EXTRA_PANTRY_QTY = "pantry_item_qty";
    public static final String EXTRA_PANTRY_UNIT = "pantry_item_unit";
    public static final String EXTRA_PANTRY_EXPIRY = "pantry_item_expiry";

    private EditText editName, editQty, editUnit, editExpiry;
    private Button btnSave;
    private DatabaseHelper databaseHelper;
    private int pantryItemId = -1; // -1 indicates a new item creation mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_ingredients);

        databaseHelper = new DatabaseHelper(this);

        editName = findViewById(R.id.editIngredientName);
        editQty = findViewById(R.id.editIngredientQuantity);
        editUnit = findViewById(R.id.editIngredientUnit);
        editExpiry = findViewById(R.id.editIngredientExpiry);
        btnSave = findViewById(R.id.btnSaveIngredient);

        //check if an existing item was passed for editing
        if (getIntent().hasExtra(EXTRA_PANTRY_ID)) {
            pantryItemId = getIntent().getIntExtra(EXTRA_PANTRY_ID, -1);
            editName.setText(getIntent().getStringExtra(EXTRA_PANTRY_NAME));
            editQty.setText(String.valueOf(getIntent().getDoubleExtra(EXTRA_PANTRY_QTY, 0.0)));
            editUnit.setText(getIntent().getStringExtra(EXTRA_PANTRY_UNIT));
            editExpiry.setText(getIntent().getStringExtra(EXTRA_PANTRY_EXPIRY));
            setTitle("Edit Pantry Ingredient");
            btnSave.setText("Update Ingredient");
        } else {
            setTitle("Add Pantry Ingredient");
            btnSave.setText("Save Ingredient");
        }

        btnSave.setOnClickListener(v -> saveIngredientToDatabase());
    }

    private void saveIngredientToDatabase() {
        String name = editName.getText().toString().trim();
        String qtyString = editQty.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();

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

        boolean success;
        if (pantryItemId == -1) {
            //create a new item
            success = databaseHelper.addPantryItem(name, quantity, unit, expiry);
            if (success) {
                Toast.makeText(this, "Ingredient added successfully!", Toast.LENGTH_SHORT).show();
            }
        } else {
            //update the existing item
            success = databaseHelper.updatePantryItem(pantryItemId, name, quantity, unit, expiry);
            if (success) {
                Toast.makeText(this, "Ingredient updated successfully!", Toast.LENGTH_SHORT).show();
            }
        }

        if (success) {
            finish(); //return to MainActivity
        } else {
            Toast.makeText(this, "Error saving ingredient to database", Toast.LENGTH_SHORT).show();
        }
    }
}