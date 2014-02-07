package me.yuxing.demo.audio;

import android.app.Fragment;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainFragment extends Fragment implements View.OnClickListener {

    private View mAudioStatusView;
    private Button mStartStopButton;
    private boolean mRecording = false;
    private MediaRecorder mMediaRecorder;
    private RecorderListFragment mRecorderListFragment;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mAudioStatusView = rootView.findViewById(R.id.audioStatus);
        mStartStopButton = (Button) rootView.findViewById(R.id.startStop);

        rootView.findViewById(R.id.startStop).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecorderListFragment = (RecorderListFragment) getFragmentManager().findFragmentById(R.id.recorderList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.startStop) {
            mRecording = !mRecording;
            if (mRecording) {
                startRecord();
            } else {
                stopRecord();
            }
        }
    }

    private void startRecord() {
        mStartStopButton.setText(R.string.button_stop);
        showAudioStatusView(true);

        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        File file = new File(getActivity().getExternalFilesDir("audio"), new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".amr");

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecord() {
        mStartStopButton.setText(R.string.button_start);
        showAudioStatusView(false);

        mMediaRecorder.stop();
        mMediaRecorder.reset();

        mRecorderListFragment.refresh();
    }

    private void showAudioStatusView(boolean show) {

        if (show) {
            mAudioStatusView.setVisibility(View.VISIBLE);
            mAudioStatusView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.audio_status_in));
        } else  {
            mAudioStatusView.setVisibility(View.GONE);
            mAudioStatusView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.audio_status_out));
        }
    }
}