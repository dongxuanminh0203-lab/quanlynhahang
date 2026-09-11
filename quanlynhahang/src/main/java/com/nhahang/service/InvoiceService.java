package com.nhahang.service;

import com.nhahang.dao.InvoiceDAO;

import java.sql.SQLException;
import java.util.List;

public class InvoiceService {

    private final InvoiceDAO invoiceDAO =
            new InvoiceDAO();

    public List<InvoiceDAO.InvoiceRecord> findAll()
            throws SQLException {

        return invoiceDAO.findAll();
    }

    public List<InvoiceDAO.InvoiceDetail> findDetails(
            int orderId
    ) throws SQLException {

        if (orderId <= 0) {
            throw new IllegalArgumentException(
                    "Mã hóa đơn không hợp lệ"
            );
        }

        return invoiceDAO.findDetails(orderId);
    }

    public void pay(
            int orderId,
            int tableId,
            double amount,
            String paymentMethod
    ) throws SQLException {

        if (orderId <= 0) {
            throw new IllegalArgumentException(
                    "Mã hóa đơn không hợp lệ"
            );
        }

        if (tableId <= 0) {
            throw new IllegalArgumentException(
                    "Mã bàn không hợp lệ"
            );
        }

        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Số tiền thanh toán không hợp lệ"
            );
        }

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Vui lòng chọn phương thức thanh toán"
            );
        }

        invoiceDAO.payInvoice(
                orderId,
                tableId,
                amount,
                paymentMethod
        );
    }
}