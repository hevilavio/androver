package com.hevilavio.ardurover.command;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hevilavio on 10/14/16.
 */
public class LeftOrRightCommandTest {

    @Test
    public void canBuildCommandString_Neutral() throws Exception {
        String commandString = new LeftOrRightCommand(0.6).commandString();
        assertEquals("030012", commandString);
    }


    @Test
    public void canBuildCommandString_Left() throws Exception {
        String commandString = new LeftOrRightCommand(-1.5).commandString();
        assertEquals("031030", commandString);
    }

    @Test
    public void canBuildCommandString_Right() throws Exception {
        String commandString = new LeftOrRightCommand(1.5).commandString();
        assertEquals("032030", commandString);
    }

}