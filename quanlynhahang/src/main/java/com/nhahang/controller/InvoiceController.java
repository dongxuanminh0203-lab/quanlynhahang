package com.nhahang.controller;

import com.nhahang.dao.InvoiceDAO;
import com.nhahang.service.InvoiceService;
import com.nhahang.model.Invoice;
import com.nhahang.model.InvoiceDetail;

import java.sql.SQLException;
import java.util.List;

public class InvoiceController {

    private final InvoiceService invoiceService =
            new InvoiceService();

        public List<Invoice> loadInvoices()
            throws SQLException {

        return invoiceService.findAll();
    }

        public List<InvoiceDetail> loadDetails(
            int orderId
    ) throws SQLException {

        return invoiceService.findDetails(orderId);
    }

    public void payInvoice(
            int orderId,
            int tableId,
            double amount,
            String paymentMethod
    ) throws SQLException {

        invoiceService.pay(
                orderId,
                tableId,
                amount,
                paymentMethod
        );
    }
}