package com.leonyr.smartipaddemo.password;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.leonyr.lib.utils.FileUtil;
import com.leonyr.lib.utils.ToastUtil;
import com.leonyr.mvvm.act.Common;
import com.leonyr.mvvm.frag.AbBindFragment;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.conn.FileName;
import com.leonyr.smartipaddemo.databinding.FmPasswordInputBinding;

import org.parceler.Parcel;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019-06-29
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class PasswordInputFragment extends AbBindFragment<LViewModel, FmPasswordInputBinding> {

    Disposable disposable;

    @Override
    protected int getLayoutResId() {
        return R.layout.fm_password_input;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        Binding().setFm(this);
        Binding().pwInput.requestFocus();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pw_input_cancel:

                break;
            case R.id.pw_input_ok:
                if (!TextUtils.isEmpty(Binding().pwInput.getText())) {
                    String password = Binding().pwInput.getText().toString();
                    ToastUtil.showToast(mCtx, password);
                    FileUtil.saveStrToFile(password, mCtx.getExternalCacheDir() + File.separator + FileName.getTextName());
                }
                break;
        }
        if (null != disposable && !disposable.isDisposed()){
            disposable.dispose();
        }
        disposable = Observable.just(0)
                .delay(3, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        getActivity().onBackPressed();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    @Parcel
    public static class PasswordInputType implements Common.Type {
        @NonNull
        @Override
        public AbBindFragment newFragment() {
            return new PasswordInputFragment();
        }

        @NonNull
        @Override
        public String getTag() {
            return PasswordInputFragment.class.getSimpleName();
        }
    }

    @Override
    public void onDestroy() {
        if (null != disposable && !disposable.isDisposed()){
            disposable.dispose();
        }
        super.onDestroy();
    }
}
