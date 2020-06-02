package com.leonyr.smartipaddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.StringUtil;
import com.leonyr.smartipaddemo.conn.USBCommandUtil;
import com.leonyr.smartipaddemo.conn.APPContant;
import com.leonyr.smartipaddemo.home.HomeActivity;

/**
 * ==============================================================
 * Description:
 * 软件监听系统重启，自动启动
 * <p>
 * Created by leonyr on 2019.06.28
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class BootReceiver extends BroadcastReceiver {

    private final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.e("BootReceiver", action);
        if (StringUtil.isBlank(action)) {
            return;
        }
        switch (action) {
            case Intent.ACTION_DATE_CHANGED:
                USBCommandUtil.sendUSBCommand(APPContant.DATE_CHANGED);
                break;
            case ACTION_BOOT:
                //自启动
                startHomeActivity(context);
                break;
            case "NotifyServiceStart":
                USBCommandUtil.sendUSBCommand(APPContant.USB_NOTIFY_TAKE_PHOTO);
                break;
            case APPContant.USB_NOTIFY_TAKE_PHOTO:
            case APPContant.USB_NOTIFY_TAKE_MOVIE:
            case APPContant.USB_NOTIFY_TAKE_AUDIO:
            case APPContant.USB_NOTIFY_PLAY_AUDIO:
            case APPContant.USB_NOTIFY_TAKE_FINGER_PRINT:
            case APPContant.USB_NOTIFY_TAKE_FINGER:
            case APPContant.USB_NOTIFY_DOWNLOAD_FINGER:
            case APPContant.USB_NOTIFY_TAKE_PASSWORD:
            case APPContant.USB_NOTIFY_TAKE_SIGNATURE:
            case APPContant.USB_NOTIFY_FILE_SIGNER:
            case APPContant.USB_NOTIFY_TAKE_SETTING:
            case APPContant.USB_NOTIFY_TAKE_NFC:
            case APPContant.USB_NOTIFY_TAKE_CLEAN:
            case APPContant.USB_NOTIFY_DOWNLOAD_DATA:
            case APPContant.USB_NOTIFY_INSTALL_APP:
            case APPContant.USB_NOTIFY_TAKE_LIGHT:
                USBCommandUtil.sendUSBCommand(action);
                break;
            default:
                startHomeActivity(context);
                break;
        }
    }

    private void startHomeActivity(Context context) {
        Intent intentMainActivity = new Intent(context, HomeActivity.class);
        intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentMainActivity);
    }
}
