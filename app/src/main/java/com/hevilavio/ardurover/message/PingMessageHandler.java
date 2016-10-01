package com.hevilavio.ardurover.message;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hevilavio.ardurover.util.Constants;
import com.hevilavio.ardurover.util.TimeUtils;

import java.math.BigDecimal;

/**
 * Created by hevilavio on 07/08/2016.
 *
 * Example of ping message
 * 3+n+3+n
 * *AND179597453598427ARD179597453598427*
 *
 * 01 - type
 * -- values from Android
 * AND  - Sent by Android
 * 179597453598427 - n digits of nanotime (Android)
 *
 * -- Values from Arduino
 * ARD - Arduino received the message
 * 179597453598427 - n digits of nanotim (Arduino)
 *
 */
public class PingMessageHandler extends IntentService implements MessageHandler {


    public PingMessageHandler() {
        super("PingMessageHandler");
    }

    @Override
    public String getType() {
        return "01";
    }

    @Override
    public void onReceive(String content) {
        Log.d(Constants.LOG_TAG, "receiving content[" + content + "]");

        BigDecimal androidSentTime = getAndroidSentTime(content);
        BigDecimal currentTime = new BigDecimal(TimeUtils.getNanoTime());

        String pingValue = currentTime.subtract(androidSentTime).toString();
        sendBroadcastToUI(pingValue);
    }

    private void sendBroadcastToUI(String pingValue) {
        Log.d(Constants.LOG_TAG, "delivering pingValue [" + pingValue + "]");

        Intent pingResult = new Intent(Constants.BROADCAST_ACTION)
                .putExtra(Constants.EXTENDED_PING_VALUE, String.valueOf(pingValue));
        LocalBroadcastManager.getInstance(this).sendBroadcast(pingResult);

    }

    protected BigDecimal getAndroidSentTime(String content) {
        try{
            String substring = content.substring(3, content.indexOf("ARD"));
            return new BigDecimal(substring);
        }catch (NumberFormatException | StringIndexOutOfBoundsException e){
            Log.e(Constants.LOG_TAG, e.getMessage());

            //todo send flush message

            return new BigDecimal(-1);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // nothing
    }
}
