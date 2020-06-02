package com.leonyr.smartipaddemo.set;

import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CompoundButton;

import com.leonyr.lib.utils.DataCleanManager;
import com.leonyr.lib.utils.IntentUtil;
import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.SPUtils;
import com.leonyr.lib.utils.ShellUtil;
import com.leonyr.lib.utils.ToastUtil;
import com.leonyr.mvvm.act.Common;
import com.leonyr.mvvm.frag.AbBindFragment;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.InitApplication;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.conn.APPContant;
import com.leonyr.smartipaddemo.databinding.FmSettingBinding;
import com.leonyr.view.colorpicker.dialog.ColorPickerDialogFragment;
import com.leonyr.view.linepath.LinePathView;
import com.sd.tgfinger.api.TGAPI;

import org.parceler.Parcel;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019-06-29
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class SettingFragment extends AbBindFragment<LViewModel, FmSettingBinding> implements ColorPickerDialogFragment.ColorPickerDialogListener {

    static ShellUtil shellUtil = new ShellUtil(false);

    @Override
    protected int getLayoutResId() {
        return R.layout.fm_setting;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        Binding().setFm(this);
        try {
            Binding().setCacheSize.setText(DataCleanManager.getTotalCacheSize(mCtx));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Binding().setSwitchFingerVoiceT.setText(InitApplication.getTGAPI(mCtx).getCurrentVolume(mCtx));

        Binding().setPenT.setText(String.valueOf(InitApplication.PEN_WIDTH));
        Binding().setColorPreview.setBackgroundColor(InitApplication.PEN_COLOR);

        Binding().setSwitchFinger.setChecked(SPUtils.getInstance().getBoolean(APPContant.SP_SWITCH_FINGER_PRINT, false));
        Binding().setSwitchFingerVoiceT.setText(SPUtils.getInstance().getString(APPContant.SP_SWITCH_FINGER_PRINT_VOICE, InitApplication.getTGAPI(mCtx).getCurrentVolume(mCtx)));

        Binding().setSwitchFinger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.getInstance().put(APPContant.SP_SWITCH_FINGER_PRINT, isChecked);
            }
        });

        Binding().setSwitchCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BrightControl(isChecked);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Binding().setSwitchCamera.setChecked(SPUtils.getInstance().getBoolean(APPContant.SP_SWITCH_CAMERA_FLASHLIGHT, false));
    }

    private String ConverColorHex(int color) {
        String red = Integer.toHexString(Color.red(color));
        if (red.length() == 1) {
            red = "0" + red;
        }

        String green = Integer.toHexString(Color.green(color));
        if (green.length() == 1) {
            green = "0" + green;
        }

        String blue = Integer.toHexString(Color.blue(color));
        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        return "#" + red + green + blue;
    }

    @Override
    public void onColorSelected(int color) {
        InitApplication.PEN_COLOR = color;
        Binding().setColorPreviewText.setText(ConverColorHex(color));
        Binding().setColorPreview.setBackgroundColor(InitApplication.PEN_COLOR);
    }

    public static void BrightControl(boolean open) {
        SPUtils.getInstance().put(APPContant.SP_SWITCH_CAMERA_FLASHLIGHT, open);

        String result;
        String format = "echo %d > /sys/class/leds/camled/brightness";
        if (open) {
            result = shellUtil.run(String.format(Locale.CHINA, format, 1), 3000).getResult();
        } else {
            result = shellUtil.run(String.format(Locale.CHINA, format, 0), 3000).getResult();
        }
        LogUtil.e("BrightControl", result);


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_phone_state:
                IntentUtil.ToDeviceInfoSetting(mCtx);
                try {
                    Camera m_Camera = Camera.open();
                    Camera.Parameters mParameters;
                    mParameters = m_Camera.getParameters();
                    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    m_Camera.setParameters(mParameters);
                    m_Camera.release();
                } catch (Exception ex) {
                }
                break;
            case R.id.set_phone_date:
                IntentUtil.ToDateSetting(mCtx);
                break;
            case R.id.set_nfc:
                IntentUtil.ToNFCShareSetting(mCtx);
                break;
            case R.id.set_cache:
                DataCleanManager.clearExternalFile(mCtx);
                Binding().setCacheSize.setText("0K");
                break;
            case R.id.set_switch_finger_voice_a:
                boolean increaseVolume = InitApplication.getTGAPI(mCtx).increaseVolume();
                if (increaseVolume) {
                    String currentVolume = InitApplication.getTGAPI(mCtx).getCurrentVolume(mCtx);
                    Binding().setSwitchFingerVoiceT.setText(currentVolume);
                    SPUtils.getInstance().put(APPContant.SP_SWITCH_FINGER_PRINT_VOICE, currentVolume);
                }
                break;
            case R.id.set_switch_finger_voice_m:
                boolean descreaseVolume = InitApplication.getTGAPI(mCtx).descreaseVolume();
                if (descreaseVolume) {
                    String currentVolume = InitApplication.getTGAPI(mCtx).getCurrentVolume(mCtx);
                    Binding().setSwitchFingerVoiceT.setText(currentVolume);
                    SPUtils.getInstance().put(APPContant.SP_SWITCH_FINGER_PRINT_VOICE, currentVolume);
                }
                break;
            case R.id.set_pen_a:
                int penMaxWidth = new BigDecimal(Binding().setPenT.getText().toString()).intValue();
                if (penMaxWidth >= 30) {
                    ToastUtil.showToast(mCtx, "已达到最大值");
                } else {
                    penMaxWidth++;
                    Binding().setPenT.setText(String.valueOf(penMaxWidth));
                    InitApplication.PEN_WIDTH = penMaxWidth;
                    SPUtils.getInstance().put(APPContant.SP_PEN_WIDTH, penMaxWidth);
                }
                break;
            case R.id.set_pen_m:
                int penMinWidth = new BigDecimal(Binding().setPenT.getText().toString()).intValue();
                if (penMinWidth <= 5) {
                    ToastUtil.showToast(mCtx, "已达到最小值");
                } else {
                    penMinWidth--;
                    Binding().setPenT.setText(String.valueOf(penMinWidth));
                    InitApplication.PEN_WIDTH = penMinWidth;
                    SPUtils.getInstance().put(APPContant.SP_PEN_WIDTH, penMinWidth);
                }
                break;
            case R.id.set_color_preview:
                ColorPickerDialogFragment pickerDialogFragment = ColorPickerDialogFragment.newInstance(LinePathView.PEN_COLOR);
                pickerDialogFragment.setmListener(this);
                pickerDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
                pickerDialogFragment.show(getFragmentManager(), "d");
                break;
        }
    }


    @Parcel
    public static class SettingType implements Common.Type {
        @NonNull
        @Override
        public AbBindFragment newFragment() {
            return new SettingFragment();
        }

        @NonNull
        @Override
        public String getTag() {
            return SettingFragment.class.getSimpleName();
        }
    }
}
