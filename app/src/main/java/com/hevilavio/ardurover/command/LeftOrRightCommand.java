package com.hevilavio.ardurover.command;

import com.hevilavio.ardurover.util.MotionUtils;

/**
 * Created by hevilavio on 10/14/16.
 */

public class LeftOrRightCommand implements ArduinoCommand {

    private static final String DIRECTION_NEUTRAL = "0";
    private static final String DIRECTION_LEFT = "1";
    private static final String DIRECTION_RIGHT = "2";

    final double rawXAxis;
    final MotionUtils motionUtils;

    public LeftOrRightCommand(double rawXAxis) {
        this.rawXAxis = rawXAxis;
        motionUtils = new MotionUtils(DIRECTION_NEUTRAL, DIRECTION_RIGHT, DIRECTION_LEFT
                , ABS_LIMIT_BEFORE_MOVING_ROVER);
    }

    @Override
    public String name() {
        return "LeftOrRightCommand";
    }

    @Override
    public String commandString() {

        String speed = motionUtils.mappingTOArduinoRange(rawXAxis);
        String direction = motionUtils.getDirectionBasedOnSpeed(Integer.valueOf(speed), rawXAxis);

        return ArduinoCommandsID.ROVER_LEFT_RIGHT
                + direction
                + speed;
    }
}
