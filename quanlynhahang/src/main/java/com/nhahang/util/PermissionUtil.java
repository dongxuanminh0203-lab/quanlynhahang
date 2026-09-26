package com.nhahang.util;

import com.nhahang.model.User;

public final class PermissionUtil {

    private PermissionUtil() {
    }

    public static boolean isAdmin(User user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    public static boolean isChef(User user) {
        return user != null && "CHEF".equalsIgnoreCase(user.getRole());
    }

    public static boolean canViewHome(User user) {
        return user != null && (isAdmin(user) || isChef(user) || "STAFF".equalsIgnoreCase(user.getRole()));
    }

    public static boolean canManageTables(User user) {
        return isAdmin(user) || isChef(user) || "STAFF".equalsIgnoreCase(user.getRole());
    }

    public static boolean canManageProducts(User user) {
        return isAdmin(user) || isChef(user);
    }

    public static boolean canManageIngredients(User user) {
        return isAdmin(user) || isChef(user);
    }

    public static boolean canAccessKitchen(User user) {
        return isAdmin(user) || isChef(user) || "STAFF".equalsIgnoreCase(user.getRole());
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
            case "VIEW_HOME" -> canViewHome(user);
            case "MANAGE_TABLES" -> canManageTables(user);
            case "MANAGE_PRODUCTS" -> canManageProducts(user);
            case "MANAGE_INGREDIENTS" -> canManageIngredients(user);
            case "ACCESS_KITCHEN" -> canAccessKitchen(user);
            case "MANAGE_USERS" -> canManageUsers(user);
            case "MANAGE_EMPLOYEES" -> canManageEmployees(user);
            case "VIEW_STATISTICS" -> canViewStatistics(user);
            default -> user != null;
        };
    }
}
