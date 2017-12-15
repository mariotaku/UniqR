package org.mariotaku.uniqr;

import org.jetbrains.annotations.NotNull;

/**
 * Created by mariotaku on 2017/4/10.
 */
public interface Platform<T> {

    @NotNull
    Canvas<T> createImage(int width, int height, int padding, int backgroundColor);

    @NotNull
    Canvas<T> createScaled(@NotNull T input, int width, int height, int padding, int backgroundColor);

}
