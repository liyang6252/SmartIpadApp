package com.leonyr.mvvm.vm;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * ==============================================================
 * Description:context aware {@link ViewModel}.
 * <p>
 * Created by 01385127 on 2019.04.28
 * (C) Copyright sf_Express Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class LViewModel extends ViewModel {

    @SuppressLint("StaticFieldLeak")
    private WeakReference<Context> context;

    public LViewModel(@NonNull Context ctx) {
        context = new WeakReference<>(ctx);
    }

    /**
     * Return the application.
     */
    @SuppressWarnings("TypeParameterUnusedInFormals")
    @NonNull
    public <T extends Context> T getContext() {
        //noinspection unchecked
        return (T) context.get();
    }

    /**
     * 简化创建ViewModel
     *
     * @param a      创建的activity
     * @param tClass ViewModel类别
     * @returna
     */
    public static <C extends ViewModel> C create(FragmentActivity a, Class<C> tClass) {
        return ViewModelProviders.of(a, new LViewModelFactory(a)).get(tClass);
    }

    /**
     * 简化创建ViewModel
     *
     * @param f      创建的activity
     * @param tClass ViewModel类别
     * @returna
     */
    public static <C extends ViewModel> C create(Fragment f, Class<C> tClass) {
        return ViewModelProviders.of(f, new LViewModelFactory(f.getContext())).get(tClass);
    }

    /**
     * 简化创建ViewModel
     *
     * @param a      创建的activity
     * @param tClass ViewModel类别
     * @returna
     */
    public static <C extends ViewModel> C createAndroid(FragmentActivity a, Class<C> tClass) {
        return ViewModelProviders.of(a, new ViewModelProvider.AndroidViewModelFactory(a.getApplication())).get(tClass);
    }

    /**
     * 简化创建ViewModel
     *
     * @param f      创建的activity
     * @param tClass ViewModel类别
     * @returna
     */
    public static <C extends ViewModel> C createAndroid(Fragment f, Class<C> tClass) {
        return ViewModelProviders.of(f, new ViewModelProvider.AndroidViewModelFactory(f.getActivity().getApplication())).get(tClass);
    }
}
