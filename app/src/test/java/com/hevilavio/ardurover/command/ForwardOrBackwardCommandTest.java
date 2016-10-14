package com.hevilavio.ardurover.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hevilavio on 10/3/16.
 */
public class ForwardOrBackwardCommandTest {

    @Test
    public void canBuildCommandString() throws Exception {
        String commandString = new ForwardOrBackwardCommand(0.5).commandString();
        assertEquals("020010", commandString);
    }

    @Test
    public void canGetNeutralDirection() throws Exception {

        int rawYAxis = ArduinoCommand.ABS_LIMIT_BEFORE_MOVING_ROVER;
        String direction = new ForwardOrBackwardCommand(rawYAxis).getDirection(
                rawYAxis);

        assertEquals("0", direction);
    }


    @Test
    public void canGetForwardDirection() throws Exception {

        int rawYAxis = ArduinoCommand.ABS_LIMIT_BEFORE_MOVING_ROVER + 10;
        String direction = new ForwardOrBackwardCommand(
                rawYAxis).getDirection(rawYAxis);

        assertEquals("1", direction);
    }


    @Test
    public void canGetBackwardDirection() throws Exception {
        int rawYAxis = ArduinoCommand.ABS_LIMIT_BEFORE_MOVING_ROVER + 10;

        rawYAxis *= -1;

        String direction = new ForwardOrBackwardCommand(
                rawYAxis).getDirection(rawYAxis);

        assertEquals("2", direction);
    }

    @Test
    public void canMapSpeedWithLeftPad_1() throws Exception {

        String direction = new ForwardOrBackwardCommand(
                1.6).mappingTOArduinoRange();

        assertEquals("032", direction);
    }

    @Test
    public void canMapSpeedWithLeftPad_2() throws Exception {

        String direction = new ForwardOrBackwardCommand(
                3.6).mappingTOArduinoRange();

        assertEquals("072", direction);
    }
}