package org.mariotaku.uniqr;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by mariotaku on 2017/4/10.
 */
public class JavaSEPlatform implements Platform<BufferedImage> {
    @NotNull
    @Override
    public Canvas<BufferedImage> createImage(int width, int height, int padding, int qrBackgroundColor) {
        final BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = buffered.createGraphics();
        bGr.setColor(new Color(qrBackgroundColor, true));
        bGr.fillRect(0, 0, buffered.getWidth(), buffered.getHeight());
        bGr.dispose();
        buffered.flush();
        return new JavaSECanvas(buffered);
    }

    @NotNull
    @Override
    public Canvas<BufferedImage> createScaled(@NotNull BufferedImage input, int width, int height, int padding, int qrBackgroundColor) {
        final Image scaled = input.getScaledInstance(width - padding * 2, height - padding * 2, Image.SCALE_DEFAULT);
        // Create a buffered image with transparency
        final BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = buffered.createGraphics();
        bGr.setColor(new Color(qrBackgroundColor, true));
        bGr.fillRect(0, 0, buffered.getWidth(), buffered.getHeight());
        bGr.drawImage(scaled, padding, padding, null);
        bGr.dispose();
        scaled.flush();
        return new JavaSECanvas(buffered);
    }

    static class JavaSECanvas implements Canvas<BufferedImage> {

        private BufferedImage wrapped;

        public JavaSECanvas(BufferedImage wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public int getWidth() {
            return wrapped.getWidth();
        }

        @Override
        public int getHeight() {
            return wrapped.getHeight();
        }

        @Override
        public void drawDot(int l, int t, int size, int color) {
            final int width = wrapped.getWidth(), height = wrapped.getHeight();
            for (int x = l; x < l + size; x++) {
                for (int y = t; y < t + size; y++) {
                    if (x < 0 || y < 0 || x >= width || y >= height) continue;
                    wrapped.setRGB(x, y, color);
                }
            }
        }

        @NotNull
        @Override
        public BufferedImage produceResult() {
            return wrapped;
        }

        @Override
        public void free() {
        }
    }
}
