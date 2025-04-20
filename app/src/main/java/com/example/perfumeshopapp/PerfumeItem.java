package com.example.perfumeshopapp;

public class PerfumeItem {
    private String name;
    private String brand;
    private double price;
    private int imageResId;
    private int inStock;   // الكمية في المخزن
    private int quantity;  // الكمية اللي اختارها الزبون
    private String gender;
    private String type;
    private boolean isLongLasting;
    private boolean isStrongScent;

    public PerfumeItem() {}

    public PerfumeItem(String name, String brand, double price, int imageResId, int inStock,
                       String gender, String type, boolean isLongLasting, boolean isStrongScent) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.imageResId = imageResId;
        this.inStock = inStock;
        this.quantity = 1; // القيمة الابتدائية عند إضافة المنتج للسلة
        this.gender = gender;
        this.type = type;
        this.isLongLasting = isLongLasting;
        this.isStrongScent = isStrongScent;
    }

    // Getters
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public int getInStock() { return inStock; } // المخزون
    public int getQuantity() { return quantity; } // السلة
    public String getGender() { return gender; }
    public String getType() { return type; }
    public boolean isLongLasting() { return isLongLasting; }
    public boolean isStrongScent() { return isStrongScent; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setPrice(double price) { this.price = price; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setInStock(int inStock) { this.inStock = inStock; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setGender(String gender) { this.gender = gender; }
    public void setType(String type) { this.type = type; }
    public void setLongLasting(boolean longLasting) { isLongLasting = longLasting; }
    public void setStrongScent(boolean strongScent) { isStrongScent = strongScent; }
}
