package view.dialog;

import controller.MonAnController;
import model.MonAnWithPriceDTO;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BangGiaFormDialog extends JDialog {
    public static class InitialData {
        public final int bangGiaId;
        public final int monId;
        public final String gia;
        public final String ngayApDung;

        public InitialData(int bangGiaId, int monId, String gia, String ngayApDung) {
            this.bangGiaId = bangGiaId;
            this.monId = monId;
            this.gia = gia;
            this.ngayApDung = ngayApDung;
        }
    }

    private boolean saved = false;
    private final JComboBox<MonAnWithPriceDTO> cbMon = new JComboBox<>();
    private final JTextField txtGia = new JTextField();
    private final JTextField txtNgay = new JTextField();

    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BangGiaFormDialog(Window parent, InitialData initial) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle(initial == null ? "Thêm bảng giá" : "Sửa bảng giá");
        setSize(520, 210);
        setLocationRelativeTo(parent);

        initMon();

        if (initial != null) {
            selectMon(initial.monId);
            txtGia.setText(initial.gia);
            txtNgay.setText(initial.ngayApDung);
        } else {
            txtNgay.setText(LocalDate.now().format(dateFmt));
        }

        buildUI();
    }

    private void initMon() {
        cbMon.removeAllItems();
        List<MonAnWithPriceDTO> list = new MonAnController().getAll();
        for (MonAnWithPriceDTO m : list) {
            cbMon.addItem(m);
        }
        cbMon.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MonAnWithPriceDTO) {
                    MonAnWithPriceDTO m = (MonAnWithPriceDTO) value;
                    setText(m.getMonId() + " - " + m.getTenMon());
                }
                return this;
            }
        });
    }

    private void selectMon(int monId) {
        for (int i = 0; i < cbMon.getItemCount(); i++) {
            MonAnWithPriceDTO m = cbMon.getItemAt(i);
            if (m.getMonId() == monId) {
                cbMon.setSelectedIndex(i);
                return;
            }
        }
        if (cbMon.getItemCount() > 0) {
            cbMon.setSelectedIndex(0);
        }
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        form.add(new JLabel("Món:"));
        form.add(cbMon);

        form.add(new JLabel("Giá:"));
        form.add(txtGia);

        form.add(new JLabel("Ngày áp dụng (yyyy-MM-dd):"));
        form.add(txtNgay);

        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu");
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> onSave());

        form.add(btnCancel);
        form.add(btnSave);

        add(form, BorderLayout.CENTER);
    }

    private void onSave() {
        if (cbMon.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món.");
            return;
        }
        try {
            getGia();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Giá không hợp lệ.");
            return;
        }
        try {
            getNgayApDung();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày áp dụng không hợp lệ (yyyy-MM-dd).");
            return;
        }
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public int getMonId() {
        MonAnWithPriceDTO m = (MonAnWithPriceDTO) cbMon.getSelectedItem();
        return m == null ? 0 : m.getMonId();
    }

    public BigDecimal getGia() {
        return new BigDecimal(txtGia.getText().trim());
    }

    public LocalDate getNgayApDung() {
        return LocalDate.parse(txtNgay.getText().trim(), dateFmt);
    }
}

