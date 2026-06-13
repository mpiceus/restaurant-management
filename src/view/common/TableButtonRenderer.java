package view.common;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import util.RoundedButtonUI;

public class TableButtonRenderer extends JButton implements TableCellRenderer {
    private final Color color;
    public TableButtonRenderer(Color color) {
        this.color = color;

        setUI(new RoundedButtonUI());

        setForeground(Color.WHITE);
        setBackground(color);

        setFocusPainted(false);
        setBorderPainted(false);

        setHorizontalAlignment(SwingConstants.CENTER);

        setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setText(value == null ? "" : value.toString());
        setBackground(color);
        return this;
    }
}

