package com.nhahang.controller;

import com.nhahang.dao.PaymentDAO;
import com.nhahang.service.PaymentService;

import java.sql.SQLException;
import java.util.List;

public class PaymentController {

    private final PaymentService paymentService =
            new PaymentService();

    public List<PaymentDAO.PaymentRecord> loadPayments()
            throws SQLException {
        return paymentService.findAll();
    }
}
