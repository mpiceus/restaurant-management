package service;

import dao.UserDAO;
import model.User;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) throws ServiceException {
        if (username == null || username.trim().isEmpty()) {
            throw new ServiceException("Vui lòng nhập username.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ServiceException("Vui lòng nhập password.");
        }

        User user = userDAO.findByUsernameAndPassword(username.trim(), password);
        if (user == null) {
            throw new ServiceException("Sai username hoặc password.");
        }
        if (user.getRole() == null) {
            throw new ServiceException("Tài khoản chưa có role hợp lệ (ADMIN/STAFF).");
        }
        return user;
    }
}

