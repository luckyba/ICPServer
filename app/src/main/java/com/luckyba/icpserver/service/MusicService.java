package com.luckyba.icpserver.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.lucky.icp.IMusicService;
import com.luckyba.icpserver.R;

/**
 * Created by luckyba on 7/17/22.
 */

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private static final int NOTIFICATION_ID = 1;
    private Notification mNotification;

    private IMusicService.Stub mBinder = new IMusicService.Stub() {

        @Override
        public String getSongName() throws RemoteException {
            return mPlayerManager.getSongName();
        }

        @Override
        public void changeMediaStatus() throws RemoteException {
            Log.i(TAG, "changeMediaStatus: ");
            mPlayerManager.changeMediaStatus();
        }

        @Override
        public void playSong() throws RemoteException {
            Log.i(TAG, "playSong: ");
            mPlayerManager.playSong();
        }

        @Override
        public void play() throws RemoteException {

            mPlayerManager.play();
        }

        @Override
        public void pause() throws RemoteException {
            Log.i(TAG, "pause: ");
            mPlayerManager.pause();
        }

        @Override
        public void stopService() throws RemoteException {
            stopSelf();
        }

        @Override
        public int getCurrentDuration() throws RemoteException {
            return mPlayerManager.getCurrentDuration();
        }

        @Override
        public int getTotalDuration() throws RemoteException {
            return mPlayerManager.getTotalDuration();
        }
    };

    private MediaPlayerManager mPlayerManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        mPlayerManager = new MediaPlayerManager(getApplicationContext());
        startForegroundService();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundService() {
        startForeground(NOTIFICATION_ID, getNotification());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Notification getNotification() {
        if (mNotification == null) {
            String title = "IPC Testing";
            try {
                title = mPlayerManager.getSongName();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mNotification = new Notification.Builder(this, createNotificationChannel("music_service", "background_service")).setContentTitle(title)
                    .setContentText("IPC Testing")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
        }
        return mNotification;
    }

    private String createNotificationChannel (String channelId, String channelName) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName,
                NotificationManager.IMPORTANCE_NONE );
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }
}
