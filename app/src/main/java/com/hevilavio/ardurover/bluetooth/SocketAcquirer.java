package com.hevilavio.ardurover.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import com.hevilavio.ardurover.util.Constants;

import java.io.IOException;
import java.util.Set;

/**
 * Created by hevilavio on 07/08/2016.
 */
public class SocketAcquirer {

    public BTConnection getConnection(){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null){
            Log.w(Constants.LOG_TAG, "bluetoothAdapter not found, nothing to do...");
            return new BTConnection(BluetoothState.NOT_FOUND);
        }

        if(!bluetoothAdapter.isEnabled()){
            Log.w(Constants.LOG_TAG, "bluetoothAdapter disabled, nothing to do...");
            return new BTConnection(BluetoothState.DISABLED);
        }

        Log.d(Constants.LOG_TAG, "bluetoothAdapter enabled, trying to get devices");
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if(bondedDevices.size() == 0){
            Log.w(Constants.LOG_TAG, "No appropriated paired devices, returning");
            return new BTConnection(BluetoothState.NO_BONDED_DEVICES);
        }

        Object[] devices = bondedDevices.toArray();
        BluetoothDevice device = (BluetoothDevice) devices[0];

        Log.i(Constants.LOG_TAG, "connecting with device [" + device.getName() + "]");

        ParcelUuid[] uuids = device.getUuids();
        BluetoothSocket socket;
        try {
            socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
            socket.connect();
        } catch (IOException e) {
            throw new RuntimeException("Error on socket acquiring", e);
        }
        return new BTConnection(BluetoothState.ENABLED, socket, device);
    }

    public boolean isBluetoothActive() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }
}
