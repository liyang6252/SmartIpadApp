package com.leonyr.smartipaddemo.crop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.leonyr.lib.luban.Luban;
import com.leonyr.lib.utils.FileUtil;
import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.SPUtils;
import com.leonyr.lib.utils.ToastUtil;
import com.leonyr.mvvm.act.AbBindActivity;
import com.leonyr.mvvm.util.RxSchedulers;
import com.leonyr.smartipaddemo.R;
import com.leonyr.smartipaddemo.conn.FileName;
import com.leonyr.smartipaddemo.databinding.ActivityCameraBinding;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.SessionType;
import com.otaliastudios.cameraview.Size;
import com.otaliastudios.cameraview.SizeSelector;
import com.otaliastudios.cameraview.SizeSelectors;
import com.otaliastudios.cameraview.VideoQuality;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class CameraActivity extends AbBindActivity<CameraViewModel, ActivityCameraBinding> implements SensorEventListener {

    public static final String OUTPUT = "Camera_output";
    public static final String CAMERA_TYPE = "camera_type";

    public static final int TYPE_PHOTO = 0x88;
    public static final int TYPE_RECORD = 0x89;

    private static final String CAMERA_SCREEN_TYPE_PHOTO = "camera_screen_type_photo";
    private static final String CAMERA_SCREEN_TYPE_VIDEO = "camera_screen_type_video";

    private List<VideoQuality> videoSizes;
    private VideoQuality typeVideoSize;

    private List<SizeSelector> picSizes;
    private SizeSelector typePicSize;


    private boolean isPreview = false, isRequest = false;
    private boolean isRecord = false;

    private int rotateInt = 0;

    private SensorManager mSensorManager;

    private Sensor accelerometer; // 加速度传感器
    private Sensor magnetic; // 地磁场传感器

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];

    private String tempPath;
    private int CameraType = TYPE_PHOTO;
    Disposable timeRecord;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        Binding().setFm(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 初始化加速度传感器
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 初始化地磁场传感器
        magnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        FileName.deleteOverFile(mCtx);

        Bundle arg1 = getIntent().getExtras();
        if (null != arg1) {
            tempPath = arg1.getString(OUTPUT, "");
            CameraType = arg1.getInt(CAMERA_TYPE, TYPE_PHOTO);
        }

//        initSeekBar();
        calculateOrientation();

        Binding().camera.addCameraListener(new CameraListener() {
            @Override
            public void onCameraOpened(CameraOptions options) {
                super.onCameraOpened(options);
                onClick(Binding().screenRotateH);
                initSizeList();
                initTypeScreenSize();
            }

            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);

                LogUtil.e("onPictureTaken end");

//                if (TextUtils.isEmpty(tempPath)) {
                tempPath = mCtx.getExternalFilesDir("cache") + File.separator + FileName.getPhotoJpgName();
//                }

                if (jpeg.length > 0) {
                    Observable.
                            fromCallable(new Callable<Bitmap>() {
                                @Override
                                public Bitmap call() throws Exception {
                                    return BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                                }
                            })
                            .map(new Function<Bitmap, Bitmap>() {
                                @Override
                                public Bitmap apply(Bitmap bitmap) throws Exception {
                                    LogUtil.e("onPictureTaken map1");
                                    Matrix m = new Matrix();

                                    m.setScale(-1, 1);//水平翻转
//                                    m.setScale(1, -1);//垂直翻转
                                    int w = bitmap.getWidth();
                                    int h = bitmap.getHeight();
                                    //生成的翻转后的bitmap
                                    return Bitmap.createBitmap(bitmap, 0, 0, w, h, m, true);
                                }
                            })
                            .map(new Function<Bitmap, String>() {
                                @Override
                                public String apply(Bitmap bitmap) throws Exception {
                                    LogUtil.e("onPictureTaken map2");
                                    FileOutputStream out = new FileOutputStream(tempPath);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                    out.flush();
                                    out.close();
                                    return tempPath;
                                }
                            })
                            .map(new Function<String, String>() {
                                @Override
                                public String apply(String path) throws Exception {
                                    LogUtil.e("onPictureTaken map3");
                                    // 使用luban压缩图片
                                    /*List<File> fileList = Luban.with(mCtx).load(path).setTargetDir(mCtx.getExternalFilesDir("cache").getPath()).ignoreBy(400).get();
                                    if (fileList.size() > 0){
                                        File newFile = fileList.get(0);
                                        LogUtil.e("note 1: "+newFile.getPath());
                                        if (!newFile.getName().startsWith("photo")){
                                            File oldFile = new File(path);
                                            oldFile.deleteOnExit();
                                            LogUtil.e("note 2: "+oldFile.getPath());
                                            FileUtil.moveFile(newFile.getPath(), path);
                                            return oldFile.getName();
                                        }
                                    }*/
                                    LogUtil.e(path);
                                    return new File(path).getName();
                                }
                            })
                            .compose(RxSchedulers.IOMain())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String name) throws Exception {
                                    LogUtil.e("onPictureTaken subscribe");
                                    tempPath = new File(mCtx.getExternalCacheDir(), name).getPath();
                                    LogUtil.e("result: " + tempPath);
                                    FileUtil.moveFile(new File(mCtx.getExternalFilesDir("cache"), name), new File(tempPath));
                                    Intent intent = new Intent();
                                    intent.putExtra(OUTPUT, tempPath);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                }
                            });
                }
            }

            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
                Intent intent = new Intent();
                intent.putExtra(OUTPUT, video.getPath());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        Binding().camera.setVideoMaxDuration(60 * 1000);
//        Binding().camera.setVideoQuality(VideoQuality.MAX_720P);
        Binding().camera.setJpegQuality(60);

        Binding().screenLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int type = 1;
                switch (checkedId) {
                    case R.id.screen_high:
                        type = 0;
                        break;
                    case R.id.screen_middle:
                        type = 1;
                        break;
                    case R.id.screen_low:
                        type = 2;
                        break;
                }

                if (CameraType == TYPE_PHOTO) {
                    typePicSize = picSizes.get(type);
                    Binding().camera.setPictureSize(picSizes.get(type));
                    SPUtils.getInstance().put(CAMERA_SCREEN_TYPE_PHOTO, type);
                } else {
                    typeVideoSize = videoSizes.get(type);
                    Binding().camera.setVideoQuality(typeVideoSize);
                    SPUtils.getInstance().put(CAMERA_SCREEN_TYPE_VIDEO, type);
                }
            }
        });
    }

    private void initSizeList() {
        if (CameraType == TYPE_PHOTO) {
            Binding().takeRecord.setVisibility(View.GONE);
            Binding().takePhoto.setVisibility(View.VISIBLE);
            picSizes = new ArrayList<>(3);
            picSizes.add(SizeSelectors.minHeight(2500));
            picSizes.add(SizeSelectors.betweenHeight(1800, 2500));
            picSizes.add(SizeSelectors.maxHeight(1800));
            Binding().camera.setSessionType(SessionType.PICTURE);
            Binding().screenHigh.setText("高");
            Binding().screenMiddle.setText("中");
            Binding().screenLow.setText("低");
        } else {
            Binding().takePhoto.setVisibility(View.GONE);
            Binding().takeRecord.setVisibility(View.VISIBLE);
            videoSizes = new ArrayList<>(3);
            videoSizes.add(VideoQuality.MAX_1080P);
            videoSizes.add(VideoQuality.MAX_720P);
            videoSizes.add(VideoQuality.MAX_480P);
            Binding().camera.setSessionType(SessionType.VIDEO);
            Binding().screenHigh.setText("高(" + videoSizes.get(0).toString() + ")");
            Binding().screenMiddle.setText("中(" + videoSizes.get(1).toString() + ")");
            Binding().screenLow.setText("低(" + videoSizes.get(2).toString() + ")");
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.screen_rotate:
                int a = rotateInt % 4;
                switch (a) {
                    case 0:
                        Binding().camera.setRotation(90);
                        break;
                    case 1:
                        Binding().camera.setRotation(180);
                        break;
                    case 2:
                        Binding().camera.setRotation(270);
                        break;
                    case 3:
                        Binding().camera.setRotation(0);
                        break;
                }
                rotateInt++;
//                ToastUtil.showToast(mCtx, "" + Binding().camera.getTotalFps());
                break;
            case R.id.screen_rotate_h:
                if (Binding().camera.getScaleX() == -1) {
                    Binding().camera.setScaleX(1);
                } else {
                    Binding().camera.setScaleX(-1);
                }
                break;
            case R.id.take_photo:
                LogUtil.e("onPictureTaken start");
                Binding().camera.capturePicture();
//                Binding().camera.captureSnapshot();
                Binding().takePhoto.setEnabled(false);
                break;
            case R.id.take_record:
                if (!isRecord) {
                    if (TextUtils.isEmpty(tempPath)) {
                        tempPath = mCtx.getExternalCacheDir() + File.separator + FileName.getMovieName();
                    }
                    Binding().camera.startCapturingVideo(new File(tempPath));
                    isRecord = true;
                    initTimeRecord();
                } else {
                    if (!timeRecord.isDisposed()) {
                        timeRecord.dispose();
                    }
//                    FileUtils.releaseFile();
                    Binding().camera.stopCapturingVideo();
                    Binding().takeRecord.setEnabled(false);
                    ToastUtil.showToast(mCtx, "success");
                    isRecord = false;

                }
                break;
        }
    }

    private void initSeekBar() {
//        Binding().seekbarBrightness.setOnSeekBarChangeListener(new SeekBarChangeListener(UVCCamera.PU_BRIGHTNESS));
//        Binding().seekbarContrast.setOnSeekBarChangeListener(new SeekBarChangeListener(UVCCamera.PU_CONTRAST));
        /*Binding().seekbarGamma.setOnSeekBarChangeListener(new SeekBarChangeListener(UVCCamera.PU_GAMMA));
        Binding().seekbarWbTemp.setOnSeekBarChangeListener(new SeekBarChangeListener(UVCCamera.PU_WB_TEMP));
        Binding().seekbarSatura.setOnSeekBarChangeListener(new SeekBarChangeListener(UVCCamera.PU_SATURATION));
        Binding().seekbarHue.setOnSeekBarChangeListener(new SeekBarChangeListener(UVCCamera.PU_HUE));
        Binding().seekbarSharpness.setOnSeekBarChangeListener(new SeekBarChangeListener(UVCCamera.PU_SHARPNESS));
        Binding().seekbarBacklight.setOnSeekBarChangeListener(new SeekBarChangeListener(UVCCamera.PU_BACKLIGHT));*/
    }

    private void initTimeRecord() {
        timeRecord = Observable
                .interval(1, TimeUnit.SECONDS)
                .delay(3000, TimeUnit.MILLISECONDS)
                .compose(RxSchedulers.IOMain())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Binding().takeRecord.setText(String.format(Locale.CHINA, "%02d:%02d", aLong / 60, aLong % 60));
                        if (aLong > 60) {
                            timeRecord.dispose();
//                            mHelper.stopPusher();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (mHelper != null) {
//            mHelper.registerUSB();
//        }
    }

    @Override
    protected void onResume() {
        Binding().camera.start();
        mSensorManager.registerListener(this, accelerometer, Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, magnetic, Sensor.TYPE_MAGNETIC_FIELD);
        super.onResume();
    }

    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        private int type;

        public SeekBarChangeListener(int type) {
            this.type = type;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
           /* if (mHelper != null && mHelper.isCameraOpened()) {
                mHelper.setModelValue(type, progress);
            }*/
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    // 计算方向
    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);

        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);

