package com.nhahang.controller;

import com.nhahang.model.KitchenOrderItem;
import com.nhahang.service.KitchenService;

import java.sql.SQLException;
import java.util.List;

public class KitchenController {
    private final KitchenService kitchenService = new KitchenService();

    public List<KitchenOrderItem> loadItems() throws SQLException {
        return kitchenService.findItems();
    }

    public void updateStatus(int orderId, int productId, String status) throws SQLException {
        kitchenService.updateStatus(orderId, productId, status);
    }
}
