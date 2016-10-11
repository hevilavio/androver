package com.hevilavio.ardurover.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.hevilavio.ardurover.bluetooth.mock.MockedInputStream;
import com.hevilavio.ardurover.bluetooth.mock.MockedOutputStream;

import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by hevilavio on 10/11/16.
 */
public class BTConnectionInterfaceTest {

    @Test
    public void canStartWhenBTIsDisabled() throws Exception {

        SocketAcquirer socketAcquirer = getSocketAcquirer(BluetoothState.DISABLED);

        BTConnectionInterface btConnectionInterface = new BTConnectionInterface(socketAcquirer);

        btConnectionInterface.start();

        assertTrue(btConnectionInterface.inStream instanceof MockedInputStream);
        assertTrue(btConnectionInterface.outputStream instanceof MockedOutputStream);
        assertTrue(btConnectionInterface.readerThread != null);
        assertEquals(Thread.State.RUNNABLE, btConnectionInterface.readerThread.getState());
        assertTrue(btConnectionInterface.started);
    }

    @Test
    public void canStartWhenBTIsEnabled() throws Exception {
        InputStream myInStream = mock(InputStream.class);
        OutputStream myOutStream = mock(OutputStream.class);

        BluetoothSocket bluetoothSocket = mock(BluetoothSocket.class);
        when(bluetoothSocket.getInputStream()).thenReturn(myInStream);
        when(bluetoothSocket.getOutputStream()).thenReturn(myOutStream);
        when(bluetoothSocket.isConnected()).thenReturn(true);

        SocketAcquirer socketAcquirer = getSocketAcquirer(BluetoothState.ENABLED,
                bluetoothSocket, null);

        BTConnectionInterface btConnectionInterface = new BTConnectionInterface(socketAcquirer);

        btConnectionInterface.start();

        assertEquals(myInStream, btConnectionInterface.inStream);
        assertEquals(myOutStream, btConnectionInterface.outputStream);

        assertTrue(btConnectionInterface.readerThread != null);
        assertEquals(Thread.State.RUNNABLE, btConnectionInterface.readerThread.getState());
        assertTrue(btConnectionInterface.started);

    }

    private SocketAcquirer getSocketAcquirer(BluetoothState state) {
        return getSocketAcquirer(state, null, null);
    }

    private SocketAcquirer getSocketAcquirer(BluetoothState state, BluetoothSocket bluetoothSocket,
                                             BluetoothDevice bluetoothDevice) {
        SocketAcquirer socketAcquirer = mock(SocketAcquirer.class);
        BTConnection connection = new BTConnection(state, bluetoothSocket, bluetoothDevice);
        when(socketAcquirer.getConnection()).thenReturn(connection);

        return socketAcquirer;
    }
}