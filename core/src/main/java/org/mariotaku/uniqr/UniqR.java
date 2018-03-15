package org.mariotaku.uniqr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * UniqR core class
 * Created by mariotaku on 2017/4/9.
 */
public class UniqR<T> {

    private static final int POSITION_PATTERN_SIZE = 9;
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
    private int qrBackgroundColor = 0xFFFFFFFF;
    private int qrFunctionPatternBackgroundColor = 0xFFFFFFFF;
    private int qrPatternColor = 0xFF000000;
    private int qrEmptyPatternColor = 0xFFFFFFFF;
    private boolean qrFunctionPatternBackgroundColorSet = false;
    private boolean qrEmptyPatternColorSet = false;

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

    public int getQrFunctionPatternBackgroundColor() {
        if (!qrFunctionPatternBackgroundColorSet) return getQrBackgroundColor();
        return qrFunctionPatternBackgroundColor;
    }

    public void setQrFunctionPatternBackgroundColor(int color) {
        this.qrFunctionPatternBackgroundColorSet = true;
        this.qrFunctionPatternBackgroundColor = color;
    }

    public int getQrPatternColor() {
        return qrPatternColor;
    }

    public void setQrPatternColor(int color) {
        this.qrPatternColor = color;
    }

    public int getQrEmptyPatternColor() {
        if (!qrEmptyPatternColorSet) return getQrBackgroundColor();
        return qrEmptyPatternColor;
    }

    public void setQrEmptyPatternColor(int qrEmptyPatternColor) {
        this.qrEmptyPatternColorSet = true;
        this.qrEmptyPatternColor = qrEmptyPatternColor;
    }

    public int getDotSize() {
        return dotSize;
    }

    public void setDotSize(int dotSize) {
        this.dotSize = dotSize;
    }

    @NotNull
    public Canvas<T> build() {
        final int backgroundColor = getQrBackgroundColor();
        final int qrPatternColor = getQrPatternColor();
        final int qrEmptyPatternColor = getQrEmptyPatternColor();
        final int contentSize = qrData.getSize() * scale;
        final int outputSize = contentSize + padding * 2;
        // Draw background
        Canvas<T> result;
        if (background == null) {
            result = platform.createImage(outputSize, outputSize, padding, backgroundColor);
        } else {
            result = platform.createScaled(background, outputSize, outputSize, padding, backgroundColor);
        }
        // Draw QR code dots
        final int dotSize = getDotSize();
        final int dotPos = (scale - dotSize) / 2;
        for (int x = 0; x < contentSize; x++) {
            for (int y = 0; y < contentSize; y++) {
                if (x % scale != dotPos || y % scale != dotPos) continue;
                final int row = y / scale, col = x / scale;
                drawDot(result, x + padding, y + padding, qrData.get(col, row) ? qrPatternColor : qrEmptyPatternColor);
            }
        }
        // Draw function patterns
        drawFunctionPatterns(result);
        return result;
    }

    private void drawDot(@NotNull Canvas<T> target, int x, int y, int color) {
        final int dotSize = getDotSize();
        for (int j = x; j < x + dotSize; j++) {
            for (int k = y; k < y + dotSize; k++) {
                target.drawDot(j, k, dotSize, color);
            }
        }
    }

    private void drawFunctionPatterns(@NotNull Canvas<T> target) {
        // Draw 3 position patterns (all corners except bottom right; overwrites some timing modules)
        // Top left
        drawPositionPattern(target, 0, 0);
        // Top right
        drawPositionPattern(target, qrData.getSize() - POSITION_PATTERN_CONTENT_SIZE, 0);
        // Bottom left
        drawPositionPattern(target, 0, qrData.getSize() - POSITION_PATTERN_CONTENT_SIZE);

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

    private void drawPositionPattern(@NotNull Canvas<T> target, int qrX, int qrY) {
        final int patternPad = (POSITION_PATTERN_SIZE - POSITION_PATTERN_CONTENT_SIZE) / 2;
        final int patternColor = this.getQrPatternColor();
        final int patternBackgroundColor = getQrFunctionPatternBackgroundColor();
        for (int x = 0; x < POSITION_PATTERN_SIZE; x++) {
            for (int y = 0; y < POSITION_PATTERN_SIZE; y++) {
                final int row = x - patternPad, col = y - patternPad;
                if (row < 0 || col < 0 || row >= POSITION_PATTERN_CONTENT_SIZE || col >= POSITION_PATTERN_CONTENT_SIZE) {
                    target.drawDot(padding + (qrX + row) * scale, padding + (qrY + col) * scale, scale,
                            patternBackgroundColor);
                } else {
                    boolean dot = POSITION_PATTERN_CONTENT[row][col];
                    target.drawDot(padding + (qrX + row) * scale, padding + (qrY + col) * scale, scale,
                            dot ? patternColor : patternBackgroundColor);
                }
            }
        }

    }

    private void drawAlignmentPattern(@NotNull Canvas<T> target, int qrX, int qrY) {
        final int patternPad = (ALIGNMENT_PATTERN_SIZE - ALIGNMENT_PATTERN_CONTENT_SIZE) / 2;
        final int patternColor = this.getQrPatternColor();
        final int patternBackgroundColor = getQrFunctionPatternBackgroundColor();
        for (int x = 0; x < ALIGNMENT_PATTERN_SIZE; x++) {
            for (int y = 0; y < ALIGNMENT_PATTERN_SIZE; y++) {
                final int row = x - patternPad, col = y - patternPad;
                if (row < 0 || col < 0 || row >= ALIGNMENT_PATTERN_CONTENT_SIZE || col >= ALIGNMENT_PATTERN_CONTENT_SIZE) {
                    target.drawDot(padding + (qrX + row) * scale, padding + (qrY + col) * scale, scale,
                            patternBackgroundColor);
                } else {
                    boolean dot = ALIGNMENT_PATTERN[row][col];
                    target.drawDot(padding + (qrX + row) * scale, padding + (qrY + col) * scale, scale,
                            dot ? patternColor : patternBackgroundColor);
                }
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
