package com.nhahang.model;

public class Product {
    private final String id;
    private final String name;
    private final String category;
    private final double price;
    private final boolean available;
    private final String imagePath;
    private final String ingredients;
    private final boolean serveImmediately;

    public Product(String id, String name, String category, double price,
                   boolean available, String imagePath) {
        this(id, name, category, price, available, imagePath, "");
    }

    public Product(String id, String name, String category, double price,
                   boolean available, String imagePath, String ingredients) {
        this(id, name, category, price, available, imagePath, ingredients, false);
    }

    public Product(String id, String name, String category, double price,
                   boolean available, String imagePath, String ingredients,
                   boolean serveImmediately) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.available = available;
        this.imagePath = imagePath;
        this.ingredients = ingredients == null ? "" : ingredients;
        this.serveImmediately = serveImmediately;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public String getImagePath() { return imagePath; }
    public String getIngredients() { return ingredients; }
    public boolean isServeImmediately() { return serveImmediately; }
}
