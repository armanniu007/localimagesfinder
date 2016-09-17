package com.bigniu.localimagesfinder.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by bigniu on 16/9/15.
 */
public class MemoryCache implements Cache {

    private static final String TAG = "MemoryCache";
    LruCache<String, Bitmap> mBitmapLruCache;

    public MemoryCache(Context pContext) {
        long freeMemory = Runtime.getRuntime().freeMemory() >> 10;
        long totalMemory = Runtime.getRuntime().totalMemory() >> 10;
        long maxMemory = Runtime.getRuntime().maxMemory() >> 10;

        Log.i(TAG, "MemoryCache: free = " + freeMemory + "\ntotal = " + totalMemory + "\nmax = " + maxMemory);
        mBitmapLruCache = new LruCache<String, Bitmap>((int) (maxMemory / 8)) {
            @Override
            protected int sizeOf(String key, Bitmap value) {

                int byteCount;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    byteCount = value.getAllocationByteCount();
//                } else {
//                    byteCount = value.getByteCount();
//                }
                byteCount = value.getByteCount();
                byteCount >>= 10;
                Log.i(TAG, "sizeOf: byteCount = " + byteCount);
                return byteCount;
            }
        };
    }

    public void put(String pImagePath, Bitmap pBitmap) {
        mBitmapLruCache.put(pImagePath, pBitmap);
    }

    @Override
    public Bitmap getImage(String pImagePath) {
        return mBitmapLruCache.get(pImagePath);
    }

    @Override
    public void clear() {
        mBitmapLruCache.evictAll();
    }

    @Override
    public void remove(String pImagePath) {
        mBitmapLruCache.remove(pImagePath);
    }
}
