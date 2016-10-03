package com.hevilavio.ardurover.command;

/**
 * Created by hevilavio on 10/3/16.
 */
public class ForwardOrBackwardCommand implements ArduinoCommand {

    final int wheelSpeed;
    final String direction;

    public ForwardOrBackwardCommand(int wheelSpeed, String direction) {
        this.wheelSpeed = wheelSpeed;
        this.direction = direction;
    }

    @Override
    public String name() {
        return "ForwardOrBackwardCommand";
    }

    @Override
    public String commandString() {
        return ArduinoCommandsID.ROVER_FORWARD_BACKWARD
                + direction
                + wheelSpeed;
    }
}
