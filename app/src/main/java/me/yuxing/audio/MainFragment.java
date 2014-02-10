package me.yuxing.audio;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.yuxing.audio.widget.TimingView;

public class MainFragment extends Fragment implements View.OnClickListener, ServiceConnection {

    public static final String TAG = "MainFragment";
    private View mAudioStatusView;
    private Button mStartStopButton;
    private boolean mRecording = false;
    private RecorderListFragment mRecorderListFragment;
    private TimingView mTimingView;
    private ProgressBar mTimingProgress;
    private RecordService mRecordService;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mAudioStatusView = rootView.findViewById(R.id.audioStatus);
        mStartStopButton = (Button) rootView.findViewById(R.id.startStop);
        mTimingView = (TimingView) rootView.findViewById(R.id.timing);
        mTimingProgress = (ProgressBar) rootView.findViewById(R.id.timingProgress);
        rootView.findViewById(R.id.startStop).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecorderListFragment = (RecorderListFragment) getFragmentManager().findFragmentById(R.id.recorderList);

        getActivity().bindService(new Intent(getActivity(), RecordService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unbindService(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.startStop) {
            if (mRecording) {
                stopRecord();
            } else {
                startRecord();
            }
        }
    }

    private void startRecord() {

        getActivity().startService(new Intent(getActivity(), RecordService.class)
                .putExtra("file_path", createRecordFilePath()));

        updateRecordStatusView(true);

//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                if (mMediaRecorder != null) {
//                    int maxAmplitude = mMediaRecorder.getMaxAmplitude();
//                    mTimingProgress.setProgress(maxAmplitude);
//                    handler.postDelayed(this, 200);
//                }
//            }
//        };
//        handler.post(runnable);
    }

    private String createRecordFilePath() {
        File file = new File(getActivity().getExternalFilesDir("audio"), new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".amr");
        return file.getAbsolutePath();
    }

    private void stopRecord() {
        getActivity().stopService(new Intent(getActivity(), RecordService.class));
        updateRecordStatusView(false);

        mRecorderListFragment.refresh();
    }

    private void updateRecordStatusView(boolean show) {
        if (show) {
            mRecording = true;
            mStartStopButton.setText(R.string.button_stop);
            showAudioStatusView(true);
        } else {
            mRecording = false;
            mStartStopButton.setText(R.string.button_start);
            showAudioStatusView(false);
        }
    }

    private void showAudioStatusView(boolean show) {

        if (show) {
            mAudioStatusView.setVisibility(View.VISIBLE);
            mAudioStatusView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.audio_status_in));
            mTimingView.start(mRecordService);
        } else {
            mAudioStatusView.setVisibility(View.GONE);
            mAudioStatusView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.audio_status_out));
            mTimingView.stop();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected");
        mRecordService = ((RecordService.RecordBinder) service).getService();

        if (mRecordService.isRecording()) {
            updateRecordStatusView(true);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "onServiceDisconnected");
        mRecordService = null;
    }
}