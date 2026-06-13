package view.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import model.HourlyTrendDTO;

public class HourlyBarChartPanel extends JPanel {
    private List<HourlyTrendDTO> data = new ArrayList<>();

    public HourlyBarChartPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(0xD3C7B5)));
        setPreferredSize(new Dimension(0, 260));
    }

    public void setData(List<HourlyTrendDTO> data) {
        this.data = data == null ? new ArrayList<>() : new ArrayList<>(data);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int left = 64;
            int right = 18;
            int top = 18;
            int bottom = 42;
            int chartW = Math.max(1, w - left - right);
            int chartH = Math.max(1, h - top - bottom);
            int baseY = top + chartH;

            g2.setColor(new Color(0x5C4C35));
            g2.drawLine(left, top, left, baseY);
            g2.drawLine(left, baseY, w - right, baseY);

            g2.rotate(-Math.PI / 2);
            g2.drawString("Luong phuc vu", -(top + chartH / 2 + 40), 22);
            g2.rotate(Math.PI / 2);

            double max = 0.0;
            for (HourlyTrendDTO item : data) {
                max = Math.max(max, item.getAverageInvoices());
            }
            if (max <= 0.0) {
                max = 1.0;
            }

            int count = Math.max(data.size(), 1);
            int barSlot = chartW / count;
            int barWidth = Math.max(6, Math.min(20, barSlot - 6));
            for (int i = 0; i < data.size(); i++) {
                HourlyTrendDTO item = data.get(i);
                double ratio = item.getAverageInvoices() / max;
                int barH = (int) Math.round(ratio * (chartH - 20));
                int barX = left + i * barSlot + Math.max(0, (barSlot - barWidth) / 2);
                int barY = baseY - barH;

                g2.setColor(new Color(0xC97A1E));
                g2.fillRoundRect(barX, barY, barWidth, barH, 6, 6);

                g2.setColor(new Color(0x8E5A18));
                g2.drawRoundRect(barX, barY, barWidth, barH, 6, 6);

                g2.setColor(new Color(0x444444));
                String valueText = String.format("%.2f", item.getAverageInvoices());
                FontMetrics fm = g2.getFontMetrics();
                int valueW = fm.stringWidth(valueText);
                g2.drawString(valueText, barX + Math.max(0, (barWidth - valueW) / 2), barY - 4);

                if (i % 2 == 0 || barSlot >= 38) {
                    String label = item.getLabel();
                    int labelW = fm.stringWidth(label);
                    g2.drawString(label, barX + Math.max(0, (barWidth - labelW) / 2), baseY + 16);
                }
            }
        } finally {
            g2.dispose();
        }
    }
}
