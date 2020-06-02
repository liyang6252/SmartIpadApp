package com.leonyr.smartipaddemo.sign;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.leonyr.lib.utils.BitmapUtil;
import com.leonyr.lib.utils.FileUtil;
import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.ScreenUtil;
import com.leonyr.lib.utils.StringUtil;
import com.leonyr.lib.utils.ToastUtil;
import com.leonyr.mvvm.act.Common;
import com.leonyr.mvvm.frag.AbBindFragment;
import com.leonyr.mvvm.util.RxSchedulers;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.databinding.FmSignFileBinding;
import com.leonyr.smartipaddemo.set.PDFUtils;
import com.leonyr.smartipaddemo.view.FloatDragView;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019/6/27
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class SignFileFragment extends AbBindFragment<LViewModel, FmSignFileBinding> {

    public static final String RESULT_PATH_SIGN = "result_path_sign";
    private final int REQUEST_CODE_TAKE_SIGN = 0x1202; //请求签名

    private ProgressDialog progressDialog;
    private String filePath;
    FloatDragView dragView;

    private int scrollHeight = 0;

    public static SignFileFragment newInstance(String imagePath) {

        Bundle args = new Bundle();
        args.putString(RESULT_PATH_SIGN, imagePath);

        SignFileFragment fragment = new SignFileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fm_sign_file;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        Binding().setFm(this);
        Bundle bundle = getArguments();
        if (null == bundle) {
            getActivity().onBackPressed();
        }

        filePath = bundle.getString(RESULT_PATH_SIGN);
        Glide.with(mCtx).load("file://" + filePath).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(Binding().fileImage)
        ;
        Binding().fileScroll.setOnScrollListener(new ControlScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                scrollHeight = scrollY;
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.file_sign:
                SignHomeFragment fragment = SignHomeFragment.newInstance();
                fragment.setTargetFragment(this, REQUEST_CODE_TAKE_SIGN);
                fragment.show(getFragmentManager(), SignHomeFragment.class.getSimpleName());
                break;
            case R.id.file_confirm:
                new SignFileAsyncTask().execute("");

                break;
        }
    }

    public class SignFileAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("", "请稍后...");
        }

        @Override
        protected String doInBackground(String... strings) {
            signFileConfirm();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideProgressDialog();
            ToastUtil.showToast(mCtx, "签批完成");
            getActivity().onBackPressed();
        }
    }

    private void signFileConfirm() {

        try {

            if (StringUtil.isBlank(filePath)) {
                return;
            }

            if (null == dragView) {
                return;
            }

            File resultFile = null;
            try {
                Bitmap mark = dragView.getBitmap();
                Bitmap src = BitmapFactory.decodeStream(new FileInputStream(filePath));

                float scale = (float) src.getWidth() / ScreenUtil.getScreenWidth();
                mark = BitmapUtil.scaleBitmap(mark, scale);
                Bitmap result = PDFUtils.mergeBitmap(src, mark, (int) (dragView.getPosX() * scale), (int) ((dragView.getPosY() + scrollHeight) * scale));

                int start = filePath.lastIndexOf(File.separator);
                int end = filePath.lastIndexOf(".");
                String fileSignName = filePath.substring(start + 1, end);
                if (StringUtil.isBlank(fileSignName)) {
                    fileSignName = String.valueOf(System.currentTimeMillis());
                }

                resultFile = new File(mCtx.getExternalFilesDir("cache"), fileSignName + "_sign.jpg");
                if (resultFile.exists()) {
                    resultFile.delete();
                }
                FileOutputStream outputStream = new FileOutputStream(resultFile);
                result.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                outputStream.flush();
                outputStream.close();

                new File(filePath).delete();
//                        new File(signPath).delete();
                filePath = resultFile.getPath();

                src.recycle();
                mark.recycle();
                src = null;
                mark = null;
                result.recycle();
                result = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (resultFile != null) {
                    FileUtil.moveFile(resultFile, new File(mCtx.getExternalCacheDir(), resultFile.getName()));
                }

            }
        } catch (Exception e) {

        }
    }


    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(getActivity(), title, message, false, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private int getNavigationBarHeight() {
        Resources resources = getActivity().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.v("dbw", "Navi height:" + height);
        return height;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_TAKE_SIGN:

                String signPath = data.getStringExtra(SignHomeFragment.RESULT_PATH_SIGN);
                if (StringUtil.isNotBlank(signPath)) {
                    try {
                        Bitmap src = BitmapFactory.decodeStream(new FileInputStream(filePath));
                        Bitmap mark = BitmapFactory.decodeStream(new FileInputStream(signPath));

                        int newW = ScreenUtil.getScreenWidth() / 5;
                        int newH = (int) (newW * mark.getHeight() / (float) mark.getWidth());

                        float scale = (float) ScreenUtil.getScreenWidth() / (float) src.getWidth();
                        mark = BitmapUtil.scaleBitmap(mark, newW, newH);

                        if (null == dragView) {
                            dragView = FloatDragView.addFloatDragView(getActivity(), Binding().fileLayout, mark, null);
                        } else {
                            dragView.setBitMap(mark);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    /*if (StringUtil.isBlank(filePath)) {
                        return;
                    }

                    File resultFile = null;
                    try {
                        Bitmap src = BitmapFactory.decodeStream(new FileInputStream(filePath));
                        Bitmap mark = BitmapFactory.decodeStream(new FileInputStream(signPath));
                        Bitmap result = PDFUtils.mergeBitmap(src, mark);

                        int start = filePath.lastIndexOf(File.separator);
                        int end = filePath.lastIndexOf(".");
                        String fileSignName = filePath.substring(start + 1, end);
                        if (StringUtil.isBlank(fileSignName)){
                            fileSignName = String.valueOf(System.currentTimeMillis());
                        }

                        resultFile = new File(mCtx.getExternalFilesDir("cache"), fileSignName + "_sign.jpg");
                        if (resultFile.exists()) {
                            resultFile.delete();
                        }
                        FileOutputStream outputStream = new FileOutputStream(resultFile);
                        result.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
                        outputStream.flush();
                        outputStream.close();

                        new File(filePath).delete();
                        new File(signPath).delete();
                        filePath = resultFile.getPath();

                        src.recycle();
                        mark.recycle();
                        src = null;
                        mark = null;
                        result.recycle();
                        result = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        if (resultFile != null) {
                            FileUtil.moveFile(resultFile, new File(mCtx.getExternalCacheDir(), resultFile.getName()));
                        }
                        ToastUtil.showToast(mCtx, "签批完成");
                        getActivity().onBackPressed();
                    }*/

                    /*File pdfFile = new File(getContext().getExternalCacheDir(), FileName.getPdfName());
                    if (StringUtil.isBlank(filePath)) {
                        ToastUtil.showToast(mCtx, "暂无图片");
                        return;
                    }
                    try {
                        List<String> fileList = new ArrayList<>();
                        fileList.add(filePath);
                        PDFUtils.createPdf(pdfFile.getPath(), fileList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        ToastUtil.showToast(mCtx, "合成Success");
                        getActivity().onBackPressed();
                    }*/
                    break;
                }
        }
    }

    @Parcel
    public static class SignFileType implements Common.Type {

        String filePath;

        @ParcelConstructor
        public SignFileType(String filePath) {
            this.filePath = filePath;
        }

        @NonNull
        @Override
        public AbBindFragment newFragment() {
            return newInstance(filePath);
        }

        @NonNull
        @Override
        public String getTag() {
            return SignFileFragment.class.getSimpleName();
        }
    }
}
