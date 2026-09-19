package com.nhahang.util;

import com.nhahang.model.User;

public final class PermissionUtil {

    private PermissionUtil() {
    }

    public static boolean isAdmin(User user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    public static boolean canManageUsers(User user) {
        return isAdmin(user);
    }

    public static boolean canManageEmployees(User user) {
        return isAdmin(user);
    }

    public static boolean canViewStatistics(User user) {
        return isAdmin(user);
    }

    public static boolean canAccess(String permission, User user) {
        return switch (permission) {
            case "MANAGE_USERS" -> canManageUsers(user);
            case "MANAGE_EMPLOYEES" -> canManageEmployees(user);
            case "VIEW_STATISTICS" -> canViewStatistics(user);
            default -> user != null;
        };
    }
}
