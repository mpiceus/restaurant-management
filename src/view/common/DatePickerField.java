package view.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class DatePickerField extends JPanel {
    private static final LocalDate MIN_DATE = LocalDate.of(2026, 3, 1);

    private final JTextField field = new JTextField();
    private final JButton button = new JButton("v");
    private final JPopupMenu popup = new JPopupMenu();
    private final CalendarPopup calendarPopup = new CalendarPopup();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDate date;

    public DatePickerField(LocalDate initialDate) {
        setLayout(new BorderLayout(4, 0));
        setOpaque(false);

        field.setEditable(true);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xBDAE97)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        field.addActionListener(e -> commitTypedValue());
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                commitTypedValue();
            }
        });

        button.setMargin(new java.awt.Insets(0, 8, 0, 8));
        button.addActionListener(e -> showPopup());

        add(field, BorderLayout.CENTER);
        add(button, BorderLayout.EAST);

        setDate(initialDate);
    }

    public LocalDate getDate() {
        commitTypedValue();
        return date;
    }

    public void setDate(LocalDate date) {
        LocalDate normalized = normalize(date);
        this.date = normalized;
        field.setText(normalized == null ? "" : fmt.format(normalized));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        field.setEnabled(enabled);
        button.setEnabled(enabled);
    }

    private void showPopup() {
        if (!isEnabled()) {
            return;
        }
        calendarPopup.setDate(date == null ? MIN_DATE : date);
        popup.removeAll();
        popup.setBorder(BorderFactory.createLineBorder(new Color(0xBDAE97)));
        popup.add(calendarPopup);
        popup.show(this, 0, getHeight());
    }

    private void commitTypedValue() {
        if (!isEnabled()) {
            return;
        }
        String text = field.getText() == null ? "" : field.getText().trim();
        if (text.isEmpty()) {
            setDate(date);
            return;
        }
        try {
            LocalDate parsed = LocalDate.parse(text, fmt);
            if (parsed.isBefore(MIN_DATE)) {
                JOptionPane.showMessageDialog(this, "Ngay phai tu 2026-03-01 tro di.");
                setDate(MIN_DATE);
                return;
            }
            this.date = parsed;
            field.setText(fmt.format(parsed));
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Dinh dang ngay khong hop le. Dung yyyy-MM-dd.");
            setDate(date == null ? MIN_DATE : date);
        }
    }

    private LocalDate normalize(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.isBefore(MIN_DATE) ? MIN_DATE : date;
    }

    private class CalendarPopup extends JPanel {
        private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        private final JPanel daysPanel = new JPanel(new GridLayout(0, 7, 2, 2));
        private YearMonth viewMonth;

        CalendarPopup() {
            setLayout(new BorderLayout(4, 4));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            setPreferredSize(new Dimension(280, 250));

            JPanel header = new JPanel(new BorderLayout(6, 0));
            header.setOpaque(false);
            JButton prev = new JButton("<");
            JButton next = new JButton(">");
            prev.setMargin(new java.awt.Insets(2, 8, 2, 8));
            next.setMargin(new java.awt.Insets(2, 8, 2, 8));
            prev.addActionListener(e -> shiftMonth(-1));
            next.addActionListener(e -> shiftMonth(1));
            header.add(prev, BorderLayout.WEST);
            header.add(monthLabel, BorderLayout.CENTER);
            header.add(next, BorderLayout.EAST);

            JPanel weekHeader = new JPanel(new GridLayout(1, 7, 2, 2));
            weekHeader.setOpaque(false);
            String[] labels = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
            for (String s : labels) {
                JLabel day = new JLabel(s, SwingConstants.CENTER);
                day.setForeground(new Color(0x665544));
                weekHeader.add(day);
            }

            add(header, BorderLayout.NORTH);
            add(daysPanel, BorderLayout.CENTER);
            add(weekHeader, BorderLayout.SOUTH);
        }

        void setDate(LocalDate selected) {
            LocalDate normalized = normalize(selected);
            viewMonth = YearMonth.from(normalized == null ? MIN_DATE : normalized);
            rebuild();
        }

        private void shiftMonth(int delta) {
            YearMonth nextMonth = viewMonth.plusMonths(delta);
            YearMonth minMonth = YearMonth.from(MIN_DATE);
            if (nextMonth.isBefore(minMonth)) {
                nextMonth = minMonth;
            }
            viewMonth = nextMonth;
            rebuild();
        }

        private void rebuild() {
            monthLabel.setText(viewMonth.getMonth().toString() + " " + viewMonth.getYear());
            daysPanel.removeAll();

            LocalDate first = viewMonth.atDay(1);
            int startIndex = first.getDayOfWeek().getValue() % 7;
            for (int i = 0; i < startIndex; i++) {
                daysPanel.add(new JLabel(""));
            }

            for (int day = 1; day <= viewMonth.lengthOfMonth(); day++) {
                LocalDate current = viewMonth.atDay(day);
                if (current.isBefore(MIN_DATE)) {
                    JLabel locked = new JLabel(String.valueOf(day), SwingConstants.CENTER);
                    locked.setForeground(new Color(0xBBBBBB));
                    daysPanel.add(locked);
                    continue;
                }

                JButton b = new JButton(String.valueOf(day));
                b.setMargin(new java.awt.Insets(2, 2, 2, 2));
                b.addActionListener((ActionEvent e) -> {
                    setDate(current);
                    DatePickerField.this.setDate(current);
                    popup.setVisible(false);
                });
                daysPanel.add(b);
            }

            daysPanel.revalidate();
            daysPanel.repaint();
        }
    }
}
