package com.leonyr.smartipaddemo.crop;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.leonyr.mvvm.vm.LViewModel;

public class MediaViewModel extends LViewModel {

    public MediaViewModel(@NonNull Context ctx) {
        super(ctx);
    }

    public static MutableLiveData<String> picPath = new MutableLiveData<>();
    public static MutableLiveData<String> moviePath = new MutableLiveData<>();
    public static MutableLiveData<String> audioPath = new MutableLiveData<>();
}
