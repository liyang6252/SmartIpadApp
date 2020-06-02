package com.leonyr.smartipaddemo.movie;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.leonyr.lib.utils.IntentUtil;
import com.leonyr.lib.utils.ToastUtil;
import com.leonyr.lib.utils.UriUtil;
import com.leonyr.mvvm.act.Common;
import com.leonyr.mvvm.frag.AbBindFragment;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.conn.FileName;
import com.leonyr.smartipaddemo.crop.MediaViewModel;
import com.leonyr.smartipaddemo.databinding.FmAudioActionBinding;
import com.leonyr.smartipaddemo.home.HomeViewModel;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.parceler.Parcel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019/7/16
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class AudioFragment extends AbBindFragment<LViewModel, FmAudioActionBinding> {

    private static final int REQUEST_CODE_AUDIO = 0x1203;

    private Disposable disposable;
    private File audioFile;

    @Override
    protected int getLayoutResId() {
        return R.layout.fm_audio_action;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        Binding().setFm(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_audio:
                startRecordAudio(REQUEST_CODE_AUDIO);
                break;
            case R.id.action_audio_play:
                if (null != audioFile) {
                    HomeViewModel.playMp3(mCtx, Uri.fromFile(audioFile));
//                    IntentUtil.playMp3(mCtx, audioFile.getPath());
                }
                break;
        }
    }

    private void startRecordAudio(int requestCode) {
        disposable = new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Intent intent = new Intent();
                            intent.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                            createAudioFile();
                            //添加权限
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri uri = UriUtil.getUri(mCtx, audioFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                            startActivityForResult(intent, requestCode);
                        } else {
                            ToastUtil.showToast(getContext(), "权限不足");
                        }
                    }
                });
    }

    private void createAudioFile() {
        audioFile = new File(getContext().getExternalCacheDir(), FileName.getAudioName());//创建图片文件
        audioFile.getParentFile().mkdirs();//按设置好的目录层级创建
        audioFile.setWritable(true);//不加这句会报Read-only警告。且无法写入SD
    }

    /**
     * 保存音频到SD卡的指定位置
     *
     * @param path 录音文件的路径
     */
    private void saveVoiceToSD(String path) {
        //创建输入输出
        InputStream isFrom = null;
        OutputStream osTo = null;
        try {
            //设置输入输出流
            isFrom = new FileInputStream(path);
            osTo = new FileOutputStream(audioFile);
            byte bt[] = new byte[1024];
            int len;
            while ((len = isFrom.read(bt)) != -1) {
                Log.d(TAG, "len = " + len);
                osTo.write(bt, 0, len);
            }
            Log.d(TAG, "保存录音完成。");
            MediaViewModel.audioPath.setValue(audioFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (osTo != null) {
                try {
                    //不管是否出现异常，都要关闭流
                    osTo.close();
                    Log.d(TAG, "关闭输出流");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isFrom != null) {
                try {
                    isFrom.close();
                    Log.d(TAG, "关闭输入流");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try{
                new File(path).delete();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过Uri，获取录音文件的路径（绝对路径）
     *
     * @param uri 录音文件的uri
     * @return 录音文件的路径（String）
     */
    private String getAudioFilePathFromUri(Uri uri) {
        Cursor cursor = mCtx.getContentResolver()
                .query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        String temp = cursor.getString(index);
        cursor.close();
        return temp;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_AUDIO) {
            Binding().actionAudioPlay.setVisibility(View.VISIBLE);
            String audio = getAudioFilePathFromUri(data.getData());
            saveVoiceToSD(audio);
        }
    }

    private void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onDestroy() {
        dispose();
        super.onDestroy();
    }

    @Parcel
    public static class AudioType implements Common.Type {
        @NonNull
        @Override
        public AbBindFragment newFragment() {
            return new AudioFragment();
        }

        @NonNull
        @Override
        public String getTag() {
            return AudioFragment.class.getSimpleName();
        }
    }

}
