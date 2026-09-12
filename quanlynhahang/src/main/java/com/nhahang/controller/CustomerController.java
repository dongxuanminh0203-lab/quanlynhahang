package com.nhahang.controller;

import com.nhahang.dao.CustomerDAO;
import com.nhahang.service.CustomerService;

import java.sql.SQLException;
import java.util.List;

public class CustomerController {

    private final CustomerService customerService = new CustomerService();

    public List<CustomerDAO.CustomerRecord> loadCustomers() throws SQLException {
        return customerService.findAll();
    }

    public void addCustomer(CustomerDAO.CustomerRecord customer) throws SQLException {
        customerService.insert(customer);
    }

    public void updateCustomer(CustomerDAO.CustomerRecord customer) throws SQLException {
        customerService.update(customer);
    }

    public void deleteCustomer(int customerId) throws SQLException {
        customerService.delete(customerId);
    }
}
