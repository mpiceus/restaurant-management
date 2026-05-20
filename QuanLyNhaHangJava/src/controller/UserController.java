package controller;

import model.User;
import service.ServiceException;
import service.UserService;
import util.Role;

import java.util.List;

public class UserController {
    private final UserService userService = new UserService();

    public List<User> getAll() {
        return userService.getAll();
    }

    public void create(String username, String password, Role role, String fullName) throws ServiceException {
        userService.create(username, password, role, fullName);
    }

    public void update(int userId, String username, String password, Role role, String fullName) throws ServiceException {
        userService.update(userId, username, password, role, fullName);
    }

    public void delete(int userId) throws ServiceException {
        userService.delete(userId);
    }
}
