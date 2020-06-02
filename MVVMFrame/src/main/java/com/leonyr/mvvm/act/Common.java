package com.leonyr.mvvm.act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.StatusBarUtil;
import com.leonyr.mvvm.R;
import com.leonyr.mvvm.databinding.CommonBinding;
import com.leonyr.mvvm.frag.AbBindFragment;
import com.leonyr.mvvm.vm.LViewModel;

import org.parceler.Parcels;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author L·Leony·R
 * @version 1.0
 * @time 2017/10/17 15:32
 * Copyright (C) L·Leony·R. All rights reversed.
 */
public class Common extends AbBindActivity<LViewModel, CommonBinding> {

    public static final String KEY_TYPE = "TYPE";

    public static void start(Context context, Type type) {
        Intent intent = new Intent(context, Common.class);
        intent.putExtra(KEY_TYPE, Parcels.wrap(type));
        context.startActivity(intent);
    }

    public static void startForResult(Fragment fragment, Type type, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), Common.class);
        intent.putExtra(KEY_TYPE, Parcels.wrap(type));
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startForResult(FragmentActivity c, Type type, int requestCode) {
        Intent intent = new Intent(c, Common.class);
        intent.putExtra(KEY_TYPE, Parcels.wrap(type));
        c.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.common;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initData(savedInstanceState);
        getIntentData(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntentData(intent, true);
    }

    protected void getIntentData(Intent intent, boolean canBack) {
        Type type = Parcels.unwrap(intent.getParcelableExtra(KEY_TYPE));
        if (type == null) {
            LogUtil.e("Common", "fragment type is null.");
            return;
        }
        replaceFragment(type, canBack);
    }

    protected void getIntentData(Intent intent) {
        getIntentData(intent, false);
    }

    protected void initData(Bundle savedInstanceState) {
        StatusBarUtil.setTransparentForWindow(this);
    }

    public interface Type {
        @NonNull
        AbBindFragment newFragment();

        @NonNull
        String getTag();
    }

    protected void replaceFragment(Type type, boolean canBack) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(type.getTag());
        if (null == fragment) {
            transaction.replace(R.id.fragment_container, type.newFragment(), type.getTag());
            if (canBack) {
                transaction.addToBackStack(type.getTag());
            }
            transaction.commit();
        } else {
            getSupportFragmentManager().popBackStack(fragment.getTag(), 0);
        }

    }

    /**
     * 用户按返回键监听器
     */
    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

    protected final List<WeakReference<Fragment>> fragList = new ArrayList<>();

    @Override
    public void onAttachFragment(Fragment fragment) {
        fragList.add(new WeakReference<>(fragment));
    }

    public List<Fragment> getActiveFragments() {
        List<Fragment> ret = new ArrayList<>();
        for (WeakReference<Fragment> ref : fragList) {
            Fragment f = ref.get();
            if (f != null) {
                if (f.isVisible()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();

        //  如果返回栈的数量大于0，那么存在除 ViewPager 中创建 Fragment
        if (fm.getBackStackEntryCount() > 0) {
            List<Fragment> fragments = getActiveFragments();
            Fragment fragment = fragments.get(fragments.size() - 1);
            if (fragment instanceof OnBackPressedListener) {
                boolean b = ((OnBackPressedListener) fragment).onBackPressed();
                if (b) {
                    return;
                }
            }
        }

        super.onBackPressed();
        finish();
    }
}