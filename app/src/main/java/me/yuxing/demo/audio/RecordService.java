package me.yuxing.demo.audio;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yuxing on 14-2-9.
 */
public class RecordService extends Service {

    private static final String TAG = "RecordService";
    private static final int OneSecond = 1000;
    private MediaRecorder mMediaRecorder;
    private Timer timer;
    private int currentSecond = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        timer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        String filePath = intent.getStringExtra("file_path");
        if (filePath != null) {
            startMediaRecord(filePath);
            timer.schedule(new RecordTimerTask(), OneSecond, OneSecond);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        stopMediaRecord();
        timer.cancel();
    }

    public int getCurrentSecond() {
        return currentSecond;
    }

    public int getMaxAmplitude() {
        if (mMediaRecorder != null) {
            return mMediaRecorder.getMaxAmplitude();
        }

        return 0;
    }

    private void startMediaRecord(String filePath) {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setOutputFile(filePath);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopMediaRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public boolean isRecording() {
        return mMediaRecorder != null;
    }

    public class RecordBinder extends Binder {
        RecordService getService() {
            return RecordService.this;
        }
    }

    private class RecordTimerTask extends TimerTask {

        @Override
        public void run() {
            currentSecond ++;
        }
    }
}
