package util;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class RoundedButtonUI extends BasicButtonUI {
    private static final int ARC = 18;

    @Override
    public void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();

            Color base = b.getBackground();
            if (model.isPressed()) {
                base = UITheme.COFFEE;
            }
            else if (model.isRollover()) {

                if (base.equals(UITheme.SIDEBAR)) {
                    base = UITheme.SIDEBAR_HOVER;
                }
                else base = lighten(base, 20);
            }
            g2.setColor(base);
            g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, ARC, ARC);
            g2.setColor(UITheme.BORDER);
            g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, ARC, ARC);
        } finally {
            g2.dispose();
        }
        paint(g, c);
    }

    private Color lighten(Color c, int amount) {
        return new Color(
                Math.min(255, c.getRed() + amount),
                Math.min(255, c.getGreen() + amount),
                Math.min(255, c.getBlue() + amount)
        );
    }
}
