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
        assertEquals("02010", commandString);
    }
}