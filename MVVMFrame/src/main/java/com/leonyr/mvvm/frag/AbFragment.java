package com.leonyr.mvvm.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leonyr.mvvm.vm.LViewModel;

/**
 * ==============================================================
 * Description: fragment 基类
 * <p>
 * Created by leonyr on 2019-04-20.04.20
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public abstract class AbFragment<VM extends LViewModel> extends IFragment {

    protected String TAG;
    private VM VModel;
    protected Context mCtx;
    protected View rootView;

    public AbFragment(){
        getLifecycle().addObserver(new FragmentObserver());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCtx = context;
        TAG = getClass().getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutResId(), container);
            initView(rootView, savedInstanceState);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    public View getRootView() {
        return rootView;
    }

    public VM getVModel() {
        if (VModel == null) {
            throw new NullPointerException("You should setViewModel first!");
        }
        return VModel;
    }

    public void setVModel(VM VModel) {
        this.VModel = VModel;
    }

    @LayoutRes
    protected abstract int getLayoutResId();

    protected abstract void initView(View rootView, Bundle savedInstanceState) ;
}
