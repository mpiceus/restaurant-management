package view.common;

import util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoundedTextField extends JTextField {
    private final String placeholder;

    public RoundedTextField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        setBorder(new EmptyBorder(10, 14, 10, 14));
        setBackground(Color.WHITE);
        setForeground(UITheme.TEXT);
        setCaretColor(UITheme.TEXT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        g2.setColor(UITheme.BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);

        super.paintComponent(g2);

        if (!isFocusOwner() && getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
            g2.setColor(new Color(0, 0, 0, 90));
            Insets ins = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, ins.left, y);
        }

        g2.dispose();
    }
}

