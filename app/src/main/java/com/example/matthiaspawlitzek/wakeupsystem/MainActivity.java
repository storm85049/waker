package com.example.matthiaspawlitzek.wakeupsystem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    NtfListener ntf;
    Button btn;
    NotificationManager nManager;
    Notification n;
    CheckBox onlyWA;
    CheckBox unlock;
    SharedPreferences preferences;
    private SensorManager smgr;
    private Sensor acc;

    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ntf = new NtfListener();
        btn = (Button)findViewById(R.id.btn);
        onlyWA = (CheckBox)findViewById(R.id.onlyWA);
        unlock = (CheckBox)findViewById(R.id.unlock);
        preferences = getPreferences(MODE_PRIVATE);
        onlyWA.setChecked(preferences.getBoolean("onlyWA",false));
        unlock.setChecked(preferences.getBoolean("unlock",false));

        mAccel =0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        smgr = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        acc = smgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(preferences.getBoolean("unlock",false)){
            smgr.registerListener(this,acc,SensorManager.SENSOR_DELAY_NORMAL);
        }

        Intent intent = new Intent(this, NtfListener.class);

        PendingIntent pendingIntent = PendingIntent.getActivities(this, (int) System.currentTimeMillis(), new Intent[]{intent}, 0 );
        n = new Notification.Builder(this)
                .setContentTitle("Test")
                .setContentText("noch ein test")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nManager.notify(0,n);

            }
        });

        onlyWA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("onlyWA",isChecked).commit();
                ntf.setOnlyWA(isChecked);
            }
        });

        unlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("unlock",isChecked).commit();
                ntf.setUnlock(isChecked);
                if(!isChecked){
                    smgr.unregisterListener(MainActivity.this);
                }
                else{
                    smgr.registerListener(MainActivity.this,acc,SensorManager.SENSOR_DELAY_NORMAL);
                }


            }
        });





    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x_ = event.values[0];
        float y_ = event.values[1];
        float z_ = event.values[2];
        Log.i("inside","läuft");

        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float)Math.sqrt(x_*x_ + y_*y_ + z_*z_);
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel*0.09f+delta;

        if(mAccel > 1 && preferences.getBoolean("unlock",false)){
            wakeScreen();
        }


    }

    private void wakeScreen() {
        try{

            PowerManager.WakeLock screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                            |PowerManager.ACQUIRE_CAUSES_WAKEUP
                            |PowerManager.ON_AFTER_RELEASE,"abc");

            screenLock.acquire(500);
            //screenLock.release();
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
