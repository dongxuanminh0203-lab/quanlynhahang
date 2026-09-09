package com.nhahang.service;

import com.nhahang.dao.ProductDAO;

import java.sql.SQLException;
import java.util.List;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    public List<ProductDAO.ProductRecord> findAll() throws SQLException {
        return productDAO.findAll();
    }

    public String nextId() throws SQLException {
        return productDAO.nextId();
    }

    public void create(ProductDAO.ProductRecord product) throws SQLException {
        validate(product);
        productDAO.insert(product);
    }

    public void update(ProductDAO.ProductRecord product) throws SQLException {
        validate(product);
        productDAO.update(product);
    }

    public void delete(String id) throws SQLException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Mã món không hợp lệ");
        }
        productDAO.delete(id);
    }

    private void validate(ProductDAO.ProductRecord product) {
        if (product == null || product.getName() == null
                || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên món không được để trống");
        }
        if (product.getCategory() == null || product.getCategory().isBlank()) {
            throw new IllegalArgumentException("Danh mục không được để trống");
        }
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Giá món không được âm");
        }
    }
}
