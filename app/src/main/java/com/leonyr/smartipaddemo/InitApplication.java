package com.leonyr.smartipaddemo;

import android.content.Context;
import android.graphics.Color;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.leonyr.lib.utils.SPUtils;
import com.leonyr.smartipaddemo.conn.APPContant;
import com.leonyr.smartipaddemo.db.DBUtil;
import com.leonyr.view.linepath.LinePathView;
import com.sd.tgfinger.api.TGAPI;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019-07-11
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class InitApplication extends MultiDexApplication {

    public static int PEN_COLOR = Color.BLACK;
    public static  int PEN_WIDTH = 15;

    static TGAPI tgapi;

    public static TGAPI getTGAPI(Context c) {
        if (null == tgapi) {
            tgapi = TGAPI.getTGAPI();
            tgapi.init(c, null);
        }
        return tgapi;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DBUtil.init(this);

        getTGAPI(this).startDevService(this);
//        FileUtil.copyAssetToSDCard(getAssets(), "pdf.p12", getExternalFilesDir("file") + File.separator + "pdf.p12");


        initDir();

        PEN_WIDTH = SPUtils.getInstance().getInt(APPContant.SP_PEN_WIDTH, 15);
        PEN_COLOR = SPUtils.getInstance().getInt(APPContant.SP_PEN_COLOR, Color.BLACK);
    }

    private void initDir() {
        this.getExternalCacheDir();
        this.getExternalFilesDir("download");

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        getTGAPI(this).unbindDevService(this);
    }
}
