package com.leonyr.smartipaddemo.nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.leonyr.lib.utils.FileUtil;
import com.leonyr.lib.utils.IntentUtil;
import com.leonyr.lib.utils.NFCUtil;
import com.leonyr.lib.utils.StringUtil;
import com.leonyr.lib.utils.ToastUtil;
import com.leonyr.mvvm.act.AbBindActivity;
import com.leonyr.mvvm.util.RxSchedulers;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.conn.FileName;
import com.leonyr.smartipaddemo.databinding.FmNfcReadBinding;
import com.leonyr.smartipaddemo.home.HomeActivity;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019/7/1
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class NFCActivity extends AbBindActivity<LViewModel, FmNfcReadBinding> {

    Tag tag;
    Disposable disposable;

    @Override
    protected int getLayoutResId() {
        return R.layout.fm_nfc_read;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new NFCUtil(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NFCUtil.mNfcAdapter.enableForegroundDispatch(this, NFCUtil.mPendingIntent, NFCUtil.mIntentFilter, NFCUtil.mTechList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NFCUtil.mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ReadTagAndContent(intent);
    }

    private void ReadTagAndContent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }

        //当该Activity接收到NFC标签时，运行该方法
        /*if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            1，标签读写
            Ndef ndef = Ndef.get(tag);//如果ndef为空表示不支持该格式
            //可进行格式  如果格式化失败则不能只能换个方式
            2，M1 扇区读写
            MifareClassic mfc = MifareClassic.get(tag);//CPU卡时 mfc将为空
            3，CPU卡 读写
            NfcCpuUtilsnfc = new NfcCpuUtils(IsoDep.get(tag));
        }*/

        try {
            String s = NFCUtil.readNFCFromTag(intent);
            if (StringUtil.isNotBlank(s)) {
                Binding().nfcContent.setText(s);
                FileUtil.saveStrToFile(s, mCtx.getExternalCacheDir() + File.separator + FileName.getTextName());
            }

            if (null == tag && StringUtil.isBlank(s)) {
                return;
            }

            if (null != disposable && !disposable.isDisposed()) {
                disposable.dispose();
            }
            disposable = Observable
                    .just(0)
                    .delay(3, TimeUnit.SECONDS)
                    .compose(RxSchedulers.IOMain())
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            onBackPressed();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        Binding().setFm(this);
        ReadTagAndContent(getIntent());
    }

    public void onClick(View view) {
        try {
            if (TextUtils.isEmpty(Binding().nfcWrite.getText())) {
                return;
            }
            String content = Binding().nfcWrite.getText().toString();
            Intent intent = new Intent();
            intent.putExtra(NfcAdapter.EXTRA_TAG, tag);
            NFCUtil.writeNFCToTag(content, intent);
            ToastUtil.showToast(mCtx, "success");
        } catch (Exception e) {
            ToastUtil.showToast(mCtx, "fail");
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (!IntentUtil.isActivityForeground(mCtx, HomeActivity.class.getSimpleName())) {
            Intent i = new Intent(mCtx, HomeActivity.class);
            startActivity(i);
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }
}
