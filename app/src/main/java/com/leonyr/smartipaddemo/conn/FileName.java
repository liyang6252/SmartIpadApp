package com.leonyr.smartipaddemo.conn;

import android.content.Context;

import com.leonyr.lib.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019/7/26
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class FileName {

    private static final String fileFormat = "%s_%s%s";

    private static String getFileTypeName(String prefix, String name, String type) {
        return String.format(Locale.CHINA, fileFormat, prefix, name, type);
    }
    //设置图片文件名（含后缀），以当前时间的毫秒值为名称
    public static String getPhotoPngName() {
        return getPhotoName(String.valueOf(System.currentTimeMillis()), ".png");
    }
    //设置图片文件名（含后缀），以当前时间的毫秒值为名称
    public static String getPhotoJpgName() {
        return getPhotoName(String.valueOf(System.currentTimeMillis()), ".jpg");
    }

    public static String getPhotoName(String name, String postfix) {
        return getFileTypeName("photo", name, postfix);
    }

    public static String getMovieName() {
        return getMovieName(String.valueOf(System.currentTimeMillis()));
    }

    public static String getMovieName(String name) {
        return getFileTypeName("movie", name, ".mp4");
    }

    public static String getTextName() {
        return getTextName(String.valueOf(System.currentTimeMillis()));
    }

    public static String getTextName(String name) {
        return getFileTypeName("text", name, ".txt");
    }

    public static String getJsonName() {
        return getJsonName(String.valueOf(System.currentTimeMillis()));
    }

    public static String getJsonName(String name) {
        return getFileTypeName("json", name, ".json");
    }

    public static String getAudioName() {
        return getAudioName(String.valueOf(System.currentTimeMillis()));
    }

    public static String getAudioName(String name) {
        return getFileTypeName("audio", name, ".amr");
    }

    public static String getBatName() {
        return getBatName(String.valueOf(System.currentTimeMillis()));
    }

    public static String getBatName(String name) {
        return getFileTypeName("byte", name, ".bat");
    }

    public static String getPdfName() {
        return getPdfName(String.valueOf(System.currentTimeMillis()));
    }

    public static String getPdfName(String name) {
        return getFileTypeName("file", name, ".pdf");
    }

    public static void deleteOverFile(Context ctx){
        File file = new File(ctx.getExternalFilesDir("flag"), "over");
        if (file.exists()){
            file.delete();
        }
    }

    public static void createOverFile(Context ctx){
        File file = new File(ctx.getExternalFilesDir("flag"), "over");
        if (!file.exists()){
            try {
                file.createNewFile();
//                LogUtil.e(file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
