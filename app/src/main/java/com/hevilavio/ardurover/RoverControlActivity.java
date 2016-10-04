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
import com.hevilavio.ardurover.command.ArduinoCommand;
import com.hevilavio.ardurover.command.ArduinoCommandSender;
import com.hevilavio.ardurover.command.ForwardOrBackwardCommand;
import com.hevilavio.ardurover.util.AxisUiUpdater;
import com.hevilavio.ardurover.util.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoverControlActivity extends AppCompatActivity implements SensorEventListener {

    // TODO - Move to another class
    private static final int DECIMAL_PLATES = 4;
    static final float AXIS_CHANGE_TOLERANCE = 0.5f;

    // // TODO: 10/4/16 - can it be a local variable?
    private SensorManager sensorManager;

    double ax,ay,az;
    double lAx,lAy,lAz;

    private final AxisUiUpdater axisUiUpdater;
    private final ArduinoCommandSender arduinoCommandSender;


    protected RoverControlActivity() {
        this.axisUiUpdater = new AxisUiUpdater();
        this.arduinoCommandSender = ArduinoCommandSender.getInstance();
    }

    public RoverControlActivity(AxisUiUpdater axisUiUpdater, ArduinoCommandSender arduinoCommandSender) {
        this.axisUiUpdater = axisUiUpdater;
        this.arduinoCommandSender = arduinoCommandSender;
    }

    // todo - a test here will be good
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

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        BTConnectionInterface.getInstance().start();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(Constants.LOG_TAG, "M=onWindowFocusChanged");
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(Constants.LOG_TAG, "M=onAccuracyChanged");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(!isAnAccelerometerEvent(event)) return;

        storeSensorValues(event);
        updateUI();

        if(!hasSignificantChange(ax, ay, az)) return;

        updateLastSensorValues(ax, ay, az);
        fireEventToArduino();
    }

    private void fireEventToArduino() {
        ArduinoCommand command = new ForwardOrBackwardCommand(ay);
        arduinoCommandSender.sendCommand(command);
    }

    private void updateUI() {
        axisUiUpdater.updateText(this, R.id.txt_axis_x_value, scaleDoubleToString(ax));
        axisUiUpdater.updateText(this, R.id.txt_axis_y_value, scaleDoubleToString(ay));
        axisUiUpdater.updateText(this, R.id.txt_axis_z_value, scaleDoubleToString(az));
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
