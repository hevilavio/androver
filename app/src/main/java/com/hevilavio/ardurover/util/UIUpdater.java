package com.hevilavio.ardurover.util;

import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by hevilavio on 01/10/2016.
 */
public class UIUpdater {

    public void updateText(AppCompatActivity activity, int viewId, String value) {
        ((TextView)activity.findViewById(viewId)).setText(value);
    }

    public void updateThrottleLine(AppCompatActivity activity, int viewId, int speed) {
        View throttleLineView = activity.findViewById(viewId);

        int screenWidth = getScreenWidth(activity);
        int lineWidth = new Mapper().linearMapping(speed, 0, 80, 0, screenWidth - 20);

        RelativeLayout.LayoutParams layoutParams = getLayoutParams(throttleLineView);

        layoutParams.width = lineWidth;
        throttleLineView.setLayoutParams(layoutParams);
    }

    private RelativeLayout.LayoutParams getLayoutParams(View throttleLineView) {
        return (RelativeLayout.LayoutParams) throttleLineView.getLayoutParams();
    }

    private int getScreenWidth(AppCompatActivity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
