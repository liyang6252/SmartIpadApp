package com.leonyr.mvvm.frag;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
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
public abstract class AbBindBottomDialogFragment<VM extends LViewModel, B extends ViewDataBinding> extends BottomSheetDialogFragment {

    protected String TAG;
    private VM VModel;
    protected Context mCtx;
    private B binding;

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
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false);
            initView(binding.getRoot(), savedInstanceState);
        }
        ViewGroup parent = (ViewGroup) binding.getRoot().getParent();
        if (parent != null) {
            parent.removeView(binding.getRoot());
        }
        return binding.getRoot();
    }

    public B Binding() {
        return binding;
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
