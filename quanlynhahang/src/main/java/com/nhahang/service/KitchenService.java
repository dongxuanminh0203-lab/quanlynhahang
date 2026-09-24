package com.nhahang.service;

import com.nhahang.dao.KitchenDAO;
import com.nhahang.model.KitchenOrderItem;

import java.sql.SQLException;
import java.util.List;

public class KitchenService {
    private final KitchenDAO kitchenDAO = new KitchenDAO();

    public List<KitchenOrderItem> findItems() throws SQLException {
        return kitchenDAO.findItems();
    }

    public void updateStatus(int orderId, int productId, String status) throws SQLException {
        if (!"PENDING".equals(status) && !"COOKING".equals(status)
                && !"READY".equals(status)) {
            throw new IllegalArgumentException("Trạng thái món không hợp lệ");
        }
        kitchenDAO.updateStatus(orderId, productId, status);
    }
}
