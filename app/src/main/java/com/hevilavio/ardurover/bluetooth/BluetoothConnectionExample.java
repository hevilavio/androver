package com.hevilavio.ardurover.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import com.hevilavio.ardurover.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by hevilavio on 04/08/2016.
 */
public class BluetoothConnectionExample {

    private OutputStream outputStream = null;
    private InputStream inStream = null;

    public void init(){
        try {
            initInternal();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initInternal() throws IOException {

        Log.i(Constants.LOG_TAG, "starting connection");

        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {

                Log.i(Constants.LOG_TAG, "blueAdapter enabled = true");

                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                if(bondedDevices.size() > 0) {
                    Object[] devices = bondedDevices.toArray();
                    BluetoothDevice device = (BluetoothDevice) devices[0];

                    Log.i(Constants.LOG_TAG, "connecting with [" + device.getName() + "]");

                    ParcelUuid[] uuids = device.getUuids();
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    socket.connect();

                    Log.i(Constants.LOG_TAG, "socket connected");
                    outputStream = socket.getOutputStream();
                    inStream = socket.getInputStream();
                }
                else {
                    String msg = "Cant execute test. No appropriate paired devices.";
                    Log.e(Constants.LOG_TAG, msg);
                    throw new RuntimeException(msg);
                }

            } else {
                String msg = "Cant handle test. Bluetooth is disabled.";
                Log.e(Constants.LOG_TAG, msg);
                throw new RuntimeException(msg);
            }
        }
    }

    public void startSocketThreads(){

        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    read();
                }
            }
        });

        Thread writer = new Thread(new Runnable() {
            long count = 0;
            @Override
            public void run() {

                while (true){
                    try {
                        write("ANDR MSG COUNT " + count++);
                        TimeUnit.SECONDS.sleep(3);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });


        reader.start();
        Log.i(Constants.LOG_TAG, "reader thread started...");

        writer.start();
        Log.i(Constants.LOG_TAG, "writer thread started...");
    }

    final String terminator = "\n";
    final int terminatorByte = terminator.getBytes()[0];

    private void write(String s) throws IOException {

        Log.d(Constants.LOG_TAG, "out >> " + s);
        outputStream.write(s.getBytes());
        outputStream.write(terminatorByte);
    }

    private void read() {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int _byte, index = 0;


        while (true) {
            try {
                Log.d(Constants.LOG_TAG, "=== waiting for socket ===");

                while ((_byte = inStream.read()) != terminatorByte) {
                    buffer[index++] = (byte) _byte;
                }

                String message = new String(Arrays.copyOf(buffer, index));
                Log.d(Constants.LOG_TAG, "in  << " + message);

                buffer = new byte[BUFFER_SIZE];
                index = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
