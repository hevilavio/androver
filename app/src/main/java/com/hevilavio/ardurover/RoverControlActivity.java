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
import android.widget.TextView;

import com.hevilavio.ardurover.bluetooth.BTConnectionInterface;
import com.hevilavio.ardurover.util.ArduinoCommands;
import com.hevilavio.ardurover.util.AxisUiUpdater;
import com.hevilavio.ardurover.util.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RoverControlActivity extends AppCompatActivity implements SensorEventListener {

    private static final int DECIMAL_PLATES = 4;

    private static final String DIRECTION_NEUTRAL = "0";
    private static final String DIRECTION_CLOCKWISE = "1";
    private static final String DIRECTION_COUNT_CLOCKWISE = "2";

    private final double AXIS_CHANGE_TOLLERANCE = 1.0;
    private final int ABS_LIMIT_BEFORE_MOVING_ROVER = 25;


    private SensorManager sensorManager;
    double ax,ay,az;
    double lAx,lAy,lAz;

    private final AxisUiUpdater axisUiUpdater;

    public RoverControlActivity() {
        this.axisUiUpdater = new AxisUiUpdater();
    }

    public RoverControlActivity(AxisUiUpdater axisUiUpdater) {
        this.axisUiUpdater = axisUiUpdater;
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

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        BTConnectionInterface.getInstance().start();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(Constants.LOG_TAG, "M=onWindowFocusChanged, hasFocus= " + hasFocus);

        if(!hasFocus){
//            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(!isAnAcelerometerEvent(event)){
            return;
        }

        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];

        updateAxisUiValue(R.id.txt_axis_x_value, ax);
        updateAxisUiValue(R.id.txt_axis_y_value, ay);
        updateAxisUiValue(R.id.txt_axis_z_value, az);

        if(hasChanged(ax, ay, az)){
            updateLastValues(ax, ay, az);

            int wheelSpeed = linearMappingTOArduinoRange(Math.abs(ay));
            String direction = getDirectionByYValue(wheelSpeed, ay);

            Log.d(Constants.LOG_TAG, "M=readyToSend,wheelSpeed=" + wheelSpeed + ",dir=" + direction);

            BTConnectionInterface.getInstance().write(ArduinoCommands.ROVER_CONTROL
                    + direction
                    + wheelSpeed);
        }
    }

    /**
     * android acelerometer - 0 ~ (10 * 10)
     * arduino analogic - 0 ~ 255
     *
     * Y = (X-A)/(B-A) * (D-C) + C
     * reference: http://stackoverflow.com/questions/345187/math-mapping-numbers
     *
     * */
    private int linearMappingTOArduinoRange(double value) {
        int x = (int) (value * 10); // 0~10 will be 0~100
        double a = 0;
        double b = 100;

        double c = 0;
        double d = 255;
        return (int) ((x-a)/(b-a) * (d-c) + c);
    }
 // 32 / 10 * 255
    /**
     * According to Y value, return the direction
     * to move the wheels of the rover.
     * */
    private String getDirectionByYValue(int mappedY, double rawY) {

        if(Math.abs(mappedY) <= ABS_LIMIT_BEFORE_MOVING_ROVER) return DIRECTION_NEUTRAL;
        if(rawY > 0) return DIRECTION_CLOCKWISE;
        if(rawY < 0) return DIRECTION_COUNT_CLOCKWISE;

        throw new IllegalStateException("invalid direction, mappedY=" + mappedY);
    }

    private void updateAxisUiValue(int viewId, double value) {
        axisUiUpdater.updateText(((TextView) findViewById(viewId)), scaleDoubleToString(value));
    }

    private String scaleDoubleToString(double value) {
        return new BigDecimal(value).setScale(
                DECIMAL_PLATES, RoundingMode.DOWN).toString();
    }

    private boolean isAnAcelerometerEvent(SensorEvent event) {
        return event.sensor.getType() == Sensor.TYPE_ACCELEROMETER;
    }

    private void updateLastValues(double ax, double ay, double az) {
        lAx = ax;
        lAy = ay;
        lAz = az;
    }

    private boolean hasChanged(double ax, double ay, double az) {
        return Math.abs(lAx - ax) > AXIS_CHANGE_TOLLERANCE
                || Math.abs(lAy - ay) > AXIS_CHANGE_TOLLERANCE
                || Math.abs(lAz - az) > AXIS_CHANGE_TOLLERANCE;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(Constants.LOG_TAG, "M=onAccuracyChanged");
    }
}
