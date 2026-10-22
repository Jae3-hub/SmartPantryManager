package com.example.mypantry;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private Context context;
    private Cursor cursor; //holds database query results
    private OnItemClickListener listener; //pass delete actions back to the main activity

    //interface handles click events on individual list items
    public interface OnItemClickListener {
        void onDeleteClick(int id);
    }

    //constructor initialises the context, cursor and listener
    public PantryAdapter(Context context, Cursor cursor, OnItemClickListener listener) {
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflates the individual row layout from pantry_item.xml into the view holder
        View view = LayoutInflater.from(context).inflate(R.layout.pantry_item, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        //moves the cursor to exact row position matching the recycler view item
        if (!cursor.moveToPosition(position)) {
            return;
        }

        //extract column values from the database row
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_NAME));
        double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_QTY));
        String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_UNIT));
        String expiry = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_EXPIRY));

        //set the data inside pantry_item.xml
        holder.textName.setText(name);
        holder.textDetails.setText("Quantity: " + quantity + " " + unit + (expiry != null && !expiry.isEmpty() ? " | Exp: " + expiry : ""));

        //handle deletions
        //passes the item's database id to the listener
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(id);
            }
        });
    }

    @Override
    public int getItemCount() {
        //returns total number of rows available in the cursor
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        //refresh the adapter data when items are added, updated or deleted
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    //the view holder class caches references to individual views inside pantry_item.xml
    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textDetails;
        Button btnDelete;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textIngredientName);
            textDetails = itemView.findViewById(R.id.textIngredientDetails);
            btnDelete = itemView.findViewById(R.id.btnDeletePantry);
        }
    }
}
