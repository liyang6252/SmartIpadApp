package com.leonyr.smartipaddemo.finger;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.SPUtils;
import com.leonyr.lib.utils.ToastUtil;
import com.leonyr.mvvm.act.Common;
import com.leonyr.mvvm.frag.AbBindFragment;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.InitApplication;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.conn.APPContant;
import com.leonyr.smartipaddemo.conn.FileName;
import com.leonyr.smartipaddemo.databinding.FmFingerPrintBinding;
import com.leonyr.smartipaddemo.db.DBUtil;
import com.sd.tgfinger.api.TGAPI;
import com.sd.tgfinger.utils.AudioProvider;

import org.parceler.Parcel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019/7/12
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class FingerPrintFragment extends AbBindFragment<LViewModel, FmFingerPrintBinding> {

    private static final int MODE_REGISTER = 0x11;
    private static final int MODE_VERIFY = 0x12;
    private static final int MODE_VERIFY_MORE = 0x13;

    private int cMode = MODE_REGISTER;
    AudioProvider audioPro;

    private long lastClickTime = 0;

    @Override
    protected int getLayoutResId() {
        return R.layout.fm_finger_print;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        Binding().setFm(this);
        initTG();
    }

    private void initTG() {
        if (InitApplication.getTGAPI(mCtx).isDevOpen()) {
            deviceLink(true);
            showTip("设备已连接");
        } else {
            deviceLink(false);
        }
        InitApplication.getTGAPI(mCtx).openDev(tgHandler, TGAPI.WORK_BEHIND, TGAPI.TEMPL_MODEL_3);
        audioPro = InitApplication.getTGAPI(mCtx).getAP(mCtx);

        BigDecimal voiceDecimal = new BigDecimal(SPUtils.getInstance().getString(APPContant.SP_SWITCH_FINGER_PRINT_VOICE, "8"));
        InitApplication.getTGAPI(mCtx).setVolume(mCtx, voiceDecimal.intValue());
    }

    private void startRead(int type) {
        cMode = type;
        InitApplication.getTGAPI(mCtx).readDataFromHost(tgHandler, InitApplication.getTGAPI(mCtx).moniExter3Path);
    }

    @SuppressLint("HandlerLeak")
    private Handler tgHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TGAPI.DEV_STATUS:
                    /*
                     * 设备状态：
                     *  1：设备状态：已连接
                     *  -1：已断开,连接中...
                     */
                    int devStatusArg = msg.arg1;
                    deviceLink(devStatusArg > 0);
                    break;
                case TGAPI.OPEN_DEV:
                    /**
                     * 1:设备打开成功，后比设置成功
                     * -1:设备打开失败
                     */
                    int openArg = msg.arg1;
                    if (openArg == 1) {
                        showTip("设备打开成功");
                        //设置设备的工作模式
                        InitApplication.getTGAPI(mCtx).setDevWorkModel(tgHandler, TGAPI.WORK_BEHIND);
                        //初始化数据，开启连续验证
//                        startRead(MODE_VERIFY);
                    } else if (openArg == -1) {
                        showTip("设备打开失败");
                    }
                    break;
                case TGAPI.READ_FILE:
                    //读取数据
//                    LogUtil.e(msg.what + " " + msg.arg1);

                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        byte[] fingerData = bundle.getByteArray(TGAPI.DATA);
                        int fingerSize = bundle.getInt(TGAPI.FINGER_SIZE);
                        if (fingerData != null && fingerData.length > 0) {
                            if (cMode == MODE_REGISTER) {
                                InitApplication.getTGAPI(mCtx).extractFeatureRegister(tgHandler, fingerData, fingerSize);
                            } else if (cMode == MODE_VERIFY) {
                                InitApplication.getTGAPI(mCtx).continueVerifyN(tgHandler, fingerData, fingerSize, 1);
                            } else if (cMode == MODE_VERIFY_MORE) {
//                                showTip("数据准备完毕");
//                                Binding().printRegister.setEnabled(false);
                                //开启连续验证
                                InitApplication.getTGAPI(mCtx).continueVerifyN(tgHandler, fingerData, fingerSize, 1000);
                            }
                        } else {
                            if (cMode == MODE_REGISTER) {
                                InitApplication.getTGAPI(mCtx).extractFeatureRegister(tgHandler, null, 0);
                            } else if (cMode == MODE_VERIFY) {
                                ToastUtil.showToast(mCtx, "暂无模板数据，请先注册模板");
                            }
                        }
                    }
                    break;
                case TGAPI.WRITE_FILE:
                    //存储数据
                    int saveArg = msg.arg1;
                    if (saveArg == 1) {
//                        showTip("数据存储成功");
                        //初始化数据，开启连续验证
                        startRead(MODE_VERIFY);
                    } else if (saveArg == -1) {
//                        showTip("数据存储失败");
                    }
                    break;
                case TGAPI.CLOSE_DEV:
                    /**
                     * 1:指静脉设备关闭成功
                     * -1:指静脉设备关闭失败
                     */
                    int closeArg = msg.arg1;
                    if (closeArg == 1) {
                        showTip("指静脉设备关闭成功");
                    } else if (closeArg == -1) {
                        showTip("指静脉设备关闭失败");
                    }
                    break;
