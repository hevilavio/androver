package com.hevilavio.ardurover.util;

import com.hevilavio.ardurover.command.ArduinoCommand;

/**
 * Created by hevilavio on 10/14/16.
 */

public class MotionUtils {

    private final String onNeutral;
    private final String onPositive;
    private final String onNegative;
    private final int tolerance;

    public MotionUtils(String onNeutral, String onPositive, String onNegative, int tolerance) {
        this.onNeutral = onNeutral;
        this.onPositive = onPositive;
        this.onNegative = onNegative;
        this.tolerance = tolerance;
    }

    public String getDirectionBasedOnSpeed(int speed, double rawAxis){

        if(Math.abs(speed) <= tolerance) return onNeutral;
        if(rawAxis > 0) return onPositive;
        if(rawAxis < 0) return onNegative;

        throw new IllegalStateException("invalid direction, speed=" + speed
                + ",rawAxis=" + rawAxis);
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
    public String mappingTOArduinoRange(double rawAxis) {

        double value = Math.abs(rawAxis);

        int x = (int) (value * 10); // 0~10 will be 0~100
        double a = 0;
        double b = 100;

        double c = 0;
        double d = 200; // actually, is 80. But I want a more smooth throttle
        int mapped = (int) ((x-a)/(b-a) * (d-c) + c);

        // todo: should ARDUINO_PWM_LIMIT be on the class?
        if(mapped > ArduinoCommand.ARDUINO_PWM_LIMIT){
            // TODO: trigger tilt alert
            mapped = ArduinoCommand.ARDUINO_PWM_LIMIT;
        }

        return leftPad(mapped);
    }

    private String leftPad(int mapped) {
        return String.format("%3s", mapped).replace(' ', '0');
    }
}
