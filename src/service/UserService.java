package service;

import dao.UserDAO;
import model.User;
import util.Role;

import java.util.List;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public List<User> getAll() {
        return userDAO.findAll();
    }

    public void create(String username, String password, Role role, String fullName) throws ServiceException {
        if (username == null || username.trim().isEmpty()) {
            throw new ServiceException("Username không được để trống.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ServiceException("Password không được để trống.");
        }
        if (role == null) {
            throw new ServiceException("Role không hợp lệ.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ServiceException("Fullname không được để trống.");
        }
        try {
            User u = new User();
            u.setUsername(username.trim());
            u.setPassword(password);
            u.setRole(role);
            u.setFullName(fullName.trim());
            userDAO.insert(u);
        } catch (Exception e) {
            throw new ServiceException("Không thể thêm nhân viên.", e);
        }
    }

    public void update(int userId, String username, String password, Role role, String fullName) throws ServiceException {
        if (username == null || username.trim().isEmpty()) {
            throw new ServiceException("Username không được để trống.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ServiceException("Password không được để trống.");
        }
        if (role == null) {
            throw new ServiceException("Role không hợp lệ.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ServiceException("Fullname không được để trống.");
        }
        try {
            User u = new User();
            u.setUserId(userId);
            u.setUsername(username.trim());
            u.setPassword(password);
            u.setRole(role);
            u.setFullName(fullName.trim());
            userDAO.update(u);
        } catch (Exception e) {
            throw new ServiceException("Không thể cập nhật nhân viên.", e);
        }
    }

    public void delete(int userId) throws ServiceException {
        try {
            userDAO.delete(userId);
        } catch (Exception e) {
            throw new ServiceException("Không thể xóa nhân viên.", e);
        }
    }
}
