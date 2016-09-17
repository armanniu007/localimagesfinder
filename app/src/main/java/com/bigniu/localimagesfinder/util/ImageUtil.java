package com.bigniu.localimagesfinder.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

/**
 * Created by bigniu on 16/5/20.
 */
public class ImageUtil {

    private static final String TAG = "ImageUtil";

    /**
     * 获取缩略版的图片，更加节省内存
     */
    public static Bitmap getThumbImage(String path, int pRealWidth, int pRealHeight) {

        BitmapFactory.Options option = new BitmapFactory.Options();

        option.inPreferredConfig = Bitmap.Config.RGB_565;

        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, option);
        option.inSampleSize = getScale(option, pRealWidth, pRealHeight);
        option.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, option);
    }

    /**
     * 根据需求宽高和图片宽高的对比，获取图片缩放比例
     *
     * @param options     包含了图片的宽高等数据
     * @param pRealWidth  需求的宽
     * @param pRealHeight 需求的高
     * @return 缩放比例
     */
    public static int getScale(BitmapFactory.Options options, int pRealWidth, int pRealHeight) {
        int inSampleSize = 1;
        if (pRealWidth <= 0 && pRealHeight <= 0) {
            return inSampleSize;
        }

        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        if (bitmapWidth > pRealWidth || bitmapHeight > pRealHeight) {
            int widthScale = pRealWidth > 0 ? Math.round((float) bitmapWidth / (float) pRealWidth) : Integer.MAX_VALUE;
            int heightScale = pRealHeight > 0 ? Math.round((float) bitmapHeight / (float) pRealHeight) : Integer.MAX_VALUE;
            inSampleSize = Math.min(widthScale, heightScale);
        }

        if (inSampleSize < 1) {
            inSampleSize = 1;
        }

        if (inSampleSize > 1) {
            double pow = Math.pow((double) inSampleSize, (double) 1 / 2);
            Log.i(TAG, "getScale: pow = " + pow);
            inSampleSize = (int) Math.pow((double) 2, Math.ceil(pow));
        }

        Log.i(TAG, "getScale: imageW = " + bitmapWidth + "\nimageH = " + bitmapHeight + "\n inSampleSize = " + inSampleSize);

        return inSampleSize;
    }

    public static int getImageOrientation(String pPath) {
        try {
            ExifInterface exifInterface = new ExifInterface(pPath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                default:
                    return 0;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }
}
