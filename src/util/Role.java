package util;

/**
 * Vai trò người dùng trong hệ thống.
 */
public enum Role {
    ADMIN,
    STAFF;

    public static Role fromDb(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        if ("ADMIN".equals(normalized)) {
            return ADMIN;
        }
        if ("STAFF".equals(normalized) || "NHANVIEN".equals(normalized)) {
            return STAFF;
        }
        return null;
    }
}

