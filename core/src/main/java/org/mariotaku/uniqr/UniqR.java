package org.mariotaku.uniqr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * UniqR core class
 * Created by mariotaku on 2017/4/9.
 */
public class UniqR<T> {

    private static final int POSITION_PATTERN_SIZE = 8;
    private static final int POSITION_PATTERN_CONTENT_SIZE = 7;
    private static final int ALIGNMENT_PATTERN_SIZE = 5;
    private static final int ALIGNMENT_PATTERN_CONTENT_SIZE = 5;

    private static final boolean[][] POSITION_PATTERN_CONTENT = {
            {true, true, true, true, true, true, true},
            {true, false, false, false, false, false, true},
            {true, false, true, true, true, false, true},
            {true, false, true, true, true, false, true},
            {true, false, true, true, true, false, true},
            {true, false, false, false, false, false, true},
            {true, true, true, true, true, true, true},
    };

    private static final boolean[][] ALIGNMENT_PATTERN = {
            {true, true, true, true, true},
            {true, false, false, false, true},
            {true, false, true, false, true},
            {true, false, false, false, true},
            {true, true, true, true, true},
    };

    @NotNull
    private final Platform<T> platform;
    @Nullable
    private final T background;
    @NotNull
    private final QrData qrData;
    private int scale;
    private int dotSize;
    private int padding;
    private int qrBackgroundColor = 0xFFFFFFFF, qrPatternColor = 0xFF000000;

