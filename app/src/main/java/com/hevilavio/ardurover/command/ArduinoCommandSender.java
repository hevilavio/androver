package com.hevilavio.ardurover.command;

import android.util.Log;

import com.hevilavio.ardurover.bluetooth.BTConnectionInterface;
import com.hevilavio.ardurover.util.Constants;

/**
 * Created by hevilavio on 10/3/16.
 */
public class ArduinoCommandSender {

    private static ArduinoCommandSender instance;
    private final BTConnectionInterface btConnectionInterface;

    ArduinoCommandSender(BTConnectionInterface btConnectionInterface){
        this.btConnectionInterface = btConnectionInterface;
    }

    public synchronized static ArduinoCommandSender getInstance(){
        if(instance == null){
            instance = new ArduinoCommandSender(BTConnectionInterface.getInstance());
        }

        return instance;
    }


    public void sendCommand(ArduinoCommand command){
        if(command == null) throw new IllegalArgumentException("command cannot be null");

        log(command);

        btConnectionInterface.write(command.commandString());
    }

    private void log(ArduinoCommand arduinoCommand) {
        Log.d(Constants.LOG_TAG, "M=sendCommand, about to send ["
                + arduinoCommand.commandString()
                + "] as a ["
                + arduinoCommand.name() + "]");
    }

}
