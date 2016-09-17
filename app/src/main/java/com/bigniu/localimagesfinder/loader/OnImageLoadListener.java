package com.bigniu.localimagesfinder.loader;

import android.graphics.Bitmap;

/**
 * Created by bigniu on 16/9/15.
 */
public interface OnImageLoadListener {
    void onLoad(Bitmap pBitmap, Object pTag);
}
