package com.nhahang.service;

import com.nhahang.dao.CustomerDAO;

import java.sql.SQLException;
import java.util.List;

public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();

    public List<CustomerDAO.CustomerRecord> findAll() throws SQLException {
        return customerDAO.findAll();
    }

    public void insert(CustomerDAO.CustomerRecord customer) throws SQLException {
        validate(customer);
        customerDAO.insert(customer);
    }

    public void update(CustomerDAO.CustomerRecord customer) throws SQLException {
        validate(customer);
        customerDAO.update(customer);
    }

    public void delete(int customerId) throws SQLException {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ.");
        }

        customerDAO.delete(customerId);
    }

    private void validate(CustomerDAO.CustomerRecord customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Thông tin khách hàng không được để trống.");
        }

        if (customer.getId() <= 0) {
            throw new IllegalArgumentException("Mã khách hàng phải lớn hơn 0.");
        }

        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống.");
        }
    }
}
