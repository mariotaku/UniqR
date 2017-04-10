package org.mariotaku.uniqr;

import org.jetbrains.annotations.NotNull;

/**
 * Created by mariotaku on 2017/4/10.
 */
public interface Platform<T> {

    @NotNull
    Canvas<T> createImage(int width, int height);

    @NotNull
    Canvas<T> createImage(@NotNull T input);

    @NotNull
    Canvas<T> createScaled(@NotNull T input, int width, int height);

}
