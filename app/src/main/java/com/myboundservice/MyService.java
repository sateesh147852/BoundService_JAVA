package com.myboundservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyService extends Service {

    private MyBinder myBinder = new MyBinder();
    private final String TAG = "MyService";
    private Handler handler;
    private int maxValue;
    private int progress;
    private boolean isPaused;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        maxValue = 5000;
        progress = 0;
        isPaused = true;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public void clearTask(){
        progress = 0;
        isPaused = true;
        updateTask();
    }

    public int getMaxValue(){
        return maxValue;
    }

    public boolean isPaused(){
        return isPaused;
    }

    public int getProgress(){
        return progress;
    }

    public void pauseTask(){
        isPaused = true;
        updateTask();
    }

    public void startTask(){
        isPaused = false;
        updateTask();
    }


    private void updateTask(){
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isPaused || progress > maxValue){
                    handler.removeCallbacks(this);
                    Log.i(TAG, "run: "+progress +  "  "+isPaused);
                }
                else {
                    progress += 100;
                    handler.postDelayed(this,100);
                }
            }
        };
        handler.postDelayed(runnable,100);
    }


    public class MyBinder extends Binder{

        MyService getService(){
            return MyService.this;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