//                case TGAPI.SET_DEV_MODEL:
//                    /**
//                     * 设置设备的工作模式
//                     * 1：设置成功
//                     * 2：设置失败，该设备不支持6特征模板注册
//                     * 3：请先删除设备中的三模板
//                     * 4：请先删除设备中的六模板
//                     * -1：设置失败
//                     * -2 ：入参错误
//                     */
//                    int setModelArg = msg.arg1;
//                    if (setModelArg == 1) {
//                        tipTv.setText("设置成功");
//                    } else if (setModelArg == 2) {
//                        tipTv.setText("设置失败，该设备不支持6特征模板注册");
//                    } else if (setModelArg == 3) {
//                        tipTv.setText("请先删除设备中的三模板");
//                    } else if (setModelArg == 4) {
//                        tipTv.setText("请先删除设备中的六模板");
//                    } else if (setModelArg == -1) {
//                        tipTv.setText("设置失败");
//                    } else if (setModelArg == -2) {
//                        tipTv.setText("入参错误");
//                    }
//                    break;
                case TGAPI.DEV_WORK_MODEL:
                    /**
                     * 1:前比3特征模板
                     * 2:后比
                     * 3:前比6特征模板
                     * -1:超时
                     */
                    int devModelArg = msg.arg1;
                    if (devModelArg == 1) {
                        showTip("前比3特征模板");
                    } else if (devModelArg == 2) {
                        showTip("后比");
                    } else if (devModelArg == 3) {
                        showTip("前比6特征模板");
                    } else if (devModelArg == -1) {
                        showTip("超时");
                    }
                    break;
                case TGAPI.CANCEL_REGISTER://取消注册接口也可取消验证
                    /**
                     * 1:取消注册成功
                     * -1:取消注册失败
                     */
                    resetClick();
                    int cancelRegisterArg = msg.arg1;
                    if (cancelRegisterArg == 1) {
                        showTip("取消注册成功");
                        //初始化数据，开启连续验证
//                        startRead(MODE_VERIFY_MORE);
                    } else if (cancelRegisterArg == -1) {
                        showTip("取消注册失败");
                    }
                    break;
                case TGAPI.EXTRACT_FEATURE_REGISTER:
                    /**
                     * 注册
                     * 1:特征提取成功,Output数据有效
                     * 2:特征提取失败,因证书路径错误,Output数据无效
                     * 3:特征提取失败,因证书内容无效,Output数据无效
                     * 4:特征提取失败,因证书内容过期,Output数据无效
                     * 5:特征提取失败,因"图像"数据无效,Output数据无效
                     * 6:特征提取失败,因"图像"质量较差,Output数据无效
                     * 7:模板登记重复
                     * 8:登记成功
                     * 9:特征融合失败，因"特征"数据一致性差，Output数据无效
                     * -1:特征提取失败,因参数不合法,Output数据无效
                     * -2:注册特征提取失败
                     * -3:抓图超时
                     * -4:设备断开
                     * -5:操作取消
                     * -6:入参错误
                     * -7:抓图未知错误
                     * -8:特征比对（1：N）失败，因参数不合法，Output数据无效
                     * -9:验证提取特征失败
                     * -10:登记失败
                     */
                    int registerArg = msg.arg1;
                    if (registerArg == 1) {
                        showTip("特征提取成功,Output数据有效");
                    } else if (registerArg == 2) {
                        showTip("特征提取失败,因证书路径错误,Output数据无效");
                        resetClick();
                    } else if (registerArg == 3) {
                        showTip("特征提取失败,因证书路径错误,Output数据无效");
                        resetClick();
                    } else if (registerArg == 4) {
                        showTip("特征提取失败,因证书内容过期,Output数据无效");
                        resetClick();
                    } else if (registerArg == 5) {
                        showTip("特征提取失败,因\"图像\"数据无效,Output数据无效");
                        resetClick();
                    } else if (registerArg == 6) {
                        showTip("特征提取失败,因\"图像\"质量较差,Output数据无效");
                        resetClick();
                    } else if (registerArg == 7) {
                        showTip("模板登记重复");
                        //初始化数据，开启连续验证
//                        startRead(MODE_VERIFY_MORE);
                        resetClick();
                    } else if (registerArg == 8) {
                        resetClick();
                        showTip("登记成功");
                        Bundle data = msg.getData();
                        if (data != null) {
                            byte[] fingerData = data.getByteArray(TGAPI.FINGER_DATA);
                            if (fingerData != null && fingerData.length > 0) {
//                                showTip("接收到注册的模板数据");
                            }
                            //模拟数据库存储数据
                            saveData(fingerData);
                        }

                    } else if (registerArg == 9) {
                        showTip("特征融合失败，因\"特征\"数据一致性差，Output数据无效");
                        resetClick();
                    } else if (registerArg == -1) {
                        showTip("特征提取失败,因参数不合法,Output数据无效");
                        resetClick();
                    } else if (registerArg == -2) {
                        showTip("注册特征提取失败");
                        resetClick();
                    } else if (registerArg == -3) {
                        showTip("操作超时");
                        resetClick();
                    } else if (registerArg == -4) {
                        showTip("设备断开");
                        resetClick();
                    } else if (registerArg == -5) {
                        Log.d("===KKK", "操作取消  111");
//                        showTip("操作取消");
//                        resetClick();
                    } else if (registerArg == -6) {
                        showTip("入参错误");
                        resetClick();
                    } else if (registerArg == -7) {
                        showTip("抓图未知错误");
                        resetClick();
                    } else if (registerArg == -8) {
                        showTip("特征比对（1：N）失败，因参数不合法，Output数据无效");
                        resetClick();
                    } else if (registerArg == -9) {
                        showTip("验证提取特征失败");
                        resetClick();
                    } else if (registerArg == -10) {
                        showTip("登记失败");
                        resetClick();
                    }
                    break;
                case TGAPI.FEATURE_COMPARE1_N:
                case TGAPI.CONTINUE_VERIFY:
                    /**
                     * 1:验证成功,Output数据有效
                     * -1:特征比对（1：N）失败，因参数不合法，Output数据无效
                     * -2:特征比对（1：N）失败，仅Output的matchScore数据有效
                     * -3:抓图超时
                     * -4:设备断开
                     * -5:操作取消
                     * -6:入参错误
                     * -7:抓图未知错误
                     * -8:验证提取特征失败
                     */
                    int compareNArg = msg.arg1;
                    if (compareNArg == 1) {
                        Bundle data = msg.getData();
                        if (data != null) {
                            //获取比对的分数，比对模板的位置，比对的后可更新的模板数据
                            int score = data.getInt(TGAPI.COMPARE_SCORE);
                            int index = data.getInt(TGAPI.INDEX);
                            byte[] updateFinger = data.getByteArray(TGAPI.UPDATE_FINGER);
                            showTip("特征比对（1：N）成功，Output数据有效:" + score);
                        }
                        resetClick();
                        audioPro.play_vetify_ok();
                    } else if (compareNArg == -1) {
                        showTip("特征比对（1：N）失败，因参数不合法，Output数据无效");
                        audioPro.play_vetify_fail();
                        resetClick();
                    } else if (compareNArg == -2) {
                        Bundle data = msg.getData();
                        if (data != null) {
                            int score = data.getInt(TGAPI.COMPARE_SCORE);
                            showTip("特征比对（1：N）失败，仅Output的matchScore数据有效:" + score);
                        }
                        audioPro.play_vetify_fail();
                        resetClick();
                    } else if (compareNArg == -3) {
                        showTip("抓图超时");
                        resetClick();
                    } else if (compareNArg == -4) {
                        showTip("设备断开");
                        deviceLink(false);
                    } else if (compareNArg == -5) {
                        Log.d("===KKK", "操作取消  222");
//                        showTip("操作取消");
//                        resetClick();
                    } else if (compareNArg == -6) {
                        showTip("入参错误");
                        resetClick();
                    } else if (compareNArg == -7) {
                        showTip("抓图未知错误");
                        resetClick();
                    } else if (compareNArg == -8) {
                        showTip("验证提取特征失败");
                        resetClick();
                    }
                    break;
            }
        }
    };

    public void clickRegister(){
        Binding().printVerify.setVisibility(View.GONE);
        Binding().printRegister.setVisibility(View.VISIBLE);
        Binding().printRegister.setEnabled(false);
    }

    public void clickVerify(){
        Binding().printVerify.setVisibility(View.VISIBLE);
        Binding().printVerify.setEnabled(false);
        Binding().printRegister.setVisibility(View.GONE);
    }

    public void resetClick(){
        Binding().printVerify.setVisibility(View.VISIBLE);
        Binding().printVerify.setEnabled(true);
        Binding().printRegister.setVisibility(View.VISIBLE);
        Binding().printRegister.setEnabled(true);
    }

    public void onClick(View v) {
        long nowTime = System.currentTimeMillis();
        LogUtil.e("" + (nowTime - lastClickTime));
        if (nowTime - lastClickTime < 1000){
            return;
        }
        lastClickTime = nowTime;

        switch (v.getId()) {
            case R.id.print_register:
                startRead(MODE_REGISTER);
                showTip("请自然轻放手指");
                clickRegister();
                break;
            case R.id.print_verify:
                clickVerify();
                startRead(MODE_VERIFY);
//                Binding().printRegister.setEnabled(false);
                break;
        }
    }

    private void showTip(String str) {
        Binding().printState.setText(str);
    }

    private void deviceLink(boolean link) {
        Binding().setDeviceLink(link);
    }

    private void saveData(byte[] data) {
        if (data != null && data.length > 0) {
            FingerEntity entity = new FingerEntity();
            entity.setUserId(System.currentTimeMillis() + "");
            entity.setFeature(data);
            DBUtil.getDBSession().insertOrReplace(entity);

            String path = InitApplication.getTGAPI(mCtx).moniExter3Path + File.separator + FileName.getBatName();
            InitApplication.getTGAPI(mCtx).saveDataToHost(tgHandler, data, path);

            OutputStream os = null;
            try {
                File file = new File(mCtx.getExternalCacheDir() + File.separator + FileName.getBatName());
                if (!file.exists()) {
                    file.createNewFile();
                }
                os = new FileOutputStream(file.getPath());
                os.write(data, 0, data.length);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        InitApplication.getTGAPI(mCtx).closeDev(tgHandler);
        if (null != audioPro) {
            audioPro.release();
        }
        super.onDestroy();
    }

    @Parcel
    public static class FingerPrintType implements Common.Type {
        @NonNull
        @Override
        public AbBindFragment newFragment() {
            return new FingerPrintFragment();
        }

        @NonNull
        @Override
        public String getTag() {
            return FingerPrintFragment.class.getSimpleName();
        }
    }

}