//        Log.d(TAG, String.format(Locale.CHINA, "[x: %f, y: %f, z:%f]", values[0], values[1], values[2]));
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values;
        }
        calculateOrientation();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void initTypeScreenSize() {
        int type = 1;
        if (CameraType == TYPE_PHOTO) {
            type = SPUtils.getInstance().getInt(CAMERA_SCREEN_TYPE_PHOTO, 1);
            typePicSize = picSizes.get(type);
            Binding().camera.setPictureSize(typePicSize);
        } else {
            type = SPUtils.getInstance().getInt(CAMERA_SCREEN_TYPE_VIDEO, 1);
            typeVideoSize = videoSizes.get(type);
            Binding().camera.setVideoQuality(typeVideoSize);
        }

        switch (type) {
            case 0:
                Binding().screenHigh.setChecked(true);
                break;
            case 1:
                Binding().screenMiddle.setChecked(true);
                break;
            case 2:
                Binding().screenLow.setChecked(true);
                break;
            default:
                Binding().screenMiddle.setChecked(true);
                break;
        }
    }

    private void saveYuv2Jpeg(String path, byte[] data, int mWidth, int mHeight) {
        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            boolean result = yuvImage.compressToJpeg(new Rect(0, 0, mWidth, mHeight), 70, bos);
            if (result) {
                byte[] buffer = bos.toByteArray();
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                FileOutputStream fos = null;
                fos = new FileOutputStream(file);
                // fixing bm is null bug instead of using BitmapFactory.decodeByteArray
                fos.write(buffer);
                fos.close();
            }
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        Binding().camera.stop();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        try {
            Binding().camera.destroy();
            if (null != mSensorManager && null != accelerometer) {
                mSensorManager.unregisterListener(CameraActivity.this, accelerometer);
            }
            if (null != mSensorManager && null != magnetic) {
                mSensorManager.unregisterListener(CameraActivity.this, magnetic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
