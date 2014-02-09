package me.yuxing.demo.audio.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import me.yuxing.demo.audio.RecordService;

/**
 * Created by yuxing on 2/8/14.
 */
public class TimingView extends TextView{

    private Timer mTimer;
    private Handler mHandler;
    private RecordService mRecordService;

    public TimingView(Context context) {
        this(context, null);
    }

    public TimingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                updateView();
            }
        };
    }

    public void start(RecordService recordService) {
        mRecordService = recordService;
        updateView();
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskImpl(), 1000, 1000);
    }

    public void stop() {
        if (mTimer == null) {
            throw new IllegalStateException("not start");
        }
        mTimer.cancel();
        mTimer = null;
    }

    private void updateView() {
        int second = mRecordService.getCurrentSecond();
        setText(String.format("%02d:%02d", second / 60, second % 60));
    }

    private class TimerTaskImpl extends TimerTask {

        @Override
        public void run() {
            mHandler.sendEmptyMessage(0);
        }
    }
}
