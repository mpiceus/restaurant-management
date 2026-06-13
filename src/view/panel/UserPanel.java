package view.panel;

import controller.UserController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import model.User;
import service.ServiceException;
import util.Role;
import util.RoundedButtonUI;
import util.ScrollUtils;
import util.UITheme;
import view.dialog.UserFormDialog;

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
        table.setRowHeight(32);
        table.setFont(table.getFont().deriveFont(16f));
        table.setBackground(UITheme.SAND);
        table.setOpaque(true);
        table.setForeground(Color.DARK_GRAY);
        table.setSelectionBackground(UITheme.SAND.darker());
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setBackground(UITheme.COFFEE);
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setForeground(Color.DARK_GRAY);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 15f));
        configureColumns();

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnRefresh = new JButton("Reset");
        btnRefresh.setUI(new RoundedButtonUI());
        btnRefresh.setBackground(UITheme.LATTE);
        btnRefresh.setForeground(Color.WHITE);

        JButton btnAdd = new JButton("Thêm");
        btnAdd.setUI(new RoundedButtonUI());
        btnAdd.setBackground(UITheme.CARAMEL);
        btnAdd.setForeground(Color.WHITE);

        JButton btnEdit = new JButton("Sửa");
        btnEdit.setUI(new RoundedButtonUI());
        btnEdit.setBackground(UITheme.CARAMEL);
        btnEdit.setForeground(Color.WHITE);

        JButton btnDelete = new JButton("Xóa");
        btnDelete.setUI(new RoundedButtonUI());
        btnDelete.setBackground(UITheme.CARAMEL);
        btnDelete.setForeground(Color.WHITE);

        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        bottom.add(btnRefresh);
        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);

        add(bottom, BorderLayout.SOUTH);

        ScrollUtils.apply(this);
        loadData();
    }

    private void configureColumns() {
        centerAndNarrow(table.getColumnModel().getColumn(0), 48);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
    }

    private void centerAndNarrow(TableColumn column, int width) {
        column.setPreferredWidth(width);
        column.setMinWidth(Math.max(36, width - 8));
        column.setMaxWidth(width + 8);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        column.setCellRenderer(renderer);
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
