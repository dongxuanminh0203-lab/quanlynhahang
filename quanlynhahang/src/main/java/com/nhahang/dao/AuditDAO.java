package com.nhahang.dao;

import com.nhahang.config.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AuditDAO {

    public List<AuditRecord> findRecent(String keyword) throws SQLException {
        String sql = "SELECT audit_id, action_name, entity_name, entity_id, "
                + "actor_username, details, created_at FROM audit_logs "
                + "WHERE (? = '' OR action_name LIKE ? OR entity_name LIKE ? "
                + "OR actor_username LIKE ? OR details LIKE ?) "
                + "ORDER BY created_at DESC, audit_id DESC LIMIT 500";
        String search = keyword == null ? "" : keyword.trim();
        String pattern = "%" + search + "%";
        List<AuditRecord> records = new ArrayList<>();

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, search);
            statement.setString(2, pattern);
            statement.setString(3, pattern);
            statement.setString(4, pattern);
            statement.setString(5, pattern);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(new AuditRecord(
                            resultSet.getLong("audit_id"),
                            resultSet.getString("action_name"),
                            resultSet.getString("entity_name"),
                            (Integer) resultSet.getObject("entity_id"),
                            resultSet.getString("actor_username"),
                            resultSet.getString("details"),
                            resultSet.getTimestamp("created_at")
                    ));
                }
            }
        }
        return records;
    }

    public record AuditRecord(long id, String action, String entity, Integer entityId,
                              String actor, String details, Timestamp createdAt) {
    }
}
