package com.hevilavio.ardurover.command;

import com.hevilavio.ardurover.util.MotionUtils;

/**
 * Created by hevilavio on 10/3/16.
 */
public class ForwardOrBackwardCommand implements ArduinoCommand {

    private static final String DIRECTION_NEUTRAL = "0";
    private static final String DIRECTION_CLOCKWISE = "1";
    private static final String DIRECTION_COUNT_CLOCKWISE = "2";

    final double rawYAxis;
    final MotionUtils motionUtils;
    public ForwardOrBackwardCommand(double rawYAxis) {
        this.rawYAxis = rawYAxis;
        motionUtils = new MotionUtils(DIRECTION_NEUTRAL, DIRECTION_CLOCKWISE
                , DIRECTION_COUNT_CLOCKWISE, ABS_LIMIT_BEFORE_MOVING_ROVER);
    }

    @Override
    public String name() {
        return "ForwardOrBackwardCommand";
    }

    @Override
    public String commandString() {

        String wheelSpeed = mappingTOArduinoRange();
        String direction = getDirection(Integer.valueOf(wheelSpeed));

        return ArduinoCommandsID.ROVER_FORWARD_BACKWARD
                + direction
                + wheelSpeed;
    }

    String mappingTOArduinoRange() {
        return motionUtils.mappingTOArduinoRange(rawYAxis);
    }

    /**
     * According to Y value, return the direction
     * to move the wheels of the rover.
     * */
    String getDirection(int wheelSpeed) {
        return motionUtils.getDirectionBasedOnSpeed(wheelSpeed, rawYAxis);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForwardOrBackwardCommand that = (ForwardOrBackwardCommand) o;

        return Double.compare(that.rawYAxis, rawYAxis) == 0;

    }

    @Override
    public String toString() {
        return "ForwardOrBackwardCommand{" +
                "rawYAxis=" + rawYAxis +
                '}';
    }
}
