package com.leonyr.smartipaddemo.conn;

import android.databinding.BindingAdapter;
import android.view.View;

public class BindingConfig {

    @BindingAdapter(value = {"onMulClick"})
    public static void onMulClick(View view, OnMultiClickListener listener){
        view.setOnClickListener(listener);
    }

}
