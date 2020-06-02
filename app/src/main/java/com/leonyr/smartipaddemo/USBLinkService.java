package com.leonyr.smartipaddemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.leonyr.lib.utils.LogUtil;

public class USBLinkService extends Service {

    private static final String TAG = "USBLinkService";

    public static boolean isRuning = false;

    @Override
    public void onCreate() {
        LogUtil.e(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e(TAG, "onStartCommand");
        isRuning = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.e(TAG, "onBind");
        return mBinder;
    }

    public USBLinkBinder mBinder = new USBLinkBinder();

    public class USBLinkBinder extends Binder {

        IUSBReceiveListener listener;

        public void uploadInfo(int type) {
            LogUtil.e(TAG, "upload Info");

        }

        public void sendReceiveInfo(String info){
            if (null != listener){
                listener.receiveInfo("receive info");
            }
        }

        public void setListener(IUSBReceiveListener listener) {
            this.listener = listener;
        }
    }

    public interface IUSBReceiveListener {
        void receiveInfo(String info);
    }

    @Override
    public void onDestroy() {
        LogUtil.e(TAG, "onDestroy");
        isRuning = false;
        super.onDestroy();
    }
}
