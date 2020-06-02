package com.leonyr.mvvm.vh;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import me.drakeet.multitype.ItemViewBinder;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019-04-20.04.20
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public abstract class AbViewBinder<Data, Binding extends ViewDataBinding>
        extends ItemViewBinder<Data, BindViewHolder<Binding>> {

    @NonNull
    @Override
    protected BindViewHolder<Binding> onCreateViewHolder(@NonNull LayoutInflater inflater,
                                                         @NonNull ViewGroup parent) {
        Binding binding = DataBindingUtil.inflate(inflater, getLayoutId(), parent, false);
        return new BindViewHolder<>(binding);
    }

    @LayoutRes
    protected abstract int getLayoutId();

}
