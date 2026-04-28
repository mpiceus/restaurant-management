package view.dialog;

import controller.MonAnController;
import model.MonAnWithPriceDTO;
import util.ImageUtils;
import util.MoneyUtils;
import util.UITheme;
import view.common.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MonAnByLoaiDialog extends JDialog {
    private final MonAnController monAnController = new MonAnController();
    private final JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));

    public MonAnByLoaiDialog(Window parent, int loaiId, String tenLoai) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Mon thuoc loai: " + tenLoai);
        setSize(980, 650);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UITheme.BEIGE);
        setLayout(new BorderLayout(8, 8));

        grid.setBackground(UITheme.BEIGE);
        JScrollPane scroll = new JScrollPane(grid);
        scroll.getViewport().setBackground(UITheme.BEIGE);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(scroll, BorderLayout.CENTER);

        JButton btnClose = new JButton("Dong");
        btnClose.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(UITheme.BEIGE);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

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

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        card.setBackground(isHet ? new Color(0xEEEEEE) : UITheme.BEIGE);
        card.setPreferredSize(new Dimension(210, 250));

        JLabel title = new JLabel(String.valueOf(m.getTenMon()).toUpperCase());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13.5f));
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel img = new JPanel(new BorderLayout());
        img.setPreferredSize(new Dimension(160, 160));
        img.setMaximumSize(new Dimension(160, 160));
        img.setAlignmentX(Component.LEFT_ALIGNMENT);
        img.setBackground(isHet ? new Color(0xDDDDDD) : Color.WHITE);
        img.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        ImageIcon icon = tryLoadMonImage(m.getMonId(), 160);
        if (icon != null) {
            imgLabel.setIcon(icon);
        } else {
            imgLabel.setText(m.getTenMon());
        }
        if (isHet) {
            imgLabel.setForeground(Color.GRAY);
        }
        img.add(imgLabel, BorderLayout.CENTER);

        JLabel meta1 = new JLabel("ID: " + m.getMonId());
        JLabel meta2 = new JLabel("GIA: " + MoneyUtils.formatVnd(m.getGia()) + " VND");
        JLabel meta3 = new JLabel("TRANG THAI: " + (m.getTrangThai() == null ? "" : m.getTrangThai()));

        meta1.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        meta2.setBorder(BorderFactory.createEmptyBorder(2, 10, 0, 10));
        meta3.setBorder(BorderFactory.createEmptyBorder(2, 10, 10, 10));
        meta1.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta2.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta3.setAlignmentX(Component.LEFT_ALIGNMENT);

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

    private ImageIcon tryLoadMonImage(int monId, int size) {
        String base = "assets/monan/" + monId;
        String[] exts = new String[]{".jpg", ".jpeg", ".png"};
        for (String ext : exts) {
            File f = new File(base + ext);
            if (f.exists()) {
                return ImageUtils.loadSquareIcon(f.getPath(), size);
            }
        }
        return null;
    }
}

