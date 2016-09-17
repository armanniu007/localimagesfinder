package com.bigniu.localimagesfinder.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.bigniu.localimagesfinder.util.FileUtils;
import com.bigniu.localimagesfinder.util.Md5Util;
import com.bigniu.localimagesfinder.util.StreamUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

/**
 * Created by bigniu on 16/9/15.
 */
public class LocalCache implements Cache {

    private static final String TAG = "LocalCache";
    protected static final String RELEASE = ".release";
    private static final String LOCAL_IMG = ".localImg";

    protected Context mContext;

    public LocalCache(Context pContext) {
        mContext = pContext;
    }

    @Override
    public void put(final String pImagePath, final Bitmap pBitmap) {
        File imageFile = getTempFile(pImagePath);
        File releaseFile = getReleaseFile(pImagePath);
        if (imageFile != null && FileUtils.createNewFile(imageFile)
                && releaseFile != null && (!releaseFile.exists() || releaseFile.delete())) {
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(imageFile);
            } catch (FileNotFoundException pE) {
                pE.printStackTrace();
                return;
            }

            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            pBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            StreamUtil.flushStream(bos);
            StreamUtil.flushStream(outputStream);
            StreamUtil.closeStream(bos);
            StreamUtil.closeStream(outputStream);
            imageFile.renameTo(releaseFile);

        }
    }

    @Override
    public Bitmap getImage(String pImagePath) {
        File releaseFile = getReleaseFile(pImagePath);
        if (releaseFile != null && releaseFile.exists()) {
            FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(releaseFile);
            } catch (FileNotFoundException pE) {
                pE.printStackTrace();
                return null;
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            StreamUtil.closeStream(inputStream);

            return bitmap;
        }
        return null;
    }

    @Override
    public void clear() {
        File tempRoot = getTempRoot();
        FileUtils.deleteFileAndChildren(tempRoot, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                Log.i(TAG, "accept: filename = " + filename);
                return filename.endsWith(LOCAL_IMG) || filename.endsWith(RELEASE);
            }
        });
    }

    @Override
    public void remove(final String pImagePath) {

        File tempFile = getTempFile(pImagePath);
        if (tempFile != null) {
            if (tempFile.exists()) {
                tempFile.delete();
            }

            File releaseFile = getReleaseFile(pImagePath);
            if (releaseFile.exists()) {
                releaseFile.delete();
            }
        }
    }

    protected File getTempFile(String pImagePath) {
        return FileUtils.getFile(getTempRoot(), Md5Util.md5(pImagePath) + LOCAL_IMG);
    }

    protected File getReleaseFile(String pImagePath) {
        return FileUtils.getFile(getTempRoot(), Md5Util.md5(pImagePath) + RELEASE);
    }

    private File getTempRoot() {
        return FileUtils.getFile(mContext, "temp");
    }
}
