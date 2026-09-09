package com.nhahang.service;

import com.nhahang.dao.TableDAO;

import java.sql.SQLException;
import java.util.List;

public class TableService {

    private final TableDAO tableDAO = new TableDAO();

    public List<TableDAO.TableRecord> findAll() throws SQLException {
        return tableDAO.findAll();
    }

    public void create(String name, int capacity) throws SQLException {
        validate(name, capacity);
        tableDAO.insert(name.trim(), capacity);
    }

    public void update(int id, String name, int capacity) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Mã bàn không hợp lệ");
        }
        validate(name, capacity);
        tableDAO.update(id, name.trim(), capacity);
    }

    public void delete(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Mã bàn không hợp lệ");
        }
        tableDAO.delete(id);
    }

    public void updateStatus(int id, boolean serving) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Mã bàn không hợp lệ");
        }
        tableDAO.updateStatus(id, serving);
    }

    private void validate(String name, int capacity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên bàn không được để trống");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Sức chứa phải lớn hơn 0");
        }
    }
}
