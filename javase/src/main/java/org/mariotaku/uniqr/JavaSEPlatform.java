package org.mariotaku.uniqr;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by mariotaku on 2017/4/10.
 */
public class JavaSEPlatform implements Platform<BufferedImage> {
    @Override
    public Canvas<BufferedImage> createImage(int width, int height) {
        return createImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
    }

    @Override
    public Canvas<BufferedImage> createImage(BufferedImage input) {
        return new JavaSECanvas(input);
    }

    @Override
    public Canvas<BufferedImage> createScaled(BufferedImage input, int width, int height) {
        final Image scaled = input.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        // Create a buffered image with transparency
        final BufferedImage buffered = new BufferedImage(scaled.getWidth(null),
                scaled.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = buffered.createGraphics();
        bGr.drawImage(scaled, 0, 0, null);
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

        @Override
        public BufferedImage produceResult() {
            return wrapped;
        }

        @Override
        public void free() {
        }
    }
}
