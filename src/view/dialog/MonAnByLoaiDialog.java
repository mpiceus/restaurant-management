package view.dialog;

import controller.MonAnController;
import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import model.MonAnWithPriceDTO;
import util.ImageUtils;
import util.MoneyUtils;
import util.RoundedButtonUI;
import util.RoundedPanel;
import util.ScrollUtils;
import util.UITheme;
import view.common.WrapLayout;

public class MonAnByLoaiDialog extends JDialog {
    private final MonAnController monAnController = new MonAnController();
    private final JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));

    public MonAnByLoaiDialog(Window parent, int loaiId, String tenLoai) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Món thuộc loại: " + tenLoai);
        setSize(980, 650);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UITheme.BEIGE);
        setLayout(new BorderLayout(8, 8));

        grid.setBackground(UITheme.BEIGE);
        JScrollPane scroll = new JScrollPane(grid);
        scroll.getViewport().setBackground(UITheme.BEIGE);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(scroll, BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.setUI(new RoundedButtonUI());
        btnClose.setBackground(UITheme.LATTE);
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(UITheme.BEIGE);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        ScrollUtils.apply(this);
        load(loaiId);
    }

    private void load(int loaiId) {
        List<MonAnWithPriceDTO> list = monAnController.search(null, loaiId, null);
        grid.removeAll();
        for (MonAnWithPriceDTO m : list) {
            grid.add(buildCard(m));
        }
        revalidate();
        repaint();
    }

    private JPanel buildCard(MonAnWithPriceDTO m) {
        boolean isHet = "HET".equalsIgnoreCase(String.valueOf(m.getTrangThai()));

        RoundedPanel card = new RoundedPanel(18);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        card.setBackground(isHet ? new Color(0xEEEEEE) : UITheme.SAND);
        card.setPreferredSize(new Dimension(220, 270));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel(String.valueOf(m.getTenMon()).toUpperCase());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13.5f));
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel img = new JPanel(new BorderLayout());
        img.setPreferredSize(new Dimension(170, 170));
        img.setMaximumSize(new Dimension(170, 170));
        img.setAlignmentX(Component.CENTER_ALIGNMENT);
        img.setOpaque(false);

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(170, 170));
        imgLabel.setMinimumSize(new Dimension(170, 170));
        imgLabel.setMaximumSize(new Dimension(170, 170));
        imgLabel.setOpaque(false);
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setVerticalAlignment(SwingConstants.CENTER);
        ImageIcon icon = tryLoadMonImage(m.getHinhAnh(), 170);
        if (icon != null) {
            imgLabel.setIcon(icon);
            imgLabel.setText("");
        } else {
            imgLabel.setText(m.getTenMon());
        }
        if (isHet) {
            imgLabel.setForeground(Color.GRAY);
        }
        img.add(imgLabel, BorderLayout.CENTER);

        JLabel meta1 = new JLabel("ID: " + m.getMonId());
        JLabel meta2 = new JLabel("Giá: " + MoneyUtils.formatVnd(m.getGia()) + " VND");
        JLabel meta3 = new JLabel("Trạng thái: " + (m.getTrangThai() == null ? "" : m.getTrangThai()));

        meta1.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        meta2.setBorder(BorderFactory.createEmptyBorder(2, 10, 0, 10));
        meta3.setBorder(BorderFactory.createEmptyBorder(2, 10, 10, 10));
        meta1.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta2.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta3.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta1.setHorizontalAlignment(SwingConstants.CENTER);
        meta2.setHorizontalAlignment(SwingConstants.CENTER);
        meta3.setHorizontalAlignment(SwingConstants.CENTER);

        if (isHet) {
            title.setForeground(Color.GRAY);
            meta1.setForeground(Color.GRAY);
            meta2.setForeground(Color.GRAY);
            meta3.setForeground(Color.GRAY);
        }

        card.add(title);
        card.add(img);
        card.add(meta1);
        card.add(meta2);
        card.add(meta3);
        return card;
    }

    private ImageIcon tryLoadMonImage(String imagePath, int size) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }
        File f = new File(imagePath);
        if (!f.exists()) {
            return null;
        }
        return ImageUtils.loadSquareIcon(imagePath, size);
    }
}
