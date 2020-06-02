package com.leonyr.mvvm.vh;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019-04-20.04.20
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class BindViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    @NonNull
    private final T binding;

    public BindViewHolder(@NonNull T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public T Binding(){
        return binding;
    }
}
