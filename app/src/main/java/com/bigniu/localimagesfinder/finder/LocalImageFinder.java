package com.bigniu.localimagesfinder.finder;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.bigniu.localimagesfinder.util.ImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bigniu on 16/5/18.
 * It can help you find local images
 * <p/>
 * {@link OnImagesFindListener} to get Images and {@link ImageFilter} to filter images you want to get
 */
public class LocalImageFinder {

    private Context mContext;
    private ImageFilter mImageFilter;

    public LocalImageFinder(Context pContext) {
        mContext = pContext.getApplicationContext();
    }

    public ImageFilter getImageFilter() {
        return mImageFilter;
    }

    public void setImageFilter(ImageFilter pImageFilter) {
        mImageFilter = pImageFilter;
    }

    public void execute(OnImagesFindListener pImagesFindListener) {
        new FinderTask(pImagesFindListener).execute();
    }

    class FinderTask extends AsyncTask<Integer, Integer, List<LocalImage>> {
        private OnImagesFindListener mImagesFindListener;

        public FinderTask(OnImagesFindListener pImagesFindListener) {
            mImagesFindListener = pImagesFindListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LocalImage> doInBackground(Integer... params) {
            ContentResolver contentResolver = mContext.getContentResolver();
            Cursor cursor = contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg"},
                    MediaStore.Images.Media.DATE_ADDED);

            if (cursor == null) {
                return null;
            }

            int pathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int sizeIndex = cursor.getColumnIndex(MediaStore.Images.Media.SIZE);


            List<LocalImage> localImageList = new ArrayList<>();

            while (cursor.moveToNext()) {
                String path = cursor.getString(pathIndex);
                long size = cursor.getLong(sizeIndex);
                if (mImageFilter == null) {
                    mImageFilter = new SimpleImageFilter();
                }

                LocalImage localImage = mImageFilter.filter(path, size);

                if (null != localImage) {
                    localImageList.add(localImage);
                }
            }

            cursor.close();

            return localImageList;
        }

        @Override
        protected void onPostExecute(List<LocalImage> pLocalImageList) {
            if (mImagesFindListener != null) {
                mImagesFindListener.onImagesFind(pLocalImageList);
            }
        }
    }

    class SimpleImageFilter implements ImageFilter {

        @Override
        public LocalImage filter(String path, long size) {
            if (TextUtils.isEmpty(path)) {
                return null;
            }

            if (size == 0) {
                File file = new File(path);
                size = file.length();
            }

            if (size == 0) {
                return null;
            }

            int orientation = ImageUtil.getImageOrientation(path);

            LocalImage localImage = new LocalImage();
            localImage.setLocalPath(path);
            localImage.setSize(size);
            localImage.setOrientation(orientation);

            return localImage;
        }
    }
}

