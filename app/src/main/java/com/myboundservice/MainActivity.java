package com.myboundservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.myboundservice.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private MyService myService;
    private static final String TAG = "MainActivity";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
    }

    private void initViews() {


        binding.btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleUpdates();
            }
        });

        handler = new Handler();
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        mainViewModel.getBinder().observe(this, myBinder -> {
            if (myBinder != null) {
                myService = myBinder.getService();
            } else {
                myService = null;
            }
        });

        mainViewModel.getProgressUpdating().observe(this, aBoolean -> {
            if (aBoolean) {
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        binding.progressHorizontal.setMax(myService.getMaxValue());
                        if (myService.isPaused()) {
                            binding.btStart.setText("START");
                            handler.removeCallbacks(this);
                            mainViewModel.setIsProgressUpdating(false);
                        } else if (myService.getProgress() > myService.getMaxValue()) {
                            binding.btStart.setText("RESTART");
                            myService.clearTask();
                            handler.removeCallbacks(this);
                        } else {
                            binding.btStart.setText("PAUSE");
                            binding.progressHorizontal.setProgress(myService.getProgress());
                            String value = String.valueOf(100 * myService.getProgress() /
                                    myService.getMaxValue() + "%");
                            binding.tvProgress.setText(value);
                            handler.postDelayed(this, 100);

                        }
                    }
                };

                handler.postDelayed(runnable, 100);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MyService.class);
        startService(intent);

        startService();
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);

        bindService();
    }

    private void bindService() {
        Intent serviceBindIntent = new Intent(this, MyService.class);
        bindService(serviceBindIntent, mainViewModel.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void toggleUpdates() {
        if (myService != null && mainViewModel.getBinder() != null) {
            if (myService.isPaused()) {
                myService.startTask();
                mainViewModel.setIsProgressUpdating(true);
            } else {
                myService.pauseTask();
                mainViewModel.setIsProgressUpdating(false);
            }
        } else {
            Log.i(TAG, "toggleUpdates: bindservice is null");
        }
    }
}