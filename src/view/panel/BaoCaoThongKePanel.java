package view.panel;

import controller.ThongKeController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import model.BaoCaoThongKeDTO;
import model.ThongKeItemDTO;
import util.MoneyUtils;
import util.RoundedButtonUI;
import util.RoundedPanel;
import util.ScrollUtils;
import util.UITheme;
import view.common.DatePickerField;

public class BaoCaoThongKePanel extends JPanel {
    private final ThongKeController controller = new ThongKeController();

    private final JCheckBox chkAllData = new JCheckBox("Toàn bộ dữ liệu");
    private final DatePickerField fromPicker = new DatePickerField(LocalDate.of(1900, 1, 1));
    private final DatePickerField toPicker = new DatePickerField(LocalDate.now());
    private final JLabel lblRangeHint = new JLabel();

    private final JLabel lblInvoicesValue = createValueLabel("0");
    private final JLabel lblRevenueValue = createValueLabel("0");
    private final JTable tblTopMon = createRankingTable("Món ăn", "Số món");
    private final JTable tblTopNhanVien = createRankingTable("Nhân viên", "Số bàn");
    private final JTable tblTopBan = createRankingTable("Bàn", "Số hóa đơn");
    private final HourlyBarChartPanel chartPanel = new HourlyBarChartPanel();

    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BaoCaoThongKePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BEIGE);

        add(buildFilterBar(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        chkAllData.setSelected(true);
        toggleDateInputs();
        ScrollUtils.apply(this);
        loadData();
    }

    private JPanel buildFilterBar() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(UITheme.BEIGE);
        wrap.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JPanel row = new RoundedPanel(16);
        row.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));
        row.setBackground(UITheme.BEIGE_2);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        chkAllData.addActionListener(e -> toggleDateInputs());
        row.add(new JLabel("Bộ lọc ngày:"));
        row.add(chkAllData);
        row.add(new JLabel("Từ ngày"));
        row.add(fromPicker);
        row.add(new JLabel("Đến ngày"));
        row.add(toPicker);
        
        //Nút Áp dụng 
        JButton btnApply = new JButton("Áp dụng"); 
        btnApply.setUI(new RoundedButtonUI());
        btnApply.setBackground(UITheme.CARAMEL);
        btnApply.setForeground(Color.WHITE);

        //Nút mặc định reset lại bộ lọc 
        JButton btnReset = new JButton("Mặc định"); 
        btnReset.setUI(new RoundedButtonUI());
        btnReset.setBackground(UITheme.LATTE);
        btnReset.setForeground(Color.WHITE);

        btnApply.addActionListener(e -> loadData());
        btnReset.addActionListener(e -> resetFilter());
        row.add(btnApply);
        row.add(btnReset);

        lblRangeHint.setForeground(new Color(0x5A4B2A));
        lblRangeHint.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(UITheme.BEIGE);
        outer.add(row);
        outer.add(lblRangeHint);

        wrap.add(outer, BorderLayout.CENTER);
        return wrap;
    }

    private JScrollPane buildContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.BEIGE);
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel summary = new JPanel(new GridLayout(1, 2, 12, 12));
        summary.setOpaque(false);
        summary.add(buildMetricCard("Tổng số hóa đơn", lblInvoicesValue, UITheme.COFFEE));
        summary.add(buildMetricCard("Tổng doanh thu", lblRevenueValue, UITheme.CARAMEL));

        JPanel rankings = new JPanel(new GridLayout(1, 3, 10, 10));
        rankings.setOpaque(false);
        rankings.add(buildRankingCard("Top 5 mon bán chạy", tblTopMon, UITheme.COFFEE_DARK));
        rankings.add(buildRankingCard("Top 3 nhân viên", tblTopNhanVien, UITheme.LATTE));
        rankings.add(buildRankingCard("Top 3 bàn", tblTopBan, UITheme.COFFEE));

        JPanel chartCard = new RoundedPanel(18);
        chartCard.setLayout(new BorderLayout());
        chartCard.setBackground(new Color(0xF8F3EA));
        chartCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xC9BCA6)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel chartTitle = new JLabel("Xu hướng lượng phục vụ theo khung giờ");
        chartTitle.setFont(chartTitle.getFont().deriveFont(Font.BOLD, 15f));
        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(chartPanel, BorderLayout.CENTER);

        content.add(summary);
        content.add(Box.createVerticalStrut(12));
        content.add(rankings);
        content.add(Box.createVerticalStrut(12));
        content.add(chartCard);

        JScrollPane scroll = new JScrollPane(content);
        scroll.getViewport().setBackground(UITheme.BEIGE);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    private JPanel buildMetricCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new RoundedPanel(18);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker()),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        return card;
    }

    private JPanel buildRankingCard(String title, JTable table, Color color) {
        JPanel card = new RoundedPanel(18);
        card.setLayout(new BorderLayout(0, 6));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker()),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.setPreferredSize(new Dimension(0, 215));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        card.add(lblTitle, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 120)));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setPreferredSize(new Dimension(250, 155));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JTable createRankingTable(String colName, String qtyName) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"#", colName, qtyName}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setShowGrid(false);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setIntercellSpacing(new Dimension(6, 0));

        TableColumn rankCol = table.getColumnModel().getColumn(0);
        rankCol.setPreferredWidth(34);
        rankCol.setMinWidth(30);
        rankCol.setMaxWidth(42);

        TableColumn nameCol = table.getColumnModel().getColumn(1);
        nameCol.setPreferredWidth(150);

        return table;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 24f));
        return label;
    }

    private void toggleDateInputs() {
        boolean enabled = !chkAllData.isSelected();
        fromPicker.setEnabled(enabled);
        toPicker.setEnabled(enabled);
        lblRangeHint.setText(enabled
                ? "Thống kê theo khoảng ngày đã chọn."
                : "Đang áp dụng toàn bộ dữ liệu từ trước đến nay.");
    }

    private void resetFilter() {
        chkAllData.setSelected(true);
        fromPicker.setDate(LocalDate.of(1900, 1, 1));
        toPicker.setDate(LocalDate.now());
        toggleDateInputs();
        loadData();
    }

    private void loadData() {
        LocalDate from = null;
        LocalDate to = null;
        if (!chkAllData.isSelected()) {
            from = fromPicker.getDate();
            to = toPicker.getDate();
            if (from != null && to != null && from.isAfter(to)) {
                JOptionPane.showMessageDialog(this, "Từ ngày phải nhỏ hơn hoặc bằng đến ngày.");
                return;
            }
        }

        BaoCaoThongKeDTO data = controller.getBaoCao(from, to);

        lblInvoicesValue.setText(String.valueOf(data.getTongHoaDon()));
        lblRevenueValue.setText(MoneyUtils.formatVnd(data.getTongDoanhThu()));

        fillTable(tblTopMon, data.getTopMonBanChay(), "mon");
        fillTable(tblTopNhanVien, data.getTopNhanVien(), "nhan_vien");
        fillTable(tblTopBan, data.getTopBan(), "ban");
        chartPanel.setData(data.getHourlyTrend());

        if (chkAllData.isSelected()) {
            lblRangeHint.setText("Đang áp dụng toàn bọ dữ liệu từ trước đến nay.");
        } else {
            lblRangeHint.setText("Đang thống kê: " + dateFmt.format(from) + " -> " + dateFmt.format(to));
        }
    }

    private void fillTable(JTable table, List<ThongKeItemDTO> items, String type) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        int rank = 1;
        for (ThongKeItemDTO item : items) {
            String qtyLabel;
            if ("mon".equals(type)) {
                qtyLabel = item.getSoLuong() + " món";
            } else if ("nhan_vien".equals(type)) {
                qtyLabel = item.getSoLuong() + " mòn";
            } else {
                qtyLabel = item.getSoLuong() + " hóa đơn";
            }
            model.addRow(new Object[]{rank++, item.getTen(), qtyLabel});
        }
    }
}
