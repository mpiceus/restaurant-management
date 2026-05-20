package view.common;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;

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

    public TableButtonEditor(String label, ClickHandler handler) {
        this.handler = handler;
        button.setText(label);
        button.addActionListener(this::handle);
    }

    private void handle(ActionEvent e) {
        handler.onClick(row);
        fireEditingStopped();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        button.setText(value == null ? "" : value.toString());
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return button.getText();
    }
}

