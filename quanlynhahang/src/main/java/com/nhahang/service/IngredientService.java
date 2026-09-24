package com.nhahang.service;

import com.nhahang.dao.IngredientDAO;
import com.nhahang.model.Ingredient;
import com.nhahang.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class IngredientService {
    private final IngredientDAO ingredientDAO = new IngredientDAO();

    public List<Ingredient> findAll() throws SQLException { return ingredientDAO.findAll(); }

    public void create(String name, String unit, double stock) throws SQLException {
        validate(name, unit, stock);
        ingredientDAO.insert(name.trim(), unit.trim(), stock);
    }

    public void update(int id, String name, String unit, double stock, boolean active)
            throws SQLException {
        validate(name, unit, stock);
        ingredientDAO.update(id, name.trim(), unit.trim(), stock, active);
    }

    public void delete(int id) throws SQLException { ingredientDAO.delete(id); }

    private void validate(String name, String unit, double stock) {
        ValidationUtil.requireText(name, "Tên nguyên liệu không được để trống");
        ValidationUtil.requireText(unit, "Đơn vị không được để trống");
        ValidationUtil.requireNonNegative(stock, "Tồn kho không được âm");
    }
}
