package com.leonyr.mvvm.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by 01385127 on 2019.04.28
 * (C) Copyright sf_Express Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class LViewModelFactory extends ViewModelProvider.NewInstanceFactory{

    private static LViewModelFactory instance;

    /**
     * Retrieve a singleton instance of AndroidViewModelFactory.
     *
     * @param ctx an application to pass in {@link LViewModel}
     * @return A valid {@link ViewModelProvider.AndroidViewModelFactory}
     */
    public static LViewModelFactory getInstance(@NonNull Context ctx) {
        if (null == instance){
            instance = new LViewModelFactory(ctx);
        }
        return instance;
    }

    private Context context;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param context an application to pass in {@link LViewModel}
     */
    public LViewModelFactory(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (LViewModel.class.isAssignableFrom(modelClass)) {
            //noinspection TryWithIdenticalCatches
            try {
                return modelClass.getConstructor(Context.class).newInstance(context);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
        return super.create(modelClass);
    }
}
