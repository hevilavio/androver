package com.hevilavio.ardurover;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.hevilavio.ardurover.bluetooth.BTConnectionInterface;
import com.hevilavio.ardurover.command.ArduinoCommandSender;
import com.hevilavio.ardurover.command.MotionCommand;
import com.hevilavio.ardurover.util.Constants;
import com.hevilavio.ardurover.util.MotionUtils;
import com.hevilavio.ardurover.util.UIUpdater;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoverControlActivity extends AppCompatActivity implements SensorEventListener {

    // TODO - Move to another class
    static final int DECIMAL_PLATES = 4;
    static final float AXIS_CHANGE_TOLERANCE = 0.5f;

    double ax,ay,az;
    double lAx,lAy,lAz;

    private final UIUpdater uIUpdater;
    private final ArduinoCommandSender arduinoCommandSender;

    SensorManager sensorManager;

    public RoverControlActivity() {
        this.uIUpdater = new UIUpdater();
        this.arduinoCommandSender = ArduinoCommandSender.getInstance();
    }

    public RoverControlActivity(UIUpdater axisUiUpdater, ArduinoCommandSender arduinoCommandSender) {
        this.uIUpdater = axisUiUpdater;
        this.arduinoCommandSender = arduinoCommandSender;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometer_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if(sensorManager == null) sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        registerAccelerometerListener();
        BTConnectionInterface.getInstance().start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(Constants.LOG_TAG, "M=onWindowFocusChanged,hasFocus=" + hasFocus);

        if(!hasFocus) unregisterAccelerometerListener();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(Constants.LOG_TAG, "M=onAccuracyChanged");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(!isAnAccelerometerEvent(event)) return;

        storeSensorValues(event);

        updateAxisUI();

        if(!hasSignificantChange(ax, ay, az)) return;

        updateLastSensorValues(ax, ay, az);
        fireEventToArduino();

        updateThrottleUI();
    }

    private void updateThrottleUI() {
        MotionUtils motionUtils = new MotionUtils();

        String speed = motionUtils.getSpeed(ay);
        boolean isOnLimit = motionUtils.isGreaterOrEqualsLimit(Integer.parseInt(speed));

        Log.d(Constants.LOG_TAG, "M=updateThrottleUI,speed=" + speed + ",isOnLimit=" + isOnLimit);

        // speed
//        uIUpdater.updateText(this, R.id.txt_axis_x_value, speed);
        // limit light
//        uIUpdater.updateMaxThrottle(this, R.id.txt_axis_x_value, isOnLimit);
    }


    private void registerAccelerometerListener() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterAccelerometerListener() {
        sensorManager.unregisterListener(this);
    }


    private void fireEventToArduino() {
        arduinoCommandSender.sendCommand(new MotionCommand(ax, ay));
    }

    private void updateAxisUI() {
        uIUpdater.updateText(this, R.id.txt_axis_x_value, scaleDoubleToString(ax));
        uIUpdater.updateText(this, R.id.txt_axis_y_value, scaleDoubleToString(ay));
        uIUpdater.updateText(this, R.id.txt_axis_z_value, scaleDoubleToString(az));
    }

    private void storeSensorValues(SensorEvent event) {
        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];
    }

    private String scaleDoubleToString(double value) {
        return new BigDecimal(value).setScale(DECIMAL_PLATES, RoundingMode.DOWN).toString();
    }

    private boolean isAnAccelerometerEvent(SensorEvent event) {
        return event.sensor.getType() == Sensor.TYPE_ACCELEROMETER;
    }

    private void updateLastSensorValues(double ax, double ay, double az) {
        lAx = ax;
        lAy = ay;
        lAz = az;
    }

    private boolean hasSignificantChange(double ax, double ay, double az) {
        return Math.abs(lAx - ax) > AXIS_CHANGE_TOLERANCE
                || Math.abs(lAy - ay) > AXIS_CHANGE_TOLERANCE
                || Math.abs(lAz - az) > AXIS_CHANGE_TOLERANCE;
    }
}
