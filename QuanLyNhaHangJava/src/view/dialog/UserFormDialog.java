package view.dialog;

import util.Role;

import javax.swing.*;
import java.awt.*;

public class UserFormDialog extends JDialog {
    public static class InitialData {
        public final int userId;
        public final String username;
        public final Role role;
        public final String fullName;

        public InitialData(int userId, String username, Role role, String fullName) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.fullName = fullName;
        }
    }

    private boolean saved = false;

    private final JTextField txtUsername = new JTextField();
    private final JTextField txtFullName = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JComboBox<Role> cbRole = new JComboBox<>(new Role[]{Role.ADMIN, Role.STAFF});

    public UserFormDialog(Window parent, InitialData initial) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle(initial == null ? "Thêm nhân viên" : "Sửa nhân viên");
        setSize(460, 260);
        setLocationRelativeTo(parent);

        if (initial != null) {
            txtUsername.setText(initial.username);
            cbRole.setSelectedItem(initial.role);
            txtFullName.setText(initial.fullName);
        } else {
            cbRole.setSelectedItem(Role.STAFF);
        }

        buildUI();
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Username:"));
        form.add(txtUsername);

        form.add(new JLabel("Fullname:"));
        form.add(txtFullName);

        form.add(new JLabel("Password:"));
        form.add(txtPassword);

        form.add(new JLabel("Role:"));
        form.add(cbRole);

        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        form.add(btnCancel);
        form.add(btnSave);

        add(form, BorderLayout.CENTER);
    }

    private void onSave() {
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username không được để trống.");
            return;
        }
        if (txtFullName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fullname không được để trống.");
            return;
        }
        if (new String(txtPassword.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password không được để trống.");
            return;
        }
        if (cbRole.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Role không hợp lệ.");
            return;
        }
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public String getUsername() {
        return txtUsername.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public Role getRole() {
        return (Role) cbRole.getSelectedItem();
    }

    public String getFullName() {
        return txtFullName.getText().trim();
    }
}

