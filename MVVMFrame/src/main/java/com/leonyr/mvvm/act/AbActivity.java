package com.leonyr.mvvm.act;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.leonyr.mvvm.vm.LViewModel;

/**
 * ==============================================================
 * Description: activity 基类
 * <p>
 * Created by leonyr on 2019-04-20.04.20
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public abstract class AbActivity<VM extends LViewModel> extends AppCompatActivity {

    protected String TAG;
    protected Context mCtx;
    private View rootView;
    private VM VModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mCtx = this;
        getLifecycle().addObserver(new ActivityObserver());

        setContentView(getLayoutResId());
        TAG = getClass().getSimpleName();

        initView();
    }

    @Override
    public void setContentView(int layoutResID) {
        rootView = View.inflate(this, layoutResID, null);
        super.setContentView(rootView);
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

    @Override
    protected void onResume() {
        super.onResume();
        TAG = getClass().getSimpleName();
    }

    @Override
    protected void onDestroy() {
        mCtx = null;
        super.onDestroy();
    }

    /**
     * 获取layout id
     */
    @LayoutRes
    protected abstract int getLayoutResId();

    /**
     * 初始化各类view对象
     */
    protected abstract void initView();

}