    public UniqR(@NotNull Platform<T> platform, @Nullable T background, @NotNull QrData qrData) {
        this.platform = platform;
        this.background = background;
        this.qrData = qrData;
        setScale(3);
        setDotSize(1);
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        if (scale < 3) throw new IllegalArgumentException("Scale must be >= 3");
        this.scale = scale;
        if (this.background == null) {
            this.setDotSize(scale);
        }
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public int getQrBackgroundColor() {
        return qrBackgroundColor;
    }

    public void setQrBackgroundColor(int color) {
        this.qrBackgroundColor = color;
    }

    public int getQrPatternColor() {
        return qrPatternColor;
    }

    public void setQrPatternColor(int color) {
        this.qrPatternColor = color;
    }

    public int getDotSize() {
        return dotSize;
    }

    public void setDotSize(int dotSize) {
        if (background == null) return;
        this.dotSize = dotSize;
    }

    @NotNull
    public Canvas<T> build() {
        final int contentSize = qrData.getSize() * scale;
        final int outputSize = contentSize + padding * 2;
        // Draw background
        Canvas<T> result;
        if (background == null) {
            result = platform.createImage(outputSize, outputSize);
        } else {
            result = platform.createScaled(background, outputSize, outputSize, padding, qrBackgroundColor);
        }
        // Draw QR code dots
        final int dotPos = (scale - dotSize) / 2;
        for (int x = 0; x < contentSize; x++) {
            for (int y = 0; y < contentSize; y++) {
                if (x % scale != dotPos || y % scale != dotPos) continue;
                final int row = y / scale, col = x / scale;
                drawDot(result, x + padding, y + padding, qrData.get(col, row) ? qrPatternColor : qrBackgroundColor);
            }
        }
        // Draw function patterns
        drawFunctionPatterns(result);
        return result;
    }

    private void drawDot(@NotNull Canvas<T> target, int x, int y, int color) {
        for (int j = x; j < x + dotSize; j++) {
            for (int k = y; k < y + dotSize; k++) {
                target.setPixel(j, k, color);
            }
        }
    }

    private void drawFunctionPatterns(@NotNull Canvas<T> target) {
        // Draw 3 position patterns (all corners except bottom right; overwrites some timing modules)
        // Top left
        drawPositionPattern(target, 0, 0, false, false);
        // Top right
        drawPositionPattern(target, qrData.getSize() - POSITION_PATTERN_SIZE, 0, true, false);
        // Bottom left
        drawPositionPattern(target, 0, qrData.getSize() - POSITION_PATTERN_SIZE, false, true);

        // Draw the numerous alignment patterns
        int[] alignPatPos = getAlignmentPatternPositions(qrData.getVersion());
        int numAlign = alignPatPos.length;
        for (int i = 0; i < numAlign; i++) {
            for (int j = 0; j < numAlign; j++) {
                if (i == 0 && j == 0 || i == 0 && j == numAlign - 1 || i == numAlign - 1 && j == 0)
                    continue;  // Skip the three position corners

                drawAlignmentPattern(target, alignPatPos[i] - ALIGNMENT_PATTERN_SIZE / 2,
                        alignPatPos[j] - ALIGNMENT_PATTERN_SIZE / 2);
            }
        }
    }

    private void drawPositionPattern(@NotNull Canvas<T> target, int qrX, int qrY, boolean padX, boolean padY) {
        final int patternOffsetX = padX ? 1 : 0, patternOffsetY = padY ? 1 : 0;
        final int l = qrX * scale, t = qrY * scale, r = l + POSITION_PATTERN_SIZE * scale,
                b = t + POSITION_PATTERN_SIZE * scale;
        for (int x = l; x < r; x++) {
            for (int y = t; y < b; y++) {
                final int row = (y - t) / scale - patternOffsetY, col = (x - l) / scale - patternOffsetX;
                boolean dot = row >= 0 && row < POSITION_PATTERN_CONTENT_SIZE && col >= 0 &&
                        col < POSITION_PATTERN_CONTENT_SIZE && POSITION_PATTERN_CONTENT[row][col];
                target.setPixel(x + padding, y + padding, dot ? qrPatternColor : qrBackgroundColor);
            }
        }

    }

    private void drawAlignmentPattern(@NotNull Canvas<T> target, int qrX, int qrY) {
        final int patternOffsetX = (ALIGNMENT_PATTERN_SIZE - ALIGNMENT_PATTERN_CONTENT_SIZE) / 2,
                patternOffsetY = (ALIGNMENT_PATTERN_SIZE - ALIGNMENT_PATTERN_CONTENT_SIZE) / 2;
        final int l = (qrX - patternOffsetX) * scale, t = (qrY - patternOffsetY) * scale,
                r = l + ALIGNMENT_PATTERN_SIZE * scale, b = t + ALIGNMENT_PATTERN_SIZE * scale;
        for (int x = l; x < r; x++) {
            for (int y = t; y < b; y++) {
                final int row = (y - t) / scale - patternOffsetY, col = (x - l) / scale - patternOffsetX;
                boolean dot = row >= 0 && row < ALIGNMENT_PATTERN_CONTENT_SIZE && col >= 0 &&
                        col < ALIGNMENT_PATTERN_CONTENT_SIZE && ALIGNMENT_PATTERN[row][col];
                target.setPixel(x + padding, y + padding, dot ? qrPatternColor : qrBackgroundColor);
            }
        }
    }


    // Returns a set of positions of the alignment patterns in ascending order. These positions are
    // used on both the x and y axes. Each value in the resulting array is in the range [0, 177).
    // This stateless pure function could be implemented as table of 40 variable-length lists of unsigned bytes.
    @NotNull
    private static int[] getAlignmentPatternPositions(int ver) {
        if (ver < 1 || ver > 40)
            throw new IllegalArgumentException("Version number out of range");
        else if (ver == 1)
            return new int[]{};
        else {
            int numAlign = ver / 7 + 2;
            int step;
            if (ver != 32)
                step = (ver * 4 + numAlign * 2 + 1) / (2 * numAlign - 2) * 2;  // ceil((size - 13) / (2*numAlign - 2)) * 2
            else  // C-C-C-Combo breaker!
                step = 26;

            int[] result = new int[numAlign];
            int size = ver * 4 + 17;
            result[0] = 6;
            for (int i = result.length - 1, pos = size - 7; i >= 1; i--, pos -= step)
                result[i] = pos;
            return result;
        }
    }
}
