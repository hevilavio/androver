package com.hevilavio.ardurover.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.hevilavio.ardurover.util.Constants;

/**
 * Created by hevilavio on 03/08/2016.
 */
public class PingBroadcastReceiver extends BroadcastReceiver {

    private final Handler handler;

    public PingBroadcastReceiver(Handler updatePingValueHandler) {
        this.handler = updatePingValueHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String stringExtra = intent.getStringExtra(Constants.EXTENDED_PING_VALUE);

        Message message = handler.obtainMessage();
        message.what = 1;
        message.obj = stringExtra; // ping value
        handler.handleMessage(message);
    }
}
