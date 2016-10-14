package com.hevilavio.ardurover.command;

/**
 * Created by hevilavio on 10/3/16.
 */
public class ForwardOrBackwardCommand implements ArduinoCommand {

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

        String wheelSpeed = mappingTOArduinoRange();
        String direction = getDirection(Integer.valueOf(wheelSpeed));

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
    String mappingTOArduinoRange() {

        double value = Math.abs(rawYAxis);

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

        return leftPad(mapped);
    }

    private String leftPad(int mapped) {
        return String.format("%3s", mapped).replace(' ', '0');
    }

    /**
     * According to Y value, return the direction
     * to move the wheels of the rover.
     * */
    String getDirection(int wheelSpeed) {

        // todo: forget wheelSpeed, change it to look to rawYAxis, which vary from -10 to +10
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
