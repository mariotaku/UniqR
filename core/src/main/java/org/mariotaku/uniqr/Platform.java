package org.mariotaku.uniqr;

/**
 * Created by mariotaku on 2017/4/10.
 */
public interface Platform<T> {

    Canvas<T> createImage(int width, int height);

    Canvas<T> createImage(T input);

    Canvas<T> createScaled(T input, int width, int height);

}
