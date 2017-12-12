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
    public Canvas<BufferedImage> createImage(int width, int height) {
        return createImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
    }

    @NotNull
    @Override
    public Canvas<BufferedImage> createImage(@NotNull BufferedImage input) {
        return new JavaSECanvas(input);
    }

    @NotNull
    @Override
    public Canvas<BufferedImage> createScaled(@NotNull BufferedImage input, int width, int height, int padding, int qrBackgroundColor) {
        final Image scaled = input.getScaledInstance(width - padding * 2, height - padding * 2, Image.SCALE_DEFAULT);
        // Create a buffered image with transparency
        final BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = buffered.createGraphics();
        bGr.setBackground(new Color(qrBackgroundColor, true));
        bGr.drawImage(scaled, padding, padding, null);
        bGr.dispose();
        scaled.flush();
        return createImage(buffered);
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
        public int getPixel(int x, int y) {
            return wrapped.getRGB(x, y);
        }

        @Override
        public void setPixel(int x, int y, int pixel) {
            wrapped.setRGB(x, y, pixel);
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
