package com.leonyr.mvvm.frag;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.leonyr.mvvm.R;
import com.leonyr.mvvm.vm.LViewModel;

/**
 * ==============================================================
 * Description: fragment 基类
 * <p>
 * Created by leonyr on 2019-04-20.04.20
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public abstract class AbBindDialogFragment<VM extends LViewModel, B extends ViewDataBinding> extends DialogFragment {

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

        getDialog().setCanceledOnTouchOutside(false);

        return binding.getRoot();
    }

    public B Binding() {
        return binding;
    }

    protected static float WIDTH_RATIO = 0.85f;

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        if (window != null) {
            DisplayMetrics dm = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * WIDTH_RATIO), WindowManager.LayoutParams.WRAP_CONTENT);
        }
        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.CommonDialogStyle);
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
