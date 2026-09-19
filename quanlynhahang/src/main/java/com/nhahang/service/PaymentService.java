package com.nhahang.service;

import com.nhahang.dao.PaymentDAO;
import com.nhahang.model.Payment;

import java.sql.SQLException;
import java.util.List;

public class PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();

    public List<Payment> findAll()
            throws SQLException {
        return paymentDAO.findAll();
    }
}
