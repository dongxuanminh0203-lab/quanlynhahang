package com.nhahang.service;

import com.nhahang.dao.ProductDAO;
import com.nhahang.model.Product;
import com.nhahang.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    public List<Product> findAll() throws SQLException {
        return productDAO.findAll();
    }

    public String nextId() throws SQLException {
        return productDAO.nextId();
    }

    public void create(Product product) throws SQLException {
        validate(product);
        productDAO.insert(product);
    }

    public void update(Product product) throws SQLException {
        validate(product);
        productDAO.update(product);
    }

    public void delete(String id) throws SQLException {
        ValidationUtil.requireText(id, "Mã món không hợp lệ");
        productDAO.delete(id);
    }

    private void validate(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Thông tin món không được để trống");
        }
        ValidationUtil.requireText(
                product.getName(),
                "Tên món không được để trống"
        );
        ValidationUtil.requireText(
                product.getCategory(),
                "Danh mục không được để trống"
        );
        ValidationUtil.requireNonNegative(
                product.getPrice(),
                "Giá món không được âm"
        );
    }
}
