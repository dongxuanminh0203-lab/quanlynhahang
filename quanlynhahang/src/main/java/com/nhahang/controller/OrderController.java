package com.nhahang.controller;

import com.nhahang.dao.OrderDAO;
import com.nhahang.dao.ProductDAO;
import com.nhahang.dao.TableDAO;
import com.nhahang.service.OrderService;
import com.nhahang.model.OrderItem;
import com.nhahang.model.Product;
import com.nhahang.model.RestaurantTable;

import java.sql.SQLException;
import java.util.List;

public class OrderController {

    private final OrderService orderService = new OrderService();
    private final ProductDAO productDAO = new ProductDAO();
    private final TableDAO tableDAO = new TableDAO();

    public List<Product> loadProducts() throws SQLException {
        return productDAO.findAll();
    }

    public List<RestaurantTable> loadTables() throws SQLException {
        return tableDAO.findAll();
    }

    public int createOrder(int tableId, int employeeId, List<OrderItem> items)
            throws SQLException {
        return orderService.create(tableId, employeeId, items);
    }
}
