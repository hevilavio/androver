package com.hevilavio.ardurover.util;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.hevilavio.ardurover.RoverControlActivity;


/**
 * Created by hevilavio on 01/10/2016.
 */
public class UIUpdater {

    public void updateText(AppCompatActivity activity, int viewId, String value) {
        ((TextView)activity.findViewById(viewId)).setText(value);
    }

    public void updateMaxThrottle(AppCompatActivity activity, int viewId, boolean isOnLimit) {
        // todo
    }
}
