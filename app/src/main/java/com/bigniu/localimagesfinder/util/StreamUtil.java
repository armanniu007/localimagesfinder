package com.bigniu.localimagesfinder.util;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

/**
 * Created by bigniu on 16/9/16.
 */
public class StreamUtil {
    public static void flushStream(Flushable pFlushable) {
        try {
            pFlushable.flush();
        } catch (IOException pE) {
            pE.printStackTrace();
        }
    }

    public static void closeStream(Closeable pCloseable) {
        try {
            pCloseable.close();
        } catch (IOException pE) {
            pE.printStackTrace();
        }
    }
}
