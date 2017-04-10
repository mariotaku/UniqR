package org.mariotaku.uniqr;

/**
 * Created by mariotaku on 2017/4/10.
 */
public interface Canvas<T> {
    int getWidth();

    int getHeight();

    int getPixel(int x, int y);

    void setPixel(int x, int y, int pixel);

    T produceResult();

    void free();
}
