package com.example.mypantry;

public class PantryItemModel {
    private String name;
    private double quantity;
    private String unit;

    public PantryItemModel(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
}
