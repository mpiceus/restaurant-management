package util;

import model.User;

/**
 * Lưu thông tin đăng nhập hiện tại (singleton đơn giản).
 */
public class Session {
    private static User currentUser;

    private Session() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}

