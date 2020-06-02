package com.leonyr.smartipaddemo.movie;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.leonyr.lib.utils.LogUtil;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.conn.FileName;

import java.io.File;

//import VideoHandle.EpEditor;
//import VideoHandle.EpVideo;
//import VideoHandle.OnEditorListener;

public class MovieViewModel extends LViewModel {

    public static MutableLiveData<Integer> showProgressDialog = new MutableLiveData<>();
    public static MutableLiveData<File> changeFrameHateSuccess = new MutableLiveData<>();
//    EpVideo mEpVideo;

    public MovieViewModel(@NonNull Context ctx) {
        super(ctx);
    }

    //将生成的视屏video文件属性修改，帧率改为30fps
    public void ChangeFrameVideoInfo(File videoFile) {
        if (!videoFile.exists()) {
            return;
        }

        File outPutFile = new File(getContext().getExternalFilesDir("cache"), FileName.getMovieName());
        /*mEpVideo = new EpVideo(videoFile.getPath());

        EpEditor.OutputOption option = new EpEditor.OutputOption(outPutFile.getPath());
        option.frameRate = 30;
        showProgressDialog.setValue(0);
        EpEditor.exec(mEpVideo, option, new OnEditorListener() {
            @Override
            public void onSuccess() {
                LogUtil.e("Video success");
                setMessage(0, 100);
                setMessage(1, outPutFile);
            }

            @Override
            public void onFailure() {
                LogUtil.e("Video file");
                setMessage(0, 100);
                setMessage(1, videoFile);
            }

            @Override
            public void onProgress(float progress) {
                LogUtil.e("Video progress: " + (progress * 100));
                if (progress < 1) {
                    setMessage(0, (int) (progress * 100));
                }
            }
        });*/
    }


    private void setMessage(int what, Object obj) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }

    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showProgressDialog.setValue((int) msg.obj);
                    break;
                case 1:
                    changeFrameHateSuccess.setValue((File) msg.obj);
                    break;
            }
        }
    };

}
