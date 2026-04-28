package util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public final class ImageUtils {
    private ImageUtils() {
    }

    public static ImageIcon loadSquareIcon(String path, int size) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        try {
            File f = new File(path);
            if (!f.exists()) {
                return null;
            }
            BufferedImage src = ImageIO.read(f);
            if (src == null) {
                return null;
            }
            return new ImageIcon(toSquareCover(src, size));
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Image toSquareCover(BufferedImage src, int size) {
        int w = src.getWidth();
        int h = src.getHeight();
        if (w <= 0 || h <= 0) {
            return new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        }

        double scale = Math.max((double) size / w, (double) size / h);
        int scaledW = (int) Math.ceil(w * scale);
        int scaledH = (int) Math.ceil(h * scale);

        BufferedImage scaled = new BufferedImage(scaledW, scaledH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(src, 0, 0, scaledW, scaledH, null);
        g.dispose();

        int x = Math.max(0, (scaledW - size) / 2);
        int y = Math.max(0, (scaledH - size) / 2);
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.drawImage(scaled, -x, -y, null);
        g2.dispose();
        return out;
    }
}

