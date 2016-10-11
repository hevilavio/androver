package com.hevilavio.ardurover.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.hevilavio.ardurover.bluetooth.mock.MockedInputStream;
import com.hevilavio.ardurover.bluetooth.mock.MockedOutputStream;
import com.hevilavio.ardurover.router.SimpleMessageRouter;
import com.hevilavio.ardurover.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by hevilavio on 07/08/2016.
 */
public class BTConnectionInterface {

    // final
    private final String terminator = "\n";
    private final int terminatorByte = terminator.getBytes()[0];
    private final Object lock = new Object();
    private final SocketAcquirer socketAcquirer;

    // holds state
    private boolean started;
    private InputStream inStream;
    private OutputStream outputStream;
    private BTConnection bTConnection;
    private Thread readerThread;

    // instance
    private static BTConnectionInterface instance;

    BTConnectionInterface() {
        socketAcquirer = new SocketAcquirer();
    }

    public BTConnectionInterface(SocketAcquirer socketAcquirer) {
        this.socketAcquirer = socketAcquirer;
    }

    public synchronized static BTConnectionInterface getInstance() {
        if (instance == null) {
            instance = new BTConnectionInterface();
        }

        return instance;
    }

    public synchronized void start() {

        if (started) {
            Log.w(Constants.LOG_TAG, "Already started, ignoring");
            return;
        }

        Log.i(Constants.LOG_TAG, "Starting BTConnectionInterface");
        bTConnection = socketAcquirer.getConnection();

        if(!bTConnection.isReadyToUse()){
            Log.d(Constants.LOG_TAG, "socketCom=mock");
            startUsingMockedSocket();
        }
        else {
            Log.d(Constants.LOG_TAG, "socketCom=real");
            startUsingRealSocket();
        }

        startSocketReaderThread();
        started = true;
    }

    private void startUsingMockedSocket() {
        inStream = new MockedInputStream();
        outputStream = new MockedOutputStream();

        Log.i(Constants.LOG_TAG, "BTConnectionInterface started using mocked classes");
    }

    private void startUsingRealSocket() {
        BluetoothSocket bluetoothSocket = bTConnection.getConnectedBluetoothSocket();

        Log.d(Constants.LOG_TAG, "bluetoothSocket acquired, "
                + "isConnected=" + bluetoothSocket.isConnected());

        try {
            inStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();

            Log.i(Constants.LOG_TAG, "BTConnectionInterface started normally");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void startSocketReaderThread() {
        readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true && !Thread.currentThread().isInterrupted()) {
                    String message = read();

                    if (message == null) {
                        Log.i(Constants.LOG_TAG, "ignoring null message");
                    } else {
                        SimpleMessageRouter.getInstance().route(message);
                    }
                }
            }
        });

        readerThread.start();
        Log.i(Constants.LOG_TAG, "reader thread started...");
    }

    public void stop() {

        if(!started) return;

        synchronized (lock) {
            Log.i(Constants.LOG_TAG, "Stopping BTConnectionInterface");

            try {
                readerThread.interrupt();
                Log.d(Constants.LOG_TAG, "readerThread interupted");

                inStream.close();
                Log.d(Constants.LOG_TAG, "inStream closed");

                outputStream.close();
                Log.d(Constants.LOG_TAG, "outputStream closed");

                // null check
                bTConnection.getConnectedBluetoothSocket().close();
                Log.d(Constants.LOG_TAG, "bluetoothSocket closed");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                started = false;
            }

            Log.i(Constants.LOG_TAG, "BTConnectionInterface stopped");
        }
    }

    public String getDeviceName() {
        return bTConnection.getBluetoothDevice().getName();
    }

    public boolean isBluetoothActive(){
        return new SocketAcquirer().isBluetoothActive();
    }

    public int write(String content) {

        synchronized (lock) {
            if(!started){
                Log.w(Constants.LOG_TAG, "BTConnectionInterface is now stopped");
                return -1;
            }

            Log.d(Constants.LOG_TAG, "out >> " + content);
            try {
                outputStream.write(content.getBytes());
                outputStream.write(terminatorByte);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                return 0;
            }
        }
    }

    private String read() {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int _byte, index = 0;

        try {
            Log.d(Constants.LOG_TAG, "=== waiting for socket ===");

            while ((_byte = inStream.read()) != terminatorByte) {
                buffer[index++] = (byte) _byte;
            }

            String message = new String(Arrays.copyOf(buffer, index));

            Log.d(Constants.LOG_TAG, "in  << " + message);
            return message;
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, e.getMessage());

            this.stop();
            this.start();

            return null;
        }
    }
}
