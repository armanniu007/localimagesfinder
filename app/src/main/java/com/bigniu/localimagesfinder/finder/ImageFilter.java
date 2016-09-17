package com.bigniu.localimagesfinder.finder;

public interface ImageFilter {
    LocalImage filter(String path, long size);
}