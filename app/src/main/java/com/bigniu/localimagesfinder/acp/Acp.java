package com.bigniu.localimagesfinder.acp;

import android.content.Context;
import android.util.Log;

/**
 * Created by hupei on 2016/4/26.
 */
public class Acp {

    private static final String TAG = "Acp";
    private static Acp mInstance;
    private AcpManager mAcpManager;

    public static Acp getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("Acp is not init");
        }
        return mInstance;
    }

    public static void init(Context context) {
        if (mInstance != null) {
            try {
                throw new IllegalStateException("The Acp has already been registered!!");
            } catch (IllegalStateException e) {
                Log.e(TAG, "init: ", e);
            }
        } else {
            mInstance = new Acp(context);
        }
    }

    private Acp(Context context) {
        mAcpManager = new AcpManager(context.getApplicationContext());
    }

    /**
     * 开始请求
     *
     * @param options
     * @param acpListener
     */
    public void request(AcpOptions options, AcpListener acpListener) {
        if (options == null) new NullPointerException("AcpOptions is null...");
        if (acpListener == null) new NullPointerException("AcpListener is null...");
        mAcpManager.request(options, acpListener);
    }

    AcpManager getAcpManager() {
        return mAcpManager;
    }
}
