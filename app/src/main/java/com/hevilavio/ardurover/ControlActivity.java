package com.hevilavio.ardurover;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.hevilavio.ardurover.bluetooth.BTConnectionInterface;
import com.hevilavio.ardurover.message.MessageHandler;
import com.hevilavio.ardurover.message.PingMessageHandler;
import com.hevilavio.ardurover.receiver.PingBroadcastReceiver;
import com.hevilavio.ardurover.router.SimpleMessageRouter;
import com.hevilavio.ardurover.service.PingService;
import com.hevilavio.ardurover.util.Constants;

/**
 * Responsible to the control of the rover.
 *
 * The ping activity
 *
 * */
public class ControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // my code...
        BTConnectionInterface.getInstance().stop();
        BTConnectionInterface.getInstance().start();

        startPingService(); // send ping
        registerPingMessageHandler(); // receive ping
        configurePingResultHandle(); // updateText UI handler


        // change the device name
        String deviceName = BTConnectionInterface.getInstance().getDeviceName();
        ((TextView) findViewById(R.id.txt_connectdto_value)).setText(deviceName);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(Constants.LOG_TAG, "M=onWindowFocusChanged, hasFocus= " + hasFocus);

        if(!hasFocus){
            BTConnectionInterface.getInstance().stop();
        }

    }

    private void startPingService() {
        final Intent pingServiceIntent = new Intent(this, PingService.class);

        Log.d(Constants.LOG_TAG, "M=configurePairButton, starting pingServiceIntent");
        startService(pingServiceIntent);
    }

    private void configurePingResultHandle() {
        Handler updatePingUIHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message inputMessage) {
                TextView textView = (TextView) findViewById(R.id.txt_ping_value);

                String pingValue = (String) inputMessage.obj;
                Log.d(Constants.LOG_TAG, "updating ping value to " + pingValue);
                textView.setText(pingValue + "ms");
            }
        };

        IntentFilter broadcastAction = new IntentFilter(Constants.BROADCAST_ACTION);
        PingBroadcastReceiver pingBroadcastReceiver = new PingBroadcastReceiver(updatePingUIHandler);
        LocalBroadcastManager.getInstance(this).registerReceiver(pingBroadcastReceiver,
                broadcastAction);
    }

    private void registerPingMessageHandler() {
        MessageHandler messageHandler = new PingMessageHandler();
        SimpleMessageRouter.getInstance().registerMessageHandler(messageHandler);
    }
}
