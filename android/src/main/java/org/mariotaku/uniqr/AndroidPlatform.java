package org.mariotaku.uniqr;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mariotaku on 2017/4/10.
 */
public class AndroidPlatform implements Platform<Bitmap> {

    @NotNull
    @Override
    public Canvas<Bitmap> createImage(int width, int height) {
        return createImage(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
    }

    @NotNull
    @Override
    public Canvas<Bitmap> createImage(@NotNull Bitmap input) {
        return new AndroidBitmapCanvas(input);
    }

    @NotNull
    @Override
    public Canvas<Bitmap> createScaled(@NotNull Bitmap input, int width, int height) {
        final int dimension = Math.min(width, height);
        final Bitmap scaled = ThumbnailUtils.extractThumbnail(input, dimension, dimension);
        return createImage(scaled);
    }

    static class AndroidBitmapCanvas implements Canvas<Bitmap> {
        private Bitmap bitmap;

        AndroidBitmapCanvas(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public int getWidth() {
            return bitmap.getWidth();
        }

        @Override
        public int getHeight() {
            return bitmap.getHeight();
        }

        @Override
        public int getPixel(int x, int y) {
            return bitmap.getPixel(x, y);
        }

        @Override
        public void setPixel(int x, int y, int pixel) {
            bitmap.setPixel(x, y, pixel);
        }

        @NotNull
        @Override
        public Bitmap produceResult() {
            return bitmap;
        }

        @Override
        public void free() {
            bitmap.recycle();
        }
    }
}
