package com.myboundservice;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private static final String TAG = "MainActivityViewModel";
    private MutableLiveData<MyService.MyBinder> binderMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isProgressUpdating = new MutableLiveData<>();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder binder = (MyService.MyBinder) service;
            binderMutableLiveData.postValue(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binderMutableLiveData.postValue(null);
        }
    };

    public ServiceConnection getServiceConnection(){
        return serviceConnection;
    }

    public LiveData<MyService.MyBinder> getBinder(){
        return binderMutableLiveData;
    }

    public void setIsProgressUpdating(boolean isUpdating){
        isProgressUpdating.setValue(isUpdating);
    }

    public LiveData<Boolean> getProgressUpdating(){
        return isProgressUpdating;
    }



}
