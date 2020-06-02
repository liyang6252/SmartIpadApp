package com.leonyr.smartipaddemo.home;

import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.View;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.leonyr.calendarview.CalendarDay;
import com.leonyr.calendarview.MaterialCalendarView;
import com.leonyr.calendarview.OnDateSelectedListener;
import com.leonyr.calendarview.format.DateFormatTitleFormatter;
import com.leonyr.lib.utils.DataCleanManager;
import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.SPUtils;
import com.leonyr.lib.utils.StringUtil;
import com.leonyr.lib.utils.ToastUtil;
import com.leonyr.mvvm.act.AbBindActivity;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.BuildConfig;
import com.leonyr.smartipaddemo.NewCommon;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.USBLinkService;
import com.leonyr.smartipaddemo.conn.APPContant;
import com.leonyr.smartipaddemo.conn.FileName;
import com.leonyr.smartipaddemo.conn.USBCommandUtil;
import com.leonyr.smartipaddemo.crop.MediaFragment;
import com.leonyr.smartipaddemo.databinding.ActivityHomeBinding;
import com.leonyr.smartipaddemo.finger.FingerPrintFragment;
import com.leonyr.smartipaddemo.nfc.NFCActivity;
import com.leonyr.smartipaddemo.password.PasswordInputFragment;
import com.leonyr.smartipaddemo.set.LightFragment;
import com.leonyr.smartipaddemo.set.SettingFragment;
import com.leonyr.smartipaddemo.sign.SignFileFragment;
import com.leonyr.smartipaddemo.sign.SignHomeFragment;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.drakeet.multitype.MultiTypeAdapter;

public class HomeActivity extends AbBindActivity<HomeViewModel, ActivityHomeBinding> implements USBLinkService.IUSBReceiveListener {

    private static final int REQUEST_NFC_CODE = 0x3000;

    private static String TIME_FORMAT_CALENDAR_HEADER = "yyyy年 MM月";
    private static String TIME_FORMAT_TODAY = "MM月dd日";
    private SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_CALENDAR_HEADER, Locale.CHINA);

//    private USBLinkBroadCastReceiver linkBroadCastReceiver;
    MultiTypeAdapter mAdapter;
    USBLinkService.USBLinkBinder usbLinkBinder;
    SignHomeFragment signHomeFragment ;

    private FingerPrintFragment.FingerPrintType fingerPrintType;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        USBCommandUtil.register(this);
        /*linkBroadCastReceiver = new USBLinkBroadCastReceiver();
        registerReceiver(linkBroadCastReceiver, USBCommandUtil.getCommandFilter());*/
        setVModel(LViewModel.create(this, HomeViewModel.class));

        Binding().homeRl.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new MultiTypeAdapter();
        mAdapter.register(HomeModel.class, new HomeAdapter(getItemFuncClick()));

        Date today = new Date();
        Binding().homeDate.setText(new SimpleDateFormat(TIME_FORMAT_TODAY, Locale.CHINA).format(today));
        Binding().homeWeek.setText(getVModel().getWeek());

        Binding().homeRl.setAdapter(mAdapter);
        getVModel().FuncList.observe(this, getFuncListObserve());

        Binding().homeCv.setTitleFormatter(new DateFormatTitleFormatter(DateTimeFormatter.ofPattern("yyyy年 MM月")));
        Binding().homeCv.setLeftArrow(R.drawable.transparent);
        Binding().homeCv.setRightArrow(R.drawable.transparent);
        Binding().homeCv.setPagingEnabled(false);
        Binding().homeCv.setSelectedDate(LocalDate.now());
//        Binding().homeCv.addDecorator(new CalendarTodayDecorator(mCtx));
        Binding().homeCv.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    getVModel().goToCalendar(mCtx);
                }
            }
        });

        Binding().homeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Binding().mainDrawer.isDrawerOpen(Gravity.START)) {
                    Binding().mainDrawer.closeDrawer(Gravity.START);
                } else {
                    Binding().mainDrawer.openDrawer(Gravity.START);
                }
            }
        });

        Binding().versionTv.setText(BuildConfig.CHANNEL + "  " + BuildConfig.VERSION_NAME);

        /*BroadcastReceiver receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.e("BootReceiver", action);
                if (action.equals(Intent.ACTION_DATE_CHANGED)) {
                    Binding().homeCv.setSelectedDate(LocalDate.now());
                }
            }

        };

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_DATE_CHANGED);
        registerReceiver(receiver, intentFilter);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        getVModel().loadData();
    }

    private Observer<List<HomeModel>> getFuncListObserve() {
        return new Observer<List<HomeModel>>() {
            @Override
            public void onChanged(@Nullable List<HomeModel> models) {
                mAdapter.setItems(models);
                mAdapter.notifyDataSetChanged();
            }
        };
    }

    @Subscribe(tags = {@Tag(APPContant.USB_COMMAND_TAG)})
    @Override
    public void receiveInfo(String commandId) {
        if (commandId.equals(APPContant.DATE_CHANGED)){
            Binding().homeCv.setSelectedDate(LocalDate.now());
        }else {
            gotoFunPage(commandId);
        }
    }

    private HomeAdapter.OnHomeItemFuncClick getItemFuncClick() {
        return new HomeAdapter.OnHomeItemFuncClick() {
            @Override
            public void onItemFuncClick(View view, HomeModel item) {
                gotoFunPage(item.getFuncCode());
//                gotoFunPage(APPContant.USB_NOTIFY_FILE_SIGNER);
            }
        };
    }

    public void gotoFunPage(String funCode) {
        FileName.deleteOverFile(mCtx);
        if (Binding().mainDrawer.isDrawerOpen(Gravity.START)) {
            Binding().mainDrawer.closeDrawer(Gravity.START);
        }

        if (signHomeFragment != null && signHomeFragment.isVisible()){
            signHomeFragment.dismiss();
        }

        if (null != NewCommon.INSTANCE){
            NewCommon.INSTANCE.finish();
        }

        switch (funCode) {
            case APPContant.USB_NOTIFY_TAKE_SIGNATURE://手动签名
                if (signHomeFragment == null) {
                    signHomeFragment = new SignHomeFragment();
                }else{
                    signHomeFragment.clear();
                }
                signHomeFragment.show(getSupportFragmentManager(), "sign");
                break;
            case APPContant.USB_NOTIFY_TAKE_PHOTO:
            case APPContant.USB_NOTIFY_TAKE_MOVIE:
            case APPContant.USB_NOTIFY_TAKE_AUDIO:
                NewCommon.start(mCtx, new MediaFragment.MediaType());
                break;
            case APPContant.USB_NOTIFY_TAKE_FINGER_PRINT://指静脉识别
                if (null == fingerPrintType) {
                    fingerPrintType = new FingerPrintFragment.FingerPrintType();
                }
                if (SPUtils.getInstance().getBoolean(APPContant.SP_SWITCH_FINGER_PRINT, false)) {
                    NewCommon.start(mCtx, fingerPrintType);
                } else {
                    ToastUtil.showToast(mCtx, "指静脉识别模块已关闭，请先在设置中打开！");
                }
                break;
            case APPContant.USB_NOTIFY_TAKE_NFC://nfc打卡
                startActivity(new Intent(mCtx, NFCActivity.class));
                break;
            case APPContant.USB_NOTIFY_TAKE_PASSWORD://口令功能
                NewCommon.startForResult(HomeActivity.this, new PasswordInputFragment.PasswordInputType(), REQUEST_NFC_CODE);
//                        PowerManager pm = (PowerManager) mCtx.getSystemService(Context.POWER_SERVICE);
//                        pm.reboot("");
                break;
            case APPContant.USB_NOTIFY_TAKE_SETTING://软件设置
                NewCommon.start(mCtx, new SettingFragment.SettingType());
                break;
            case APPContant.USB_NOTIFY_TAKE_CLEAN://缓存清理
                DataCleanManager.clearAllCache(mCtx);
                break;
            case APPContant.USB_NOTIFY_DOWNLOAD_FINGER:
                getVModel().readDownloadData();
                break;
            case APPContant.USB_NOTIFY_INSTALL_APP:
                getVModel().installApp();
                break;
            case APPContant.USB_NOTIFY_TAKE_LIGHT:
                NewCommon.start(mCtx, new LightFragment.LightType());
                break;
            case APPContant.USB_NOTIFY_PLAY_AUDIO:
                getVModel().playAudioMedia();
                break;
            case APPContant.USB_NOTIFY_TAKE_FINGER:
                getVModel().exportFingerData();
                break;
            case APPContant.USB_NOTIFY_FILE_SIGNER:
                String file = getVModel().signFile();
                if (StringUtil.isNotBlank(file)) {
                    NewCommon.start(mCtx, new SignFileFragment.SignFileType(file));
                }
                break;
        }
    }

    public class USBLinkBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.e("tag", action);
            gotoFunPage(action);
        }
    }

    private ServiceConnection usbLinkConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            usbLinkBinder = (USBLinkService.USBLinkBinder) service;
            usbLinkBinder.setListener(HomeActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            usbLinkBinder = null;
        }
    };


    @Override
    public void onDestroy() {
        if (USBLinkService.isRuning) {
            unbindService(usbLinkConnection);
        }
        USBCommandUtil.unRegister(this);
        /*if (null != linkBroadCastReceiver) {
            unregisterReceiver(linkBroadCastReceiver);
        }*/
        super.onDestroy();
    }
}
