package com.leonyr.smartipaddemo.conn;

import android.content.IntentFilter;

import com.hwangjr.rxbus.RxBus;

import java.util.Locale;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019/7/10
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class USBCommandUtil {

    public static void register(Object o) {
        RxBus.get().register(o);
    }

    public static void sendUSBCommand(String command) {
        RxBus.get().post(APPContant.USB_COMMAND_TAG, command);
    }

    /*public static void sendUSBCommand(String command) {
        RxBus.get().post(command);
    }*/

    public static void unRegister(Object o) {
        RxBus.get().unregister(o);
    }


    public static IntentFilter getCommandFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(APPContant.USB_NOTIFY_TAKE_PHOTO);
        filter.addAction(APPContant.USB_NOTIFY_TAKE_MOVIE);
        filter.addAction(APPContant.USB_NOTIFY_TAKE_AUDIO);
        filter.addAction(APPContant.USB_NOTIFY_PLAY_AUDIO);
        filter.addAction(APPContant.USB_NOTIFY_TAKE_FINGER_PRINT);
        filter.addAction(APPContant.USB_NOTIFY_TAKE_FINGER);
        filter.addAction(APPContant.USB_NOTIFY_DOWNLOAD_FINGER);
        filter.addAction(APPContant.USB_NOTIFY_TAKE_PASSWORD);
        filter.addAction(APPContant.USB_NOTIFY_TAKE_SIGNATURE);
        filter.addAction(APPContant.USB_NOTIFY_FILE_SIGNER);
        filter.addAction(APPContant.USB_NOTIFY_TAKE_SETTING);
        filter.addAction(APPContant.USB_NOTIFY_TAKE_NFC);
        filter.addAction(APPContant.USB_NOTIFY_TAKE_CLEAN);
        filter.addAction(APPContant.USB_NOTIFY_DOWNLOAD_DATA);
        filter.addAction(APPContant.USB_NOTIFY_INSTALL_APP);
        filter.addAction(APPContant.USB_NOTIFY_TAKE_LIGHT);
        return filter;
    }

}
