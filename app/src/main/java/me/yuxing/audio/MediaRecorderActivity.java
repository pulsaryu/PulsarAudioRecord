package me.yuxing.audio;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by yuxing on 2/7/14.
 */
public class MediaRecorderActivity extends SimpleSinglePlanActivity {
    @Override
    protected Fragment newFragment(Bundle savedInstanceState) {
        return new MediaRecorderFragment();
    }
}
