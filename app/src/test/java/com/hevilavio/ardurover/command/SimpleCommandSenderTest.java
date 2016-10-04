package com.hevilavio.ardurover.command;

import com.hevilavio.ardurover.bluetooth.BTConnectionInterface;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by hevilavio on 10/4/16.
 */
public class SimpleCommandSenderTest {

    private final String commandString = "<<COMMAND STRING>>";


    @Test(expected = IllegalArgumentException.class)
    public void exceptionWhenReceivesANullCommand() throws Exception {
        ArduinoCommandSender sender = new ArduinoCommandSender(getBTConnectionInterface());

        sender.sendCommand(null);
    }

    @Test
    public void canSendTheCommandToBTInterface() throws Exception {
        BTConnectionInterface connectionInterface = getBTConnectionInterface();
        ArduinoCommand command = getCommand();

        when(command.commandString()).thenReturn(commandString);

        ArduinoCommandSender sender = new ArduinoCommandSender(connectionInterface);
        sender.sendCommand(command);

        verify(connectionInterface, times(1)).write(commandString);
    }

    public BTConnectionInterface getBTConnectionInterface() {
        return mock(BTConnectionInterface.class);
    }

    public ArduinoCommand getCommand() {
        return mock(ArduinoCommand.class);
    }
}