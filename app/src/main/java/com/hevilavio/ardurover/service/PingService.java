package com.hevilavio.ardurover.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.hevilavio.ardurover.bluetooth.BTConnectionInterface;
import com.hevilavio.ardurover.util.Constants;
import com.hevilavio.ardurover.util.TimeUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by hevilavio on 03/08/2016.
 */
@Deprecated
public class PingService extends IntentService {

    public PingService() {
        super("PingService");
    }

    @Override
    public void onDestroy() {
        Log.i(Constants.LOG_TAG, "M=onDestroy, finishing...");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        boolean shouldContinue = true;
        while (shouldContinue){
//            Log.i(Constants.LOG_TAG, "M=onHandleIntent, pingValue=" + pingValue);
//
//            Intent pingResult = new Intent(Constants.BROADCAST_ACTION)
//                    .putExtra(Constants.EXTENDED_PING_VALUE, String.valueOf(pingValue));
//
//            LocalBroadcastManager.getInstance(this).sendBroadcast(pingResult);

            int writeResult = BTConnectionInterface.getInstance()
                    .write("01AND" + TimeUtils.getNanoTime() + "ARD");

            shouldContinue = writeResult == 0;

            sleep();
        }
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
