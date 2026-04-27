package view.dialog;

import javax.swing.*;
import java.awt.*;

public class BanFormDialog extends JDialog {
    public static class InitialData {
        public final int banId;
        public final String tenBan;
        public final String trangThai;

        public InitialData(int banId, String tenBan, String trangThai) {
            this.banId = banId;
            this.tenBan = tenBan;
            this.trangThai = trangThai;
        }
    }

    private boolean saved = false;
    private final JTextField txtTenBan = new JTextField();
    private final JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"TRONG", "DANG_PHUC_VU"});

    public BanFormDialog(Window parent, InitialData initial) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle(initial == null ? "Thêm bàn" : "Sửa bàn");
        setSize(420, 190);
        setLocationRelativeTo(parent);

        if (initial != null) {
            txtTenBan.setText(initial.tenBan);
            cbTrangThai.setSelectedItem(initial.trangThai);
        } else {
            cbTrangThai.setSelectedItem("TRONG");
        }

        buildUI();
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Tên bàn:"));
        form.add(txtTenBan);

        form.add(new JLabel("Trạng thái:"));
        form.add(cbTrangThai);

        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        form.add(btnCancel);
        form.add(btnSave);

        add(form, BorderLayout.CENTER);
    }

    private void onSave() {
        if (txtTenBan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên bàn không được để trống.");
            return;
        }
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public String getTenBan() {
        return txtTenBan.getText().trim();
    }

    public String getTrangThai() {
        return String.valueOf(cbTrangThai.getSelectedItem());
    }
}

