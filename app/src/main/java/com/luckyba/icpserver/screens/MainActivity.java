package com.luckyba.icpserver.screens;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lucky.icp.IMusicService;
import com.luckyba.icpserver.R;
import com.luckyba.icpserver.service.MusicService;

/**
 * Created by luckyba on 7/17/2022.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final long DELAY_INTERVAL = 1000;

    private IMusicService mService;
    private boolean mIsServiceConnected;
    private TextView mTextCurrentDuration;
    private TextView mTextTotalDuration;
    private TextView mTextSongName;
    private Handler mHandler;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = IMusicService.Stub.asInterface(iBinder);
            mIsServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIsServiceConnected = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService();
        initViews();
        updateCurrentProgress();
    }

    private void initViews() {
        findViewById(R.id.image_play).setOnClickListener(this);
        findViewById(R.id.image_pause).setOnClickListener(this);
        mTextCurrentDuration = findViewById(R.id.text_current_duration);
        mTextTotalDuration = findViewById(R.id.text_total_duration);
        mTextSongName = findViewById(R.id.text_name);
    }

    private void updateCurrentProgress() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mTextCurrentDuration.setText(formatDuration(mService.getCurrentDuration()));
                    mTextTotalDuration.setText(formatDuration(mService.getTotalDuration()));
                    mTextSongName.setText(mService.getSongName());
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
                updateCurrentProgress();
            }
        }, DELAY_INTERVAL);
    }

    public String formatDuration(long duration) {
        long absSeconds = Math.abs(duration);
        long hour = absSeconds / 3600;
        long minute = (absSeconds % 3600) / 60;
        long second = absSeconds % 60;
        return String.format("%d:%02d:%02d", hour, minute, second);
    }

    private void bindService() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    public void onClick(View view) {
        if (!mIsServiceConnected) {
            return;
        }
        switch (view.getId()) {
            case R.id.image_play:
                try {
                    mService.play();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.image_pause:
                try {
                    mService.pause();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}
