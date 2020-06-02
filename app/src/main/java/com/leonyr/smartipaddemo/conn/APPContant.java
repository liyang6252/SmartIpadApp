package com.leonyr.smartipaddemo.conn;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019-07-06
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class APPContant {

    public static final String addrIp = "192.168.0.119";
    public static final int addrPort = 56168;

    public static final String SP_PEN_COLOR = "sp_pen_color";
    public static final String SP_PEN_WIDTH = "sp_pen_width";

    public static final String USB_COMMAND_TAG = "usb_command_tag";

    public static final int SERVER_PORT = 10086;
    public static final String CONNECT = "CONNECT";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String TAKEPHOTO = "TAKEPHOTO";

    public static final int USB_COMMAND_EXCEPTION = 0;
    public static final int USB_COMMAND_SERVICE_START = 1;
    public static final int USB_COMMAND_SERVICE_END = 2;
    public static final int USB_COMMAND_CONNECT = 3;
    public static final int USB_COMMAND_DISCONNECT = 4;
    public static final int USB_COMMAND_TAKEPHOTO = 5;

    public static final String USB_NOTIFY_TAKE_PHOTO = "USB_NOTIFY_TAKE_PHOTO";//拍照
    public static final String USB_NOTIFY_TAKE_MOVIE = "USB_NOTIFY_TAKE_MOVIE";//视频
    public static final String USB_NOTIFY_TAKE_AUDIO = "USB_NOTIFY_TAKE_AUDIO";//录音
    public static final String USB_NOTIFY_PLAY_AUDIO = "USB_NOTIFY_PLAY_AUDIO";//录音播放
    public static final String USB_NOTIFY_TAKE_FINGER_PRINT = "USB_NOTIFY_TAKE_FINGER_PRINT";//静脉指纹识别
    public static final String USB_NOTIFY_TAKE_FINGER = "USB_NOTIFY_TAKE_FINGER";//静脉指纹识别（APP导出）
    public static final String USB_NOTIFY_DOWNLOAD_FINGER = "USB_NOTIFY_DOWNLOAD_FINGER";//宿主机批量传递静脉指纹识别（宿主机导入）
    public static final String USB_NOTIFY_TAKE_PASSWORD = "USB_NOTIFY_TAKE_PASSWORD";//密码口令
    public static final String USB_NOTIFY_TAKE_SIGNATURE = "USB_NOTIFY_TAKE_SIGNATURE";//手写签名
    public static final String USB_NOTIFY_FILE_SIGNER = "USB_NOTIFY_FILE_SIGNER";//文件签批
    public static final String USB_NOTIFY_TAKE_SETTING = "USB_NOTIFY_TAKE_SETTING";//软件设置
    public static final String USB_NOTIFY_TAKE_NFC = "USB_NOTIFY_TAKE_NFC";//NFC打卡
    public static final String USB_NOTIFY_TAKE_CLEAN = "USB_NOTIFY_TAKE_CLEAN";//清理缓存
    public static final String USB_NOTIFY_DOWNLOAD_DATA = "USB_NOTIFY_DOWNLOAD_DATA";//从宿主机下载数据
    public static final String USB_NOTIFY_INSTALL_APP = "USB_NOTIFY_INSTALL_APP";//宿主机提示安装更新包
    public static final String USB_NOTIFY_TAKE_LIGHT = "USB_NOTIFY_TAKE_LIGHT";//台灯功能


    public static final String DATE_CHANGED = "date_changed";//系统日期发生变化

    //sp key
    public static final String SP_SWITCH_FINGER_PRINT = "sp_switch_finger_print";//静脉识别开关
    public static final String SP_SWITCH_FINGER_PRINT_VOICE = "sp_switch_finger_print_voice";//静脉识别开关
    public static final String SP_SWITCH_CAMERA_FLASHLIGHT = "sp_switch_camera_flashlight";//摄像头闪光灯开关

}
