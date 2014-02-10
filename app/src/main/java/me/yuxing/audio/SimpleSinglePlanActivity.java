package me.yuxing.audio;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by yuxing on 2/7/14.
 */
public abstract class SimpleSinglePlanActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_plan);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, newFragment(savedInstanceState))
                    .commit();
        }
    }

    protected abstract Fragment newFragment(Bundle savedInstanceState);
}
