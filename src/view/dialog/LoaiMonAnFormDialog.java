package view.dialog;

import java.awt.*;
import javax.swing.*;
import util.RoundedButtonUI;
import util.UITheme;

public class LoaiMonAnFormDialog extends JDialog {
    private boolean saved = false;
    private final JTextField txtTenLoai = new JTextField();

    public LoaiMonAnFormDialog(Window parent, String initialTenLoai) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle(initialTenLoai == null ? "Thêm loại món" : "Sửa loại món");
        setSize(420, 170);
        setLocationRelativeTo(parent);

        if (initialTenLoai != null) {
            txtTenLoai.setText(initialTenLoai);
        }

        buildUI();
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Tên loại:"));
        form.add(txtTenLoai);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setUI(new RoundedButtonUI());
        btnCancel.setBackground(UITheme.LATTE);
        btnCancel.setForeground(Color.WHITE);
        JButton btnSave = new JButton("Lưu");
        btnSave.setUI(new RoundedButtonUI());
        btnSave.setBackground(UITheme.CARAMEL);
        btnSave.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        form.add(btnCancel);
        form.add(btnSave);

        add(form, BorderLayout.CENTER);
    }

    private void onSave() {
        if (txtTenLoai.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên loại không được để trống.");
            return;
        }
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public String getTenLoai() {
        return txtTenLoai.getText().trim();
    }
}

