package util;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

public final class UITheme {
    private UITheme() {
    }

    public static final Color BEIGE = new Color(0xF5F0E6);
    public static final Color BEIGE_2 = new Color(0xEFE7DA);
    public static final Color TEXT = Color.BLACK;
    public static final Color BORDER = new Color(0xD6CBB8);

    public static void apply() {
        applyFontScale(14f);

        // Base
        putColor("Panel.background", BEIGE);
        putColor("Viewport.background", BEIGE);
        putColor("ScrollPane.background", BEIGE);
        putColor("RootPane.background", BEIGE);

        // Text
        putColor("Label.foreground", TEXT);
        putColor("Button.foreground", TEXT);
        putColor("Menu.foreground", TEXT);
        putColor("MenuItem.foreground", TEXT);
        putColor("TabbedPane.foreground", TEXT);
        putColor("TextField.foreground", TEXT);
        putColor("PasswordField.foreground", TEXT);
        putColor("TextArea.foreground", TEXT);
        putColor("Table.foreground", TEXT);
        putColor("List.foreground", TEXT);

        // Inputs background
        putColor("TextField.background", Color.WHITE);
        putColor("PasswordField.background", Color.WHITE);
        putColor("TextArea.background", Color.WHITE);
        putColor("Table.background", Color.WHITE);
        putColor("TableHeader.background", BEIGE_2);

        // Buttons
        putColor("Button.background", BEIGE_2);

        // Borders
        putColor("Separator.foreground", BORDER);
        putColor("Separator.background", BORDER);
    }

    private static void applyFontScale(float size) {
        Font base = UIManager.getFont("Label.font");
        if (base == null) {
            base = new Font("SansSerif", Font.PLAIN, (int) size);
        }
        Font f = base.deriveFont(size);

        UIManager.put("Label.font", f);
        UIManager.put("Button.font", f);
        UIManager.put("ToggleButton.font", f);
        UIManager.put("Menu.font", f);
        UIManager.put("MenuItem.font", f);
        UIManager.put("TabbedPane.font", f);
        UIManager.put("TextField.font", f);
        UIManager.put("PasswordField.font", f);
        UIManager.put("TextArea.font", f);
        UIManager.put("Table.font", f);
        UIManager.put("TableHeader.font", f.deriveFont(Font.BOLD, size));
        UIManager.put("List.font", f);
        UIManager.put("ComboBox.font", f);
    }

    private static void putColor(String key, Color c) {
        UIManager.put(key, new ColorUIResource(c));
    }
}
