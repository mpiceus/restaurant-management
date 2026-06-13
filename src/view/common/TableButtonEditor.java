package view.common;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import util.RoundedButtonUI;
import util.UITheme;

/**
 * Editor button cho JTable, gọi callback khi click.
 */
public class TableButtonEditor extends AbstractCellEditor implements TableCellEditor {

    public interface ClickHandler {
        void onClick(int row);
    }

    private final JButton button = new JButton();
    private final ClickHandler handler;
    private int row;
    private final Color color;

    public TableButtonEditor(String label, Color color, ClickHandler handler) {

        this.handler = handler;
        this.color = color;

        button.setText(label);

        button.setUI(new RoundedButtonUI());
        button.setBackground(color);
        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setHorizontalAlignment(SwingConstants.CENTER);

        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        button.addActionListener(this::handle);
    }

    public TableButtonEditor(String label, ClickHandler handler) {
        this(label, UITheme.CARAMEL, handler);
    }
    @Override
    public Component getTableCellEditorComponent(
            JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column
    ) {

        this.row = row;

        button.setText(value == null ? "" : value.toString());

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return button.getText();
    }

    private void handle(ActionEvent e) {
        handler.onClick(row);
        fireEditingStopped();
    }
}