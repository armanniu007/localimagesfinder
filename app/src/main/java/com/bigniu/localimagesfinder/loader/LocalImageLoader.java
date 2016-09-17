package com.bigniu.localimagesfinder.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.WindowManager;

import com.bigniu.localimagesfinder.finder.LocalImage;
import com.bigniu.localimagesfinder.util.ImageUtil;

/**
 * Created by bigniu on 16/9/15.
 */
public class LocalImageLoader {
    protected int MAX_TASK_NUM = 15;
    private Cache mCache;
    private RealSize mRealSize;
    private LruCache<Object, LoadImageTask> mTagToAsyncTask = new LruCache<Object, LoadImageTask>(MAX_TASK_NUM) {
        @Override
        protected void entryRemoved(boolean evicted, Object key, LoadImageTask oldValue, LoadImageTask newValue) {
            super.entryRemoved(evicted, key, oldValue, newValue);
            if (evicted) {
                oldValue.cancel(true);
            }
        }
    };

    private boolean stopLoader;

    public LocalImageLoader(Context pContext) {
        mCache = new ImageCache(pContext);

        WindowManager wm = (WindowManager) pContext.getSystemService(Context.WINDOW_SERVICE);
        Point mOutSize = new Point();
        wm.getDefaultDisplay().getSize(mOutSize);
        mOutSize.set(mOutSize.x / 2, mOutSize.y / 2);

        mRealSize = new RealSize();
        mRealSize.setWidth(mOutSize.x);
        mRealSize.setHeight(mOutSize.y);
    }

    public LocalImageLoader(Context pContext, RealSize pRealSize) {
        mCache = new ImageCache(pContext);
        this.mRealSize = pRealSize;
    }

    public Cache getCache() {
        return mCache;
    }

    public void setCache(Cache pCache) {
        mCache = pCache;
    }

    public void loadImage(final LocalImage pLocalImage, final OnImageLoadListener pListener, final Object pTag) {

        LoadImageTask loadImageTask = mTagToAsyncTask.get(pTag);

        if (loadImageTask != null && loadImageTask.interecptListener(pListener)) {
            return;
        }

        synchronized (this) {
            if (!stopLoader) {
                loadImageTask = new LoadImageTask(pLocalImage, pListener, pTag);
                mTagToAsyncTask.put(pTag, loadImageTask);
                loadImageTask.execute();
            }
        }
    }

    public void stopLoader() {
        synchronized (this) {
            stopLoader = true;
        }

        mTagToAsyncTask.evictAll();
    }

    public void clearCache() {
        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... params) {
                mCache.clear();
                return null;
            }
        }.execute();
    }

    public static class RealSize {
        private int width;
        private int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int pWidth) {
            width = pWidth;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int pHeight) {
            height = pHeight;
        }
    }

    class LoadImageTask extends AsyncTask<String, String, Bitmap> {
        private LocalImage mLocalImage;
        private OnImageLoadListener mOnImageLoadListener;
        private Object mTag;

        LoadImageTask(LocalImage pLocalImage, OnImageLoadListener pOnImageLoadListener, Object pTag) {
            mLocalImage = pLocalImage;
            mOnImageLoadListener = pOnImageLoadListener;
            mTag = pTag;
        }

        public boolean interecptListener(OnImageLoadListener pOnImageLoadListener) {
            boolean flag;
            synchronized (this) {
                if (flag = mOnImageLoadListener != null) {
                    mOnImageLoadListener = pOnImageLoadListener;
                }
            }
            return flag;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String pImagePath = mLocalImage.getLocalPath();
            Bitmap bitmap = mCache.getImage(pImagePath);
            if (bitmap == null) {
                if ((mLocalImage.getOrientation() / 90) % 2 > 0) {
                    bitmap = ImageUtil.getThumbImage(pImagePath, mRealSize.height, mRealSize.width);
                } else {
                    bitmap = ImageUtil.getThumbImage(pImagePath, mRealSize.width, mRealSize.height);
                }

                if (bitmap != null) {
                    if (mLocalImage.getOrientation() > 0) {
                        Matrix matrix = new Matrix();
                        matrix.preRotate(mLocalImage.getOrientation());
                        Bitmap source = bitmap;
                        bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
                        if (source != bitmap) source.recycle();
                    }
                    mCache.put(pImagePath, bitmap);
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap pBitmap) {
            synchronized (this) {
                if (mOnImageLoadListener != null) {
                    mOnImageLoadListener.onLoad(pBitmap, mTag);
                    mOnImageLoadListener = null;
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            synchronized (this) {
                mOnImageLoadListener = null;
            }
        }
    }
}
