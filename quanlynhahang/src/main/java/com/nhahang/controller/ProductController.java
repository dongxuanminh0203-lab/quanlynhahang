package com.nhahang.controller;

import com.nhahang.dao.ProductDAO;
import com.nhahang.service.ProductService;

import java.sql.SQLException;
import java.util.List;

public class ProductController {

    private final ProductService productService = new ProductService();

    public List<ProductDAO.ProductRecord> loadProducts() throws SQLException {
        return productService.findAll();
    }

    public String nextProductId() throws SQLException {
        return productService.nextId();
    }

    public void addProduct(ProductDAO.ProductRecord product) throws SQLException {
        productService.create(product);
    }

    public void editProduct(ProductDAO.ProductRecord product) throws SQLException {
        productService.update(product);
    }

    public void removeProduct(String id) throws SQLException {
        productService.delete(id);
    }
}
