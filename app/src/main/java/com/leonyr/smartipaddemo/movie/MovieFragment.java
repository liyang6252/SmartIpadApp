package com.leonyr.smartipaddemo.movie;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bumptech.glide.Glide;
import com.leonyr.lib.utils.FileUtil;
import com.leonyr.lib.utils.IntentUtil;
import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.ToastUtil;
import com.leonyr.lib.utils.UriUtil;
import com.leonyr.mvvm.act.Common;
import com.leonyr.mvvm.frag.AbBindFragment;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.conn.FileName;
import com.leonyr.smartipaddemo.crop.CameraActivity;
import com.leonyr.smartipaddemo.crop.CropFileAdapter;
import com.leonyr.smartipaddemo.crop.MediaViewModel;
import com.leonyr.smartipaddemo.databinding.FmMovieActionBinding;
import com.leonyr.smartipaddemo.set.SettingFragment;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.parceler.Parcel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.leonyr.smartipaddemo.crop.CropImageFragment.EXTRA_CROPPED_FILE;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019/7/16
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class MovieFragment extends AbBindFragment<MovieViewModel, FmMovieActionBinding> {

    private final int REQUEST_CODE_TAKE_PHOTO = 0x1200; //请求拍照
    private static final int REQUEST_CODE_ACTION_MOVIE = 0x1202;

    private File movieFile;
    private Disposable disposable;

    List<String> fileList = new ArrayList<>();
    MultiTypeAdapter mAdapter;

    ProgressDialog mProgressDialog;

    @Override
    protected int getLayoutResId() {
        return R.layout.fm_movie_action;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        Binding().setFm(this);
        setVModel(LViewModel.create(this, MovieViewModel.class));

        Binding().mediaRl.setLayoutManager(new GridLayoutManager(mCtx, 3));
        mAdapter = new MultiTypeAdapter();
        mAdapter.register(String.class, new CropFileAdapter());
        Binding().mediaRl.setAdapter(mAdapter);
        mAdapter.setItems(fileList);

        getVModel().showProgressDialog.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer progress) {
                if (null == mProgressDialog){
                    mProgressDialog = new ProgressDialog(mCtx);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(false);
                }
                if (progress == 0){
                    mProgressDialog.show();
                }else if (progress == 100){
                    mProgressDialog.dismiss();
                }else {
                    if(null != mProgressDialog && mProgressDialog.isShowing()) {
                        mProgressDialog.setProgress(progress);
                    }
                }
            }
        });

        getVModel().changeFrameHateSuccess.observe(this, new Observer<File>() {
            @Override
            public void onChanged(@Nullable File file) {
                if (null == file || null == movieFile){
                    return;
                }
                if (file != movieFile){
                    movieFile.delete();
                    movieFile = file;
                }
                File newFile = new File(getContext().getExternalCacheDir(), movieFile.getName());
                FileUtil.moveFile(movieFile, newFile);

                movieFile = newFile;

                fileList.add(movieFile.getPath());
                mAdapter.notifyDataSetChanged();
                setMovieFile();
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_movie:
                startVideo(REQUEST_CODE_ACTION_MOVIE);
                break;
            case R.id.action_photo:
                startTakePhoto();
                break;
        }
    }

    /**
     * 调用摄像头并返回存储路径
     */
    private void startTakePhoto() {
        dispose();
        disposable = new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
//                            SettingFragment.BrightControl(true);
                            movieFile = new File(getContext().getExternalCacheDir(), FileName.getPhotoJpgName());
                            /*Intent startCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            Uri uri = UriUtil.getUri(mCtx, movieFile);
                            startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            if (startCameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
                                startActivityForResult(startCameraIntent, REQUEST_CODE_TAKE_PHOTO);
                            }*/

                            Intent startCameraIntent = new Intent(mCtx, CameraActivity.class);
                            startCameraIntent.putExtra(CameraActivity.OUTPUT, movieFile.getPath());
                            startCameraIntent.putExtra(CameraActivity.CAMERA_TYPE, CameraActivity.TYPE_PHOTO);
                            if (startCameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
                                startActivityForResult(startCameraIntent, REQUEST_CODE_TAKE_PHOTO);
                            }
                        } else {
                            ToastUtil.showToast(getContext(), "权限不足");
                        }
                    }
                });

    }

    /**
     * 启动相机，创建文件，并要求返回uri
     */
    private void startVideo(int requestCode) {
        dispose();
        disposable = new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Intent intent = new Intent();
                            //指定动作，启动相机
                            intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);

                            createVideoFile(); //创建文件
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加权限
                            Uri uri = UriUtil.getUri(mCtx, movieFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//将uri加入到额外数据

                            startActivityForResult(intent, requestCode);//启动相机并要求返回结果

                            /*movieFile = new File(getContext().getExternalFilesDir("cache"), FileName.getMovieName());//创建图片文件
                            Intent startCameraIntent = new Intent(mCtx, CameraActivity.class);
                            startCameraIntent.putExtra(CameraActivity.OUTPUT, movieFile.getPath());
                            startCameraIntent.putExtra(CameraActivity.CAMERA_TYPE, CameraActivity.TYPE_RECORD);
                            if (startCameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
                                startActivityForResult(startCameraIntent, requestCode);
                            }*/
                        } else {
                            ToastUtil.showToast(getContext(), "权限不足");
                        }
                    }
                });
    }

    private void createVideoFile() {
        movieFile = new File(getContext().getExternalFilesDir("cache"), FileName.getMovieName());//创建图片文件
        movieFile.getParentFile().mkdirs();//按设置好的目录层级创建
        movieFile.setWritable(true);//不加这句会报Read-only警告。且无法写入SD
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
//            SettingFragment.BrightControl(false);
        }
        if (resultCode != Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_ACTION_MOVIE){
                if (movieFile.exists()){
                    movieFile.delete();
                }
            }
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_TAKE_PHOTO:
                FileName.createOverFile(mCtx);
                String filePath = data.getStringExtra(CameraActivity.OUTPUT);
                fileList.add(filePath);
                mAdapter.notifyDataSetChanged();
                setMovieFile();
                break;
            case REQUEST_CODE_ACTION_MOVIE:
                //将生成的文件移动到指定的文件夹，并删除旧文件
                File newFile = new File(getContext().getExternalCacheDir(), movieFile.getName());
                FileUtil.moveFile(movieFile, newFile);

                movieFile = newFile;

                fileList.add(movieFile.getPath());
                mAdapter.notifyDataSetChanged();
                setMovieFile();
//                if (null != mProgressDialog){
//                    mProgressDialog.setProgress(0);
//                }
//                getVModel().ChangeFrameVideoInfo(movieFile);
                break;
        }
    }

    private void setMovieFile() {
        StringBuffer buffer = new StringBuffer();
        for (String item : fileList) {
            buffer.append(item);
            buffer.append(",");
        }
        MediaViewModel.moviePath.setValue(buffer.toString());
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
    public static class MovieType implements Common.Type {
        @NonNull
        @Override
        public AbBindFragment newFragment() {
            return new MovieFragment();
        }

        @NonNull
        @Override
        public String getTag() {
            return MovieFragment.class.getSimpleName();
        }
    }
}
