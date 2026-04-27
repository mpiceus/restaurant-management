package view.panel;

import controller.UserController;
import model.User;
import service.ServiceException;
import util.Role;
import view.dialog.UserFormDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Quản lý nhân viên (Users) - chỉ ADMIN.
 */
public class UserPanel extends JPanel {
    private final UserController controller = new UserController();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public UserPanel() {
        setLayout(new BorderLayout(8, 8));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Username", "Fullname", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");

        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        bottom.add(btnRefresh);
        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);

        add(bottom, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        List<User> list = controller.getAll();
        tableModel.setRowCount(0);
        for (User u : list) {
            tableModel.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getFullName(), u.getRole()});
        }
    }

    private Integer getSelectedUserId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (Integer) tableModel.getValueAt(row, 0);
    }

    private void onAdd() {
        UserFormDialog dialog = new UserFormDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            controller.create(dialog.getUsername(), dialog.getPassword(), dialog.getRole(), dialog.getFullName());
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        Integer userId = getSelectedUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nhân viên để sửa.");
            return;
        }

        int row = table.getSelectedRow();
        String username = String.valueOf(tableModel.getValueAt(row, 1));
        String fullName = String.valueOf(tableModel.getValueAt(row, 2));
        Role role = (Role) tableModel.getValueAt(row, 3);

        UserFormDialog dialog = new UserFormDialog(
                SwingUtilities.getWindowAncestor(this),
                new UserFormDialog.InitialData(userId, username, role, fullName)
        );
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            controller.update(userId, dialog.getUsername(), dialog.getPassword(), dialog.getRole(), dialog.getFullName());
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        Integer userId = getSelectedUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nhân viên để xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa user id=" + userId + " ?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.delete(userId);
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

