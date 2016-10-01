package com.hevilavio.ardurover.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Created by hevilavio on 07/08/2016.
 */
public class BTConnection {

    private final BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;

    public BTConnection(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice) {

        if(!bluetoothSocket.isConnected()){
            throw new RuntimeException();
        }

        this.bluetoothSocket = bluetoothSocket;
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluetoothSocket getConnectedBluetoothSocket() {
        return bluetoothSocket;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }
}
