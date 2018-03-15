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
    public Canvas<Bitmap> createImage(int width, int height, int padding, int backgroundColor) {
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        canvas.drawColor(backgroundColor);
        return new AndroidBitmapCanvas(bitmap);
    }

    @NotNull
    @Override
    public Canvas<Bitmap> createScaled(@NotNull Bitmap input, int width, int height, int padding, int backgroundColor) {
        final int dimension = Math.min(width - padding * 2, height - padding * 2);
        final Bitmap thumb = ThumbnailUtils.extractThumbnail(input, dimension, dimension);
        Bitmap scaled = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(scaled);
        canvas.drawColor(backgroundColor);
        canvas.drawBitmap(thumb, padding, padding, null);
        thumb.recycle();
        return new AndroidBitmapCanvas(scaled);
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
        public void drawDot(int l, int t, int size, int color) {
            final int width = bitmap.getWidth(), height = bitmap.getHeight();
            for (int x = l; x < l + size; x++) {
                for (int y = t; y < t + size; y++) {
                    if (x < 0 || y < 0 || x >= width || y >= height) continue;
                    bitmap.setPixel(x, y, color);
                }
            }
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
