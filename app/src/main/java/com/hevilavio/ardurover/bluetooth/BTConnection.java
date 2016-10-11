package com.hevilavio.ardurover.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Created by hevilavio on 07/08/2016.
 */
public class BTConnection {

    private final BluetoothState bluetoothState;
    private final BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;

    public BTConnection(BluetoothState bluetoothState, BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice) {

        if(bluetoothState.equals(BluetoothState.ENABLED)){
            if(bluetoothSocket != null && !bluetoothSocket.isConnected()){
                throw new IllegalStateException();
            }
        }

        this.bluetoothState = bluetoothState;
        this.bluetoothSocket = bluetoothSocket;
        this.bluetoothDevice = bluetoothDevice;
    }

    public BTConnection(BluetoothState bluetoothState) {
        this(bluetoothState, null, null);
    }

    public BluetoothState getBluetoothState() {
        return bluetoothState;
    }

    public BluetoothSocket getConnectedBluetoothSocket() {
        return bluetoothSocket;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public boolean isReadyToUse(){
        return BluetoothState.ENABLED.equals(bluetoothState)
                && bluetoothSocket.isConnected();
    }
}
