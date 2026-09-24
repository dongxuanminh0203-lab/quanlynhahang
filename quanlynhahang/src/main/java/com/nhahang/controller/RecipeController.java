package com.nhahang.controller;

import com.nhahang.dao.RecipeDAO;
import com.nhahang.model.ProductIngredient;
import com.nhahang.service.RecipeService;

import java.sql.SQLException;
import java.util.List;

public class RecipeController {
    private final RecipeService recipeService = new RecipeService();

    public List<ProductIngredient> loadRecipe(int productId) throws SQLException {
        return recipeService.findByProduct(productId);
    }

    public void saveRecipe(int productId, List<RecipeDAO.RecipeLine> lines) throws SQLException {
        recipeService.replace(productId, lines);
    }
}
