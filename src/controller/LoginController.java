package controller;

import model.User;
import service.AuthService;
import service.ServiceException;
import util.Session;
import view.admin.AdminDashboardFrame;
import view.staff.StaffDashboardFrame;

import javax.swing.*;

public class LoginController {
    private final AuthService authService = new AuthService();

    public void login(String username, String password, JFrame loginFrame) {
        try {
            User user = authService.login(username, password);
            Session.setCurrentUser(user);

            loginFrame.dispose();

            if (user.getRole() == util.Role.ADMIN) {
                new AdminDashboardFrame();
            } else {
                new StaffDashboardFrame();
            }
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(loginFrame, e.getMessage(), "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}

