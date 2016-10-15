package com.hevilavio.ardurover.command;

/**
 * Created by hevilavio on 10/3/16.
 */
public interface ArduinoCommand {

    int ARDUINO_PWM_LIMIT = 80;
    int ABS_LIMIT_BEFORE_MOVING_ROVER = 15;

    String name();

    String commandString();

}
