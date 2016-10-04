package com.hevilavio.ardurover.command;

/**
 * Created by hevilavio on 10/3/16.
 */
public class ForwardOrBackwardCommand implements ArduinoCommand {

    // TODO - maybe move to another class
    private final int ARDUINO_PWM_LIMIT = 80;
    private final int ABS_LIMIT_BEFORE_MOVING_ROVER = 15;

    private static final String DIRECTION_NEUTRAL = "0";
    private static final String DIRECTION_CLOCKWISE = "1";
    private static final String DIRECTION_COUNT_CLOCKWISE = "2";

    final double rawYAxis;

    public ForwardOrBackwardCommand(double rawYAxis) {
        this.rawYAxis = rawYAxis;
    }

    @Override
    public String name() {
        return "ForwardOrBackwardCommand";
    }

    @Override
    public String commandString() {

        int wheelSpeed = mappingTOArduinoRange(rawYAxis);
        String direction = getDirection(wheelSpeed);


        return ArduinoCommandsID.ROVER_FORWARD_BACKWARD
                + direction
                + wheelSpeed;
    }


    /**
     * android acelerometer - 0 ~ (10 * 10)
     * arduino analogic - 0 ~ 255
     * for my 5V motors - 0 ~ 160, limited by ARDUINO_PWM_LIMIT
     *
     * Y = (X-A)/(B-A) * (D-C) + C
     * reference: http://stackoverflow.com/questions/345187/math-mapping-numbers
     *
     * */
    private int mappingTOArduinoRange(double rawY) {

        double value = Math.abs(rawY);

        int x = (int) (value * 10); // 0~10 will be 0~100
        double a = 0;
        double b = 100;

        double c = 0;
        double d = 200; // actually, is 80. But I want a more smooth throttle
        int mapped = (int) ((x-a)/(b-a) * (d-c) + c);

        if(mapped > ARDUINO_PWM_LIMIT){
            // TODO: trigger tilt alert
            mapped = ARDUINO_PWM_LIMIT;
        }

        return mapped;
    }

    /**
     * According to Y value, return the direction
     * to move the wheels of the rover.
     * */
    private String getDirection(int wheelSpeed) {

        if(Math.abs(wheelSpeed) <= ABS_LIMIT_BEFORE_MOVING_ROVER) return DIRECTION_NEUTRAL;
        if(rawYAxis > 0) return DIRECTION_CLOCKWISE;
        if(rawYAxis < 0) return DIRECTION_COUNT_CLOCKWISE;

        throw new IllegalStateException("invalid direction, wheelSpeed=" + wheelSpeed);
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
