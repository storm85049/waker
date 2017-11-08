package com.example.matthiaspawlitzek.wakeupsystem;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;


public class NtfListener extends NotificationListenerService {

    private boolean onlyWA;
    private boolean unlock;

    SharedPreferences preferences;

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try{
            String pack = sbn.getPackageName();
            String text ="";
            String title ="";
            Bundle extras = sbn.getNotification().extras;
            text = extras.getCharSequence("android.text").toString();
            title = extras.getString("android.title");

            if(pack.equalsIgnoreCase("com.whatsapp") && onlyWA){
                wakeScreen();
            }
            else if (!onlyWA){
                wakeScreen();
            }

            Log.i("title",title);
            Log.i("text",text);
            Log.i("pack",pack);

        }
        catch(NullPointerException e ){
            e.printStackTrace();
        }


    }

    public void wakeScreen() {

try{

    PowerManager.WakeLock screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    |PowerManager.ACQUIRE_CAUSES_WAKEUP
                    |PowerManager.ON_AFTER_RELEASE,"abc");;
    screenLock.acquire(1000);
    //screenLock.release();
        }
        catch (SecurityException e){
            e.printStackTrace();
        }


    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification was removed");
    }

    public void setOnlyWA(boolean b){
        onlyWA = b;
        Log.i("onlyWA from ntf",onlyWA+"");
    }
    public void setUnlock(boolean b){
        unlock = b;
        Log.i("unlock from ntf",unlock+"");
    }


}
