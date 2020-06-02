package com.leonyr.smartipaddemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.leonyr.mvvm.act.AbBindActivity;
import com.leonyr.mvvm.vm.LViewModel;
import com.leonyr.smartipaddemo.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AbBindActivity<LViewModel, ActivityMainBinding> implements SensorEventListener {

    private SensorManager mSensorManager;

    private Sensor accelerometer; // 加速度传感器
    private Sensor magnetic; // 地磁场传感器

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];

    private static final String TAG = "---MainActivity";


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 初始化加速度传感器
        accelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // 初始化地磁场传感器
        magnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        calculateOrientation();

    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(this, accelerometer, Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, magnetic, Sensor.TYPE_MAGNETIC_FIELD);

        super.onResume();
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

        Log.d(TAG, String.format(Locale.CHINA, "[x: %f, y: %f, z:%f]", values[0], values[1], values[2]));
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

    @Override
    public void onDestroy() {
        if (null != mSensorManager && null != accelerometer) {
            mSensorManager.unregisterListener(MainActivity.this, accelerometer);
        }
        if (null != mSensorManager && null != magnetic) {
            mSensorManager.unregisterListener(MainActivity.this, magnetic);
        }
        super.onDestroy();
    }
}
