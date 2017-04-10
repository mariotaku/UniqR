package org.mariotaku.uniqr;

/**
 * Created by mariotaku on 2017/4/10.
 */
public interface QrData {
    int getSize();

    int getVersion();

    boolean get(int x, int y);
}
