package com.nhahang.service;

import com.nhahang.dao.TableDAO;
import com.nhahang.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class TableService {

    private final TableDAO tableDAO = new TableDAO();

    public List<TableDAO.TableRecord> findAll() throws SQLException {
        return tableDAO.findAll();
    }

    public void create(String name, int capacity) throws SQLException {
        String validName = ValidationUtil.requireText(
            name,
            "Tên bàn không được để trống"
        );
        ValidationUtil.requirePositive(capacity, "Sức chứa phải lớn hơn 0");
        tableDAO.insert(validName, capacity);
    }

    public void update(int id, String name, int capacity) throws SQLException {
        ValidationUtil.requirePositiveId(id, "Mã bàn không hợp lệ");
        String validName = ValidationUtil.requireText(
            name,
            "Tên bàn không được để trống"
        );
        ValidationUtil.requirePositive(capacity, "Sức chứa phải lớn hơn 0");
        tableDAO.update(id, validName, capacity);
    }

    public void delete(int id) throws SQLException {
        ValidationUtil.requirePositiveId(id, "Mã bàn không hợp lệ");
        tableDAO.delete(id);
    }

    public void updateStatus(int id, boolean serving) throws SQLException {
        ValidationUtil.requirePositiveId(id, "Mã bàn không hợp lệ");
        tableDAO.updateStatus(id, serving);
    }
}
