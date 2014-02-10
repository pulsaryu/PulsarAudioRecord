package me.yuxing.audio;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
        cancelNotification();
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

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        showNotification();
        return super.onUnbind(intent);
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

    private void showNotification() {
        Log.d(TAG, "showNotification");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(R.string.app_running_background));
        builder.setContentText("test");
        builder.setSmallIcon(R.drawable.ic_stat_nofication);

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.addCategory("android.intent.category.LAUNCHER");
        resultIntent.setAction("android.intent.action.MAIN");
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private void cancelNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(0);
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
