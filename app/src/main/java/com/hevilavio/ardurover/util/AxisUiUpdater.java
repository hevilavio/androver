package com.hevilavio.ardurover.util;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


/**
 * Created by hevilavio on 01/10/2016.
 */
public class AxisUiUpdater {

    public void updateText(AppCompatActivity activity, int viewId, String value) {
        ((TextView)activity.findViewById(viewId)).setText(value);
    }
}
