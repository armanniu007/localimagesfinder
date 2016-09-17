package com.bigniu.localimagesfinder.finder;

/**
 * Created by bigniu on 16/9/14.
 */
public class LocalImage {
    
    private String localPath;
    private long size;
    private int orientation;

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String pLocalPath) {
        localPath = pLocalPath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long pSize) {
        size = pSize;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int pOrientation) {
        orientation = pOrientation;
    }
}
