package com.hevilavio.ardurover.command;

/**
 * Created by hevilavio on 10/26/16.
 */

public class HeartbeatCommand implements ArduinoCommand {
    @Override
    public String name() {
        return "HeartbeatCommand";
    }

    @Override
    public String commandString() {
        return ArduinoCommandsID.HEARTBEAT;
    }
}
