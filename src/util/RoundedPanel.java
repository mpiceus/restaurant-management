package util;

import java.awt.*;
import javax.swing.*;

public class RoundedPanel extends JPanel {

    private final int arc;

    public RoundedPanel(int arc) {
        this.arc = arc;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        try {

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Shadow
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(
                    3,
                    3,
                    getWidth() - 6,
                    getHeight() - 6,
                    arc,
                    arc);

            // Background
            g2.setColor(getBackground());
            g2.fillRoundRect(
                    0,
                    0,
                    getWidth() - 6,
                    getHeight() - 6,
                    arc,
                    arc);

        } finally {
            g2.dispose();
        }

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(UITheme.BORDER);

        g2.drawRoundRect(
                0,
                0,
                getWidth() - 1,
                getHeight() - 1,
                arc,
                arc
        );

        g2.dispose();
    }
}