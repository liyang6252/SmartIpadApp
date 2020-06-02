package com.leonyr.smartipaddemo.sign;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.leonyr.lib.utils.FileUtil;
import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.ToastUtil;
import com.leonyr.mvvm.act.Common;
import com.leonyr.mvvm.frag.AbBindDialogFragment;
import com.leonyr.mvvm.util.RxSchedulers;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.InitApplication;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.conn.FileName;
import com.leonyr.smartipaddemo.databinding.FmSignHomeBinding;
import com.leonyr.smartipaddemo.set.SettingFragment;
import com.leonyr.smartipaddemo.view.pen.PenType;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019/6/27
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class SignHomeFragment extends AbBindDialogFragment<LViewModel, FmSignHomeBinding> {

    public static final String RESULT_PATH_SIGN = "result_path_sign";

    public static SignHomeFragment newInstance() {

        Bundle args = new Bundle();

        SignHomeFragment fragment = new SignHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fm_sign_home;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        WIDTH_RATIO = 0.6f;
        Binding().setFm(this);
        Binding().signView.setPenType(PenType.BRUSH);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != Binding().signView){
            Binding().signView.setPenColor(InitApplication.PEN_COLOR);
            Binding().signView.setPenWidth(InitApplication.PEN_WIDTH);
        }
        LogUtil.e("onResume", "onResume");
    }

    public void clear(){
        if (null != Binding().signView){
            Binding().signView.reDo();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_clear:
                Binding().signView.reDo();
                break;
            case R.id.sign_save:
                if (Binding().signView.isDrawed()) {

                    File saveFile = new File(getContext().getExternalFilesDir("cache"), FileName.getPhotoPngName());
                    Observable
                            .fromCallable(new Callable<Bitmap>() {
                                @Override
                                public Bitmap call() throws Exception {
                                    return Binding().signView.getBitmap();
                                }
                            })
                            .map(new Function<Bitmap, Object>() {
                                @Override
                                public Object apply(Bitmap bitmap) throws Exception {
                                    saveFile.createNewFile();
                                    FileOutputStream stream = new FileOutputStream(saveFile);
                                    if (bitmap.isRecycled()){
                                        return "";
                                    }
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    stream.flush();
                                    stream.close();
                                    return "";
                                }
                            })
                            .compose(RxSchedulers.IOMain())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object o) throws Exception {
                                    Binding().signView.reDo();
                                    if (getTargetFragment() != null) {
                                        Intent intent = new Intent();
                                        intent.putExtra(RESULT_PATH_SIGN, saveFile.getPath());
                                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                                    } else {
                                        File desFile = new File(getContext().getExternalCacheDir(), saveFile.getName());
                                        FileUtil.moveFile(saveFile, desFile);
                                        saveFile.delete();
                                    }
                                    dismiss();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                    ToastUtil.showToast(getContext(), "保存失败");
                                }
                            });
                } else {
                    ToastUtil.showToast(getContext(), "请签名");
                }
                break;
            case R.id.sign_set:
//                openFragment(new SettingFragment.SettingType());
                Common.start(mCtx, new SettingFragment.SettingType());
                break;
        }
    }

    public void openFragment(Common.Type type) {
        openFragment(type.newFragment(), type.getTag());
    }

    public void openFragment(Fragment fragment, String tag) {

        Fragment targetParent = this;
        while (targetParent.getId() != R.id.fragment_container) {
            targetParent = targetParent.getParentFragment();
//            LogUtil.e(getClass().getSimpleName(), "id: " + targetParent.getId());
        }

        FragmentManager fm = targetParent.getFragmentManager();
        fm.beginTransaction()
                .hide(targetParent)
                .add(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

}
