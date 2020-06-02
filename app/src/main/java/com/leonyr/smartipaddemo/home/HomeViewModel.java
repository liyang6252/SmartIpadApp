package com.leonyr.smartipaddemo.home;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leonyr.lib.Utils;
import com.leonyr.lib.utils.ApkUtil;
import com.leonyr.lib.utils.AssetsUtil;
import com.leonyr.lib.utils.DataCleanManager;
import com.leonyr.lib.utils.FileUtil;
import com.leonyr.lib.utils.IntentUtil;
import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.ToastUtil;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.InitApplication;
import com.leonyr.smartipaddemo.conn.FileName;
import com.leonyr.smartipaddemo.db.DBUtil;
import com.leonyr.smartipaddemo.db.FingerEntityDao;
import com.leonyr.smartipaddemo.finger.FingerEntity;
import com.sd.tgfinger.api.TGAPI;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HomeViewModel extends LViewModel {

    public MutableLiveData<List<HomeModel>> FuncList = new MutableLiveData<>();

    public HomeViewModel(@NonNull Context mCtx) {
        super(mCtx);
        InitApplication.getTGAPI(mCtx).readDataFromHost(tgHandler, InitApplication.getTGAPI(mCtx).moniExter3Path);
    }

    public void loadData() {
        List<HomeModel> result = null;
        try {
            result = new Gson().fromJson(AssetsUtil.getStringByAssets(getContext(), "funcs.json"), new TypeToken<List<HomeModel>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != result && !result.isEmpty()) {
            Collections.sort(result);
            FuncList.setValue(result);
        }
    }

    public String getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }


    public void goToCalendar(Context c) {
        PackageManager pm = c.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
        ResolveInfo Resolvebest = null;
        for (final ResolveInfo info : list) {
            if (info.activityInfo.packageName.endsWith(".calendar"))
                Resolvebest = info;
        }
        if (Resolvebest != null) {
            intent.setClassName(Resolvebest.activityInfo.packageName,
                    Resolvebest.activityInfo.name);
            c.startActivity(intent);
        }
    }

    //将宿主机上传的数据写入本地静脉识别模块中
    public void readDownloadData() {
        File file = getContext().getExternalFilesDir("download");
        if (file != null) {
            File[] dataFiles = file.listFiles();
            LogUtil.e(dataFiles.toString());
            if (null == dataFiles || dataFiles.length == 0) {
                ToastUtil.showToast(getContext(), "暂无可导入数据");
                return;
            }
            for (File f : dataFiles) {
                if (f.getName().endsWith(".bat")) {
                    byte[] data = FileUtil.readFile4Bytes(f);
                    try {
                        String path = InitApplication.getTGAPI(getContext()).moniExter3Path + File.separator + FileName.getBatName();
                        InitApplication.getTGAPI(getContext()).saveDataToHost(tgHandler, data, path);

                        //存入数据库
                        FingerEntity entity = new FingerEntity();
                        entity.setUserId(System.currentTimeMillis() + "");
                        entity.setFeature(data);
                        DBUtil.getDBSession().insertOrReplace(entity);

                    } catch (Exception e) {
                        LogUtil.e("readDownloadData", e);
                    } finally {
                        f.delete();
                    }
                }
            }
            ToastUtil.showToast(getContext(), "导入完成");
        }
    }

    //将APP指静脉数据导出
    public void exportFingerData() {
        DataCleanManager.clearAllCache(getContext());
        QueryBuilder<FingerEntity> builder = DBUtil.getDBSession().getFingerEntityDao().queryBuilder();
        List<FingerEntity> fingerEntities = builder.list();
        if (fingerEntities.size() <= 0) {
            ToastUtil.showToast(getContext(), "暂无可导出数据");
        } else {
            for (FingerEntity item : fingerEntities) {
                OutputStream os = null;
                try {
                    File file = new File(getContext().getExternalCacheDir() + File.separator + item.getUserId());
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    os = new FileOutputStream(file.getPath());
                    os.write(item.getFeature(), 0, item.getFeature().length);
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void playAudioMedia() {
        File file = getContext().getExternalFilesDir("download");
        if (file != null) {
            File[] dataFiles = file.listFiles();
            if (null == dataFiles || dataFiles.length == 0) {
                ToastUtil.showToast(getContext(), "暂无可导入数据");
                return;
            }
            for (File f : dataFiles) {
                if (f.getName().endsWith(".mp3") || f.getName().endsWith(".amr")) {
                    Uri uri = Uri.parse(f.getAbsolutePath());
                    playMp3(getContext(), uri);
                    return;
                }
            }
        }
    }

    private static MediaPlayer mediaPlayer;

    //播放指定Uri的音频文件
    public static void playMp3(Context ctx, Uri uri) {
        if (null != mediaPlayer && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(ctx, uri);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                File file = new File(uri.getPath());
                if (file.exists()){
                    file.delete();
                }
            }
        });
    }

    //文件签批
    public String signFile(){
        File file = getContext().getExternalFilesDir("download");
        if (file != null) {
            File[] dataFiles = file.listFiles();
            if (null == dataFiles || dataFiles.length == 0) {
                ToastUtil.showToast(getContext(), "暂无可导入数据");
                return "";
            }
            for (File f : dataFiles) {
                if (f.getName().endsWith(".jpg") || f.getName().endsWith(".png")) {
                    return f.getPath();
                }
            }
        }
        return "";
    }

    //将宿主机下载的app安装升级
    public void installApp() {
        File file = getContext().getExternalFilesDir("install");
        if (file != null) {
            File[] dataFiles = file.listFiles();
            if (null == dataFiles || dataFiles.length == 0) {
                ToastUtil.showToast(getContext(), "暂无更新安装包");
                return;
            } else if (dataFiles.length > 1) {
                ToastUtil.showToast(getContext(), "安装包多于1个");
                return;
            } else {

                try {
                    File apkFile = dataFiles[0];
                    if (apkFile.getName().endsWith(".apk") && apkFile.exists()) {
                        ApkUtil.installApk(getContext(), apkFile, getContext().getPackageName() + ".fileProvider");
                    }
                } catch (Exception e) {
                    LogUtil.e("installApp", e);
                }
            }
        }
    }

    private static Handler tgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TGAPI.WRITE_FILE:
                    //存储数据
                    int saveArg = msg.arg1;
                    if (saveArg == 1) {
                        ToastUtil.showToast(Utils.getApp(), "数据存储成功");
                        //初始化数据，开启连续验证
                    } else if (saveArg == -1) {
                        ToastUtil.showToast(Utils.getApp(), "数据存储失败");
                    }
                    break;
                case TGAPI.READ_FILE:
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        byte[] fingerData = bundle.getByteArray(TGAPI.DATA);
                        if (null != fingerData && fingerData.length > 0) {
                            QueryBuilder<FingerEntity> queryBuilder = DBUtil.getDBSession().getFingerEntityDao().queryBuilder();
                            long count = queryBuilder.where(FingerEntityDao.Properties.Feature.eq(fingerData)).count();
                            if (count == 0) {
                                FingerEntity fingerEntity = new FingerEntity();
                                fingerEntity.setUserId(System.currentTimeMillis() + "");
                                fingerEntity.setFeature(fingerData);
                                DBUtil.getDBSession().insertOrReplace(fingerEntity);
                            }
                        }
                    }
                    break;
            }
        }
    };

}
