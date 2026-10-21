package com.example.mypantry;

import android.content.Context;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //database name and initial version
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    //table Names (for the pantry items and recipes)
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";

    //columns for the pantry table
    public static final String COLUMN_PANTRY_ID = "id";
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QTY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";
    public static final String COLUMN_PANTRY_EXPIRY = "expiry_date";

    //columns for the recipe table
    public static final String COLUMN_RECIPE_ID = "id";
    public static final String COLUMN_RECIPE_NAME = "name";
    public static final String COLUMN_RECIPE_INGREDIENTS = "ingredients";  //for strict matching
    public static final String COLUMN_RECIPE_STEPS = "steps";

    //constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create the pantry table
        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COLUMN_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PANTRY_NAME + " TEXT NOT NULL, " +
                COLUMN_PANTRY_QTY + " REAL NOT NULL, " +
                COLUMN_PANTRY_UNIT + " TEXT NOT NULL, " +
                COLUMN_PANTRY_EXPIRY + " TEXT)";
        db.execSQL(createPantryTable);

        //create the recipes table
        String createRecipeTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                COLUMN_RECIPE_INGREDIENTS + " TEXT NOT NULL, " +
                COLUMN_RECIPE_STEPS + " TEXT NOT NULL)";
        db.execSQL(createRecipeTable);

        //seed the initial recipes upon first creation
        seedInitialRecipes(db);
    }

    //onUpgrade method
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    //CRUD operations (PANTRY ITEMS)

    //create
    public boolean addPantryItem(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        //content values stores column/value pairs before insertion
        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, name);
        values.put(COLUMN_PANTRY_QTY, quantity);
        values.put(COLUMN_PANTRY_UNIT, unit);
        values.put(COLUMN_PANTRY_EXPIRY, expiryDate);

        long result = db.insert(TABLE_PANTRY, null, values);
        return result != -1; // will return true if successful
    }

    //read
    public Cursor getAllPantryItems() {
        // a cursor holds the results of a query and lets the application move through the returned rows one at a time
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PANTRY, null);
    }

    //update
    public boolean updatePantryItem(int id, String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, name);
        values.put(COLUMN_PANTRY_QTY, quantity);
        values.put(COLUMN_PANTRY_UNIT, unit);
        values.put(COLUMN_PANTRY_EXPIRY, expiryDate);

        int rowsAffected = db.update(TABLE_PANTRY, values, COLUMN_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }

    //delete
    public boolean deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_PANTRY, COLUMN_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }

    //seeding recipes
    private void seedInitialRecipes(SQLiteDatabase db) {
        //pasta and rice dishes (proper meals)
        insertRecipe(db, "Simple Tomato Pasta", "pasta:200:g, tomato:3:pieces, garlic:2:cloves, olive oil:1:tbsp",
                "1. Bring a pot of salted water to a boil and cook pasta until al dente.\n2. In a skillet, heat olive oil and sauté minced garlic for 1 minute.\n3. Add diced tomatoes, simmer for 8 minutes until soft.\n4. Toss cooked pasta into the sauce and stir well before serving.");

        insertRecipe(db, "Garlic Butter Rice", "rice:150:g, butter:1:tbsp, garlic:1:cloves, salt:1:tsp",
                "1. Wash rice thoroughly and cook in a pot or rice cooker until fluffy.\n2. Melt butter in a pan over medium heat and stir in minced garlic until fragrant.\n3. Gently fold the garlic butter and salt into the cooked warm rice.");

        insertRecipe(db, "Fried Egg Rice", "rice:200:g, egg:2:pieces, soy sauce:1:tbsp, vegetable oil:1:tbsp",
                "1. Heat oil in a wok or large pan, scramble the eggs gently, and set aside.\n2. Add cooked rice to the pan, stirring continuously over high heat.\n3. Drizzle soy sauce evenly, toss in the scrambled eggs, and mix thoroughly.");

        //breakfast/light meals
        insertRecipe(db, "Scrambled Eggs on Toast", "bread:2:slices, egg:3:pieces, butter:1:tbsp, salt:1:tsp",
                "1. Whisk eggs with salt in a bowl.\n2. Melt butter in a non-stick pan on low heat and pour in the eggs.\n3. Gently fold until soft curds form.\n4. Serve over warm toasted bread.");

        insertRecipe(db, "Classic Omelette", "egg:2:pieces, cheese:30:g, milk:20:ml, butter:1:tbsp",
                "1. Whisk eggs, milk, and a pinch of salt until smooth.\n2. Melt butter in a skillet, pour in egg mixture, and cook undisturbed until edges set.\n3. Sprinkle cheese on one half, fold the omelette over, and cook until cheese melts.");

        insertRecipe(db, "Cheese and Tomato Toasted Sandwich", "bread:2:slices, cheese:50:g, tomato:1:pieces, butter:1:tbsp",
                "1. Butter the outer sides of both bread slices.\n2. Layer thin tomato slices and cheese between the unbuttered sides.\n3. Grill in a skillet on medium heat for 3-4 minutes per side until golden and cheese is melted.");

        insertRecipe(db, "Basic Sugar Pancakes", "flour:150:g, milk:200:ml, egg:1:piece, sugar:2:tbsp",
                "1. Sift flour into a bowl, whisk in milk, egg, and sugar until a smooth batter forms.\n2. Heat a lightly greased skillet on medium heat.\n3. Pour batter, cook until bubbles pop on surface, flip and cook until golden brown.");

        //fruit and sweet dishes
        insertRecipe(db, "Fresh Fruit Salad", "apple:1:pieces, banana:1:pieces, orange:1:pieces, honey:1:tbsp",
                "1. Wash, peel, and core apples and oranges, then cut into bite-sized chunks.\n2. Slice the banana into uniform rounds.\n3. Combine all fruit in a large mixing bowl and drizzle with honey before serving.");

        insertRecipe(db, "Simple Oatmeal", "oats:100:g, milk:250:ml, sugar:1:tbsp",
                "1. Combine oats and milk in a small saucepan.\n2. Bring to a gentle boil, then lower heat and simmer for 5 minutes, stirring frequently.\n3. Stir in sugar and serve hot.");

        insertRecipe(db, "Milk and Cereal", "cereal:100:g, milk:200:ml",
                "1. Pour cereal into a clean bowl.\n2. Pour chilled milk over top and serve immediately.");

        //pizza and mains
        insertRecipe(db, "Simple Margherita Pizza", "flour:200:g, yeast:1:tsp, tomato:2:pieces, cheese:100:g, olive oil:1:tbsp",
                "1. Mix flour, yeast, water, and olive oil into a dough; knead and let rise for 20 minutes.\n2. Roll dough flat on a baking tray.\n3. Spread crushed tomatoes evenly over dough and top generously with cheese.\n4. Bake at 220°C (425°F) for 12-15 minutes until crust is golden and cheese bubbles.");

        insertRecipe(db, "Lemon Garlic Chicken", "chicken breast:2:pieces, lemon:1:pieces, garlic:2:cloves, olive oil:1:tbsp",
                "1. Pat chicken breasts dry and marinate with lemon juice, minced garlic, and olive oil for 10 minutes.\n2. Heat skillet over medium-high heat.\n3. Sear chicken for 6-8 minutes per side until cooked through and golden.");

        insertRecipe(db, "Tomato Soup", "tomato:5:pieces, onion:1:pieces, garlic:2:cloves, butter:1:tbsp",
                "1. Melt butter in a pot; sauté chopped onion and garlic until translucent.\n2. Add diced tomatoes and 1 cup of water; simmer covered for 15 minutes.\n3. Blend thoroughly until smooth and season with salt.");

        insertRecipe(db, "Boiled Egg Salad", "egg:3:pieces, mayonnaise:1:tbsp, salt:1:tsp, black pepper:1:tsp",
                "1. Boil eggs in water for 10 minutes, cool in ice water, peel, and dice.\n2. Mix diced eggs with mayonnaise, salt, and pepper in a bowl until combined.\n3. Serve chilled as a side or on bread.");

        //snacks and quick sides
        insertRecipe(db, "Garlic Butter Toast", "bread:2:slices, butter:1:tbsp, garlic:1:cloves",
                "1. Finely mince garlic and mix into softened butter.\n2. Spread mixture evenly on bread slices.\n3. Toast in a skillet or oven until golden and crisp.");

        insertRecipe(db, "Cheesy Tomato Toast", "bread:2:slices, tomato:1:pieces, cheese:40:g, olive oil:1:tsp",
                "1. Slice tomatoes thinly and place over bread.\n2. Top with grated cheese and drizzle with olive oil.\n3. Broil or toast until cheese is melted and bubbling.");

        insertRecipe(db, "Sautéed Garlic Spinach", "spinach:200:g, garlic:2:cloves, olive oil:1:tbsp, salt:1:tsp",
                "1. Wash spinach leaves thoroughly and drain excess water.\n2. Heat olive oil in a skillet, sauté minced garlic for 30 seconds.\n3. Add spinach and toss until wilted (about 2-3 minutes). Season with salt.");

        insertRecipe(db, "Pan-Seared Garlic Potatoes", "potato:2:pieces, garlic:2:cloves, butter:1:tbsp, olive oil:1:tbsp",
                "1. Dice potatoes into small cubes and boil for 5 minutes until slightly soft.\n2. Heat butter and olive oil in a skillet on medium-high heat.\n3. Fry potato cubes with minced garlic until golden brown and crispy on all sides.");
    }

    //insert recipes
    private void insertRecipe(SQLiteDatabase db, String name, String ingredients, String steps) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPE_NAME, name);
        values.put(COLUMN_RECIPE_INGREDIENTS, ingredients);
        values.put(COLUMN_RECIPE_STEPS, steps);
        db.insert(TABLE_RECIPES, null, values);
    }

    //read all recipes (for recipe collection view)
    public Cursor getAllRecipes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);
    }
}
