package com.nhahang.util;

import com.nhahang.config.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class AuditLogger {

    private AuditLogger() {
    }

    public static void log(String actionName, String entityName, Integer entityId, String details) {
        String sql = "INSERT INTO audit_logs "
                + "(action_name, entity_name, entity_id, actor_username, details) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, actionName);
            statement.setString(2, entityName);
            if (entityId == null) {
                statement.setNull(3, java.sql.Types.INTEGER);
            } else {
                statement.setInt(3, entityId);
            }
            statement.setString(4, "system");
            statement.setString(5, details);
            statement.executeUpdate();
        } catch (SQLException ignored) {
            // Audit logging must not break the completed business operation.
        }
    }
}
