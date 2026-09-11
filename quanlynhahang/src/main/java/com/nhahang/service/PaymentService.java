package com.nhahang.service;

import com.nhahang.dao.PaymentDAO;

import java.sql.SQLException;
import java.util.List;

public class PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAO();

    public List<PaymentDAO.PaymentRecord> findAll()
            throws SQLException {
        return paymentDAO.findAll();
    }
}
