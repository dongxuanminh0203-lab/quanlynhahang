package com.nhahang.controller;

import com.nhahang.model.Ingredient;
import com.nhahang.service.IngredientService;

import java.sql.SQLException;
import java.util.List;

public class IngredientController {
    private final IngredientService ingredientService = new IngredientService();

    public List<Ingredient> loadIngredients() throws SQLException { return ingredientService.findAll(); }
    public void addIngredient(String name, String unit, double stock) throws SQLException {
        ingredientService.create(name, unit, stock);
    }
    public void editIngredient(int id, String name, String unit, double stock, boolean active)
            throws SQLException {
        ingredientService.update(id, name, unit, stock, active);
    }
    public void removeIngredient(int id) throws SQLException { ingredientService.delete(id); }
}
