package com.hevilavio.ardurover.command;

import com.hevilavio.ardurover.util.MotionUtils;

/**
 * -- commandType               - 2 NUM
 * -- (for|back)ward            - 1 NUM
 * -- speed (for|back)ward      - 3 NUM
 * -- left|right                - 1 NUM
 * -- speed left|right          - 3 NUM
 *
 * Example>
 * >>0310991066<<
 * 03
 * 1
 * 099
 * 1
 * 066
 *
 */
public class MotionCommand implements ArduinoCommand {

    final double rawYAxis;
    final double rawXAxis;
    final MotionUtils motionUtils;

    public MotionCommand(double rawXAxis, double rawYAxis) {
        this.rawXAxis = rawXAxis;
        this.rawYAxis = rawYAxis;

        this.motionUtils = new MotionUtils();
    }

    @Override
    public String name() {
        return "MotionCommand";
    }

    @Override
    public String commandString() {

        String speedFwBw = motionUtils.getSpeed(rawYAxis);
        String directionFwBw = motionUtils.directionBasedOnRawYAxis(rawYAxis);

        String speedLR = motionUtils.getSpeed(rawXAxis);
        String directionLR = motionUtils.directionBasedOnRawXAxis(rawXAxis);

        return ArduinoCommandsID.ROVER_MOTION
                + directionFwBw
                + speedFwBw
                + directionLR
                + speedLR;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MotionCommand that = (MotionCommand) o;

        if (Double.compare(that.rawYAxis, rawYAxis) != 0) return false;
        return Double.compare(that.rawXAxis, rawXAxis) == 0;

    }
}
