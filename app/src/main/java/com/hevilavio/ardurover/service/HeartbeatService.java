package com.hevilavio.ardurover.service;

import android.app.IntentService;
import android.content.Intent;

import com.hevilavio.ardurover.command.ArduinoCommandSender;
import com.hevilavio.ardurover.command.HeartbeatCommand;

import java.util.concurrent.TimeUnit;

/**
 * Created by hevilavio on 10/26/16.
 */

public class HeartbeatService extends IntentService {

    private final int MILLISECONDS_BETWEEN_BEATS = 500;
    private final ArduinoCommandSender arduinoCommandSender;

    static boolean activeLoop = true;

    public HeartbeatService() {
        super("HeartbeatService");
        arduinoCommandSender = ArduinoCommandSender.getInstance();
    }

    public static void prepareToStop(){
        activeLoop = false;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        while (activeLoop){
            arduinoCommandSender.sendCommand(new HeartbeatCommand());
            sleep();
        }

        activeLoop = true;
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(MILLISECONDS_BETWEEN_BEATS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
