package com.nhahang.service;

import com.nhahang.dao.OrderDAO;
import com.nhahang.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO = new OrderDAO();

    public int create(int tableId, int employeeId, List<OrderDAO.OrderItem> items)
            throws SQLException {
        ValidationUtil.requirePositiveId(tableId, "Vui lòng chọn bàn");
        ValidationUtil.requirePositiveId(employeeId, "Tài khoản chưa có nhân viên");
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng chưa có món");
        }
        return orderDAO.createOrder(tableId, employeeId, items);
    }
}
