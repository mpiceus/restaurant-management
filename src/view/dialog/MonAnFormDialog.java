package view.dialog;

import controller.LoaiMonAnController;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;
import model.LoaiMonAn;
import util.RoundedButtonUI;
import util.UITheme;

public class MonAnFormDialog extends JDialog {
    public static class InitialData {
        public final int monId;
        public final String tenMon;
        public final int loaiId;
        public final String trangThai;
        public final BigDecimal giaHienTai;
        public final String hinhAnh;

        public InitialData(int monId, String tenMon, int loaiId, String trangThai, BigDecimal giaHienTai, String hinhAnh) {
            this.monId = monId;
            this.tenMon = tenMon;
            this.loaiId = loaiId;
            this.trangThai = trangThai;
            this.giaHienTai = giaHienTai;
            this.hinhAnh = hinhAnh;
        }
    }

    private boolean saved = false;

    private final JTextField txtTenMon = new JTextField();
    private final JComboBox<LoaiMonAn> cbLoai = new JComboBox<>();
    private final JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"CÒN", "HẾT"});
    private final JTextField txtGia = new JTextField();
    private String oldImagePath;

    public MonAnFormDialog(Window parent, LoaiMonAnController loaiController, InitialData initial) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle(initial == null ? "Thêm món" : "Sửa món");
        setSize(420, 260);
        setLocationRelativeTo(parent);

        initLoai(loaiController);
        btnChooseImage.setUI(new RoundedButtonUI());
        btnChooseImage.setBackground(UITheme.CARAMEL);
        btnChooseImage.setForeground(Color.WHITE);
        btnChooseImage.addActionListener(e -> chooseImage());

        if (initial != null) {
            txtTenMon.setText(initial.tenMon);
            cbTrangThai.setSelectedItem(initial.trangThai == null ? "CÒN" : initial.trangThai);
            selectLoai(initial.loaiId);
            txtGia.setText(""); // sửa: giá mới là optional, để trống = không thêm dòng BangGia mới
            oldImagePath = initial.hinhAnh;
            lblImage.setText(
                    initial.hinhAnh == null
                            ? "Chua chon anh"
                            : initial.hinhAnh
            );
            if (initial.giaHienTai != null) {
                txtGia.setText(
                        initial.giaHienTai.toPlainString()
                );
            }
        } else {
            cbTrangThai.setSelectedItem("CÒN");
        }

        buildUI(initial != null);
    }

    private void initLoai(LoaiMonAnController loaiController) {
        cbLoai.removeAllItems();
        List<LoaiMonAn> loaiList = loaiController.getAll();
        for (LoaiMonAn l : loaiList) {
            cbLoai.addItem(l);
        }
        cbLoai.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LoaiMonAn) {
                    setText(((LoaiMonAn) value).getTenLoai());
                }
                return this;
            }
        });
    }

    private void selectLoai(int loaiId) {
        for (int i = 0; i < cbLoai.getItemCount(); i++) {
            LoaiMonAn l = cbLoai.getItemAt(i);
            if (l.getLoaiId() == loaiId) {
                cbLoai.setSelectedIndex(i);
                return;
            }
        }
        if (cbLoai.getItemCount() > 0) {
            cbLoai.setSelectedIndex(0);
        }
    }

    public String getOldImagePath() {
        return oldImagePath;
    }

    private void buildUI(boolean isEdit) {
        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Tên món:"));
        form.add(txtTenMon);

        form.add(new JLabel("Loại:"));
        form.add(cbLoai);

        form.add(new JLabel("Trạng thái:"));
        form.add(cbTrangThai);

        form.add(new JLabel(isEdit ? "Giá mới (tùy chọn):" : "Giá:"));
        form.add(txtGia);

        form.add(new JLabel("Ảnh:"));
        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        imagePanel.add(btnChooseImage, BorderLayout.WEST);
        imagePanel.add(lblImage, BorderLayout.CENTER);
        form.add(imagePanel);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setUI(new RoundedButtonUI());
        btnCancel.setBackground(UITheme.LATTE);
        btnCancel.setForeground(Color.WHITE);
        JButton btnSave = new JButton("Lưu");
        btnSave.setUI(new RoundedButtonUI());
        btnSave.setBackground(UITheme.CARAMEL);
        btnSave.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave(isEdit));

        form.add(btnCancel);
        form.add(btnSave);

        add(form, BorderLayout.CENTER);
    }

    private void onSave(boolean isEdit) {
        if (txtTenMon.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên món không được để trống.");
            return;
        }
        if (cbLoai.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại món.");
            return;
        }

        if (!isEdit) {
            try {
                getGia(); // validate bắt buộc
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Giá không hợp lệ.");
                return;
            }
        } else {
            // edit: giá optional, nếu có nhập thì validate
            try {
                getGiaOptional();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Giá không hợp lệ.");
                return;
            }
        }

        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public String getTenMon() {
        return txtTenMon.getText().trim();
    }

    public int getLoaiId() {
        LoaiMonAn l = (LoaiMonAn) cbLoai.getSelectedItem();
        return l == null ? 0 : l.getLoaiId();
    }

    public String getTrangThai() {
        return String.valueOf(cbTrangThai.getSelectedItem());
    }

    public BigDecimal getGia() {
        return new BigDecimal(txtGia.getText().trim());
    }

    /**
     * Sửa món: giá mới có thể bỏ trống (không insert BangGia).
     */
    public BigDecimal getGiaOptional() {
        String s = txtGia.getText().trim();
        if (s.isEmpty()) {
            return null;
        }
        return new BigDecimal(s);
    }

    private File hinhAnh;

    private final JButton btnChooseImage = new JButton("Chọn ảnh");

    private final JLabel lblImage = new JLabel("Chua chon anh");

    private void chooseImage() {

        JFileChooser chooser = new JFileChooser();

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {

            hinhAnh = chooser.getSelectedFile();

            lblImage.setText(hinhAnh.getName());
        }
    }

    public File getHinhAnh() {
        return hinhAnh;
    }
}

