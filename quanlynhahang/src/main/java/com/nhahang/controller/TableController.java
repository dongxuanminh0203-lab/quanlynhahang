package com.nhahang.controller;

import com.nhahang.dao.TableDAO;
import com.nhahang.service.TableService;

import java.sql.SQLException;
import java.util.List;

public class TableController {

    private final TableService tableService = new TableService();

    public List<TableDAO.TableRecord> loadTables() throws SQLException {
        return tableService.findAll();
    }

    public void addTable(String name, int capacity) throws SQLException {
        tableService.create(name, capacity);
    }

    public void editTable(int id, String name, int capacity) throws SQLException {
        tableService.update(id, name, capacity);
    }

    public void removeTable(int id) throws SQLException {
        tableService.delete(id);
    }

    public void setServing(int id, boolean serving) throws SQLException {
        tableService.updateStatus(id, serving);
    }
}
