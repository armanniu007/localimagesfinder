package com.bigniu.localimagesfinder.loader;

import android.graphics.Bitmap;

/**
 * Created by bigniu on 16/9/15.
 */
public interface Cache {
    void put(String pImagePath, Bitmap pBitmap);
    
    Bitmap getImage(String pImagePath);

    void clear();

    void remove(String pImagePath);
}
