package com.bigniu.localimagesfinder;

import android.app.Application;

import com.bigniu.localimagesfinder.acp.Acp;

/**
 * Created by bigniu on 16/9/17.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Acp.init(getApplicationContext());
    }
}
