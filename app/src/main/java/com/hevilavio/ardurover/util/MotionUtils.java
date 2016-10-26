package com.hevilavio.ardurover.util;

import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.util.Pair;

import java.io.InputStream;

/**
 * Created by hevilavio on 10/14/16.
 */

public class MotionUtils {

    public static int ARDUINO_PWM_LIMIT = 80;
    final int ABS_LIMIT_BEFORE_MOVING_ROVER = 15;

    private static final String DIRECTION_NEUTRAL = "0";
    private static final String DIRECTION_FORWARD = "1";
    private static final String DIRECTION_BACKWARD = "2";
    private static final String DIRECTION_LEFT = "1";
    private static final String DIRECTION_RIGHT = "2";

    public String directionBasedOnRawXAxis(double rawXAxis){

        String speed = getSpeed(rawXAxis);
        return getDirectionBasedOnSpeed(Integer.valueOf(speed), rawXAxis,
                DIRECTION_NEUTRAL, DIRECTION_FORWARD, DIRECTION_BACKWARD);
    }

    public String directionBasedOnRawYAxis(double rawYAxis){

        String speed = getSpeed(rawYAxis);
        return getDirectionBasedOnSpeed(Integer.valueOf(speed), rawYAxis,
                DIRECTION_NEUTRAL, DIRECTION_LEFT, DIRECTION_RIGHT);
    }

    private String getDirectionBasedOnSpeed(int speed, double rawAxis, String onNeutral,
                                           String onPositive, String onNegative){

        if(Math.abs(speed) <= ABS_LIMIT_BEFORE_MOVING_ROVER) return onNeutral;
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
    public String getSpeed(double rawAxis) {

        double value = Math.abs(rawAxis);

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

    public boolean isGreaterOrEqualsLimit(Integer speed){
        return speed >= ARDUINO_PWM_LIMIT;
    }

    private String leftPad(int mapped) {
        return String.format("%3s", mapped).replace(' ', '0');
    }
}
