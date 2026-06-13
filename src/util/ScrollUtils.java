package util;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public final class ScrollUtils {
    private ScrollUtils() {
    }

    public static void apply(Component root) {
        if (root == null) {
            return;
        }
        if (root instanceof JScrollPane) {
            tune((JScrollPane) root);
        }
        if (root instanceof Container) {
            for (Component child : ((Container) root).getComponents()) {
                apply(child);
            }
        }
    }

    private static void tune(JScrollPane pane) {
        tune(pane.getVerticalScrollBar());
        tune(pane.getHorizontalScrollBar());
    }

    private static void tune(JScrollBar bar) {
        if (bar == null) {
            return;
        }
        bar.setUnitIncrement(28);
        bar.setBlockIncrement(120);
    }
}
