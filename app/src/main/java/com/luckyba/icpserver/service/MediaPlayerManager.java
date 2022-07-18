package com.luckyba.icpserver.service;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;

import com.lucky.icp.IMusicService;
import com.luckyba.icpserver.R;

/**
 * Created by luckyba on 17/7/2022.
 */

public class MediaPlayerManager implements IMusicService {

    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private String mName;

    public MediaPlayerManager(Context context) {
        mContext = context;
    }

    @Override
    public String getSongName() throws RemoteException {
        return mName;
    }

    @Override
    public void changeMediaStatus() throws RemoteException {
        if (mMediaPlayer == null) {
            return;
        }
        if (mMediaPlayer.isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    @Override
    public void playSong() throws RemoteException {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return;
        }
        playNewSong();
    }

    private void playNewSong() {
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.dung_quen_ten_anh);
        mName = "Đừng quên tên anh";
        mMediaPlayer.start();
    }

    @Override
    public void play() throws RemoteException {
        if (mMediaPlayer == null) {
            playNewSong();
            return;
        }
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void pause() throws RemoteException {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void stopService() throws RemoteException {

    }

    @Override
    public int getCurrentDuration() throws RemoteException {
        try {
            if (mMediaPlayer != null) {
                return mMediaPlayer.getCurrentPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getTotalDuration() throws RemoteException {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public IBinder asBinder() {
        return null;
    }
}
