package com.nhahang.service;

import com.nhahang.dao.RecipeDAO;
import com.nhahang.model.ProductIngredient;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeService {
    private final RecipeDAO recipeDAO = new RecipeDAO();

    public List<ProductIngredient> findByProduct(int productId) throws SQLException {
        return recipeDAO.findByProduct(productId);
    }

    public void replace(int productId, List<RecipeDAO.RecipeLine> lines) throws SQLException {
        Set<Integer> ingredientIds = new HashSet<>();
        for (RecipeDAO.RecipeLine line : lines) {
            if (line.quantity() <= 0) {
                throw new IllegalArgumentException("Định lượng nguyên liệu phải lớn hơn 0");
            }
            if (!ingredientIds.add(line.ingredientId())) {
                throw new IllegalArgumentException("Không được chọn trùng nguyên liệu");
            }
        }
        recipeDAO.replace(productId, lines);
    }
}
