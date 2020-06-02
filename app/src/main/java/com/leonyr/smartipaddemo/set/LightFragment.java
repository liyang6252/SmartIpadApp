package com.leonyr.smartipaddemo.set;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;

import com.leonyr.lib.utils.SPUtils;
import com.leonyr.mvvm.act.Common;
import com.leonyr.mvvm.frag.AbBindFragment;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.conn.APPContant;
import com.leonyr.smartipaddemo.databinding.FmLightBinding;

import org.parceler.Parcel;

public class LightFragment extends AbBindFragment<LViewModel, FmLightBinding> {

    @Override
    protected int getLayoutResId() {
        return R.layout.fm_light;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean check = SPUtils.getInstance().getBoolean(APPContant.SP_SWITCH_CAMERA_FLASHLIGHT, false);
        Binding().switchLight.setChecked(check);
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {


        Binding().switchLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingFragment.BrightControl(isChecked);
            }
        });
    }

    @Parcel
    public static class LightType implements Common.Type {

        @NonNull
        @Override
        public AbBindFragment newFragment() {
            return new LightFragment();
        }

        @NonNull
        @Override
        public String getTag() {
            return LightFragment.class.getSimpleName();
        }
    }

}
