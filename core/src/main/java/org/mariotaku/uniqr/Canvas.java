package org.mariotaku.uniqr;

import org.jetbrains.annotations.NotNull;

/**
 * Created by mariotaku on 2017/4/10.
 */
public interface Canvas<T> {
    int getWidth();

    int getHeight();

    void drawDot(int l, int t, int size, int color);

    @NotNull
    T produceResult();

    void free();
}
