package com.leonyr.smartipaddemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.StatusBarUtil;
import com.leonyr.mvvm.act.AbBindActivity;
import com.leonyr.mvvm.act.Common;
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
public class NewCommon extends AbBindActivity<LViewModel, CommonBinding> {

    public static final String KEY_TYPE = "TYPE";
    public static NewCommon INSTANCE;

    public static void start(Context context, Common.Type type) {
        Intent intent = new Intent(context, NewCommon.class);
        intent.putExtra(KEY_TYPE, Parcels.wrap(type));
        context.startActivity(intent);
    }

    public static void startForResult(Fragment fragment, Common.Type type, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), NewCommon.class);
        intent.putExtra(KEY_TYPE, Parcels.wrap(type));
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startForResult(FragmentActivity c, Common.Type type, int requestCode) {
        Intent intent = new Intent(c, NewCommon.class);
        intent.putExtra(KEY_TYPE, Parcels.wrap(type));
        c.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.common;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        INSTANCE = this;
        initData(savedInstanceState);
        getIntentData(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntentData(intent, false);
    }

    protected void getIntentData(Intent intent, boolean canBack) {
        Common.Type type = Parcels.unwrap(intent.getParcelableExtra(KEY_TYPE));
        if (type == null) {
            LogUtil.e("Common", "fragment type is null.");
            return;
        }

        if (fragList.isEmpty()) {
            replaceFragment(type, canBack);
        }else {
            Fragment frag = fragList.get(fragList.size() - 1).get();
            if (frag instanceof DialogFragment){
                ((DialogFragment) frag).dismiss();
            }
            replaceFragment(type, canBack);
        }
    }

    protected void getIntentData(Intent intent) {
        getIntentData(intent, false);
    }

    protected void initData(Bundle savedInstanceState) {
        StatusBarUtil.setTransparentForWindow(this);
    }


    protected void replaceFragment(Common.Type type, boolean canBack) {

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
    }
}