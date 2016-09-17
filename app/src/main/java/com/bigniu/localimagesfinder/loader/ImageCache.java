package com.bigniu.localimagesfinder.loader;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by bigniu on 16/9/16.
 */
public class ImageCache implements Cache {
    private LocalCache mLocalCache;
    private MemoryCache mMemoryCache;

    public ImageCache(Context pContext) {
        mLocalCache = new LocalCache(pContext);
        mMemoryCache = new MemoryCache(pContext);
    }

    @Override
    public void put(String pImagePath, Bitmap pBitmap) {
        mMemoryCache.put(pImagePath, pBitmap);
        mLocalCache.put(pImagePath, pBitmap);
    }

    @Override
    public Bitmap getImage(String pImagePath) {
        Bitmap bitmap = mMemoryCache.getImage(pImagePath);
        if (null == bitmap) {
            bitmap = mLocalCache.getImage(pImagePath);
            if (bitmap != null) {
                mMemoryCache.put(pImagePath, bitmap);
            }
        }
        return bitmap;
    }

    @Override
    public void clear() {
        mMemoryCache.clear();
        mLocalCache.clear();
    }

    @Override
    public void remove(String pImagePath) {
        mMemoryCache.remove(pImagePath);
        mLocalCache.remove(pImagePath);
    }
}
