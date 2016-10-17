package com.hevilavio.ardurover.command;

import com.hevilavio.ardurover.util.MotionUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hevilavio on 10/17/16.
 */
public class MotionCommandTest {

    int arduinoPwmLimit = MotionUtils.ARDUINO_PWM_LIMIT;


    @Test
    public void canBuildCommandString() throws Exception {
        String commandString = new MotionCommand(0.6, 0.5).commandString();
        assertEquals("0400100012", commandString);
    }

    @Test
    public void canGetNeutral_FW_Direction() throws Exception {

        String commandString = new MotionCommand(0.6, 0.7).commandString();
        assertEquals("0400140012", commandString);
    }

    @Test
    public void canGetForward_FW_Direction() throws Exception {

        String commandString = new MotionCommand(0.6, 0.8).commandString();
        assertEquals("0410160012", commandString);
    }

    @Test
    public void canGetBackward_FW_Direction() throws Exception {

        String commandString = new MotionCommand(0.6, -0.8).commandString();
        assertEquals("0420160012", commandString);
    }

    @Test
    public void canGetNeutral_LR_Direction() throws Exception {

        String commandString = new MotionCommand(0.7, 0.7).commandString();
        assertEquals("0400140014", commandString);
    }

    @Test
    public void canGetLeft_LR_Direction() throws Exception {

        String commandString = new MotionCommand(0.8, 0.8).commandString();
        assertEquals("0410161016", commandString);
    }

    @Test
    public void canGetBackward_LR_Direction() throws Exception {

        String commandString = new MotionCommand(-0.8, -0.8).commandString();
        assertEquals("0420162016", commandString);
    }

    @Test
    public void canLimitPWM_negativeValues() throws Exception {

        String commandString = new MotionCommand(-6.8, -6.8).commandString();
        assertEquals("0420" + arduinoPwmLimit + "20" + arduinoPwmLimit, commandString);
    }

    @Test
    public void canLimitPWM_PositiveValues() throws Exception {

        String commandString = new MotionCommand(6.8, 6.8).commandString();
        assertEquals("0410" + arduinoPwmLimit + "10" + arduinoPwmLimit, commandString);
    }
}