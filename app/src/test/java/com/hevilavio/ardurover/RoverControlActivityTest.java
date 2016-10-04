package com.hevilavio.ardurover;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.support.annotation.NonNull;

import com.hevilavio.ardurover.command.ArduinoCommandSender;
import com.hevilavio.ardurover.command.ForwardOrBackwardCommand;
import com.hevilavio.ardurover.util.AxisUiUpdater;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by hevilavio on 01/10/2016.
 */
public class RoverControlActivityTest {

    float[] sensorValues;
    float[] barelyCrossingLimitValues;
    float[] crossedLimitValues;

    RoverControlActivity roverControlActivity;
    AxisUiUpdater axisUpdater;
    ArduinoCommandSender arduinoCommandSender;

    @Before
    public void setUp() throws Exception {
        sensorValues = new float[] {1, 2, 3};
        barelyCrossingLimitValues = new float[]{
                1 + RoverControlActivity.AXIS_CHANGE_TOLERANCE,
                2 + RoverControlActivity.AXIS_CHANGE_TOLERANCE,
                3 + RoverControlActivity.AXIS_CHANGE_TOLERANCE };

        crossedLimitValues = new float[]{
                1 + RoverControlActivity.AXIS_CHANGE_TOLERANCE + 0.4f,
                2 + RoverControlActivity.AXIS_CHANGE_TOLERANCE + 0.4f,
                3 + RoverControlActivity.AXIS_CHANGE_TOLERANCE + 0.4f };

        axisUpdater = mock(AxisUiUpdater.class);
        arduinoCommandSender = mock(ArduinoCommandSender.class);
        roverControlActivity = new RoverControlActivity(axisUpdater, arduinoCommandSender);
    }

    @Test
    public void doNothing_When_IsNotAnAccelerometerEvent() throws Exception {
        SensorEvent event = sensorEvent(Sensor.TYPE_GRAVITY);

        roverControlActivity.onSensorChanged(event);

        verify(axisUpdater, times(0)).updateText(any(RoverControlActivity.class), anyInt(), anyString());
        assertEquals(0, roverControlActivity.lAx, 0.00001);
        assertEquals(0, roverControlActivity.lAy, 0.00001);
        assertEquals(0, roverControlActivity.lAz, 0.00001);
        verify(arduinoCommandSender, times(0)).sendCommand(any(ForwardOrBackwardCommand.class));

    }

    @Test
    public void can_StoreSensorValues_When_IsAnAccelerometerEvent() throws Exception {
        SensorEvent event = sensorEvent(Sensor.TYPE_ACCELEROMETER);

        roverControlActivity.onSensorChanged(event);

        assertEquals(sensorValues[0], roverControlActivity.ax, 0.00001);
        assertEquals(sensorValues[1], roverControlActivity.ay, 0.00001);
        assertEquals(sensorValues[2], roverControlActivity.az, 0.00001);
    }

    @Test
    public void can_UpdateUI_When_IsAnAccelerometerEvent() throws Exception {
        SensorEvent event = sensorEvent(Sensor.TYPE_ACCELEROMETER);

        roverControlActivity.onSensorChanged(event);

        verify(axisUpdater, times(3)).updateText(any(RoverControlActivity.class), anyInt(), anyString());
    }

    @Test
    public void doNotUpdateLastValues_When_Doesnot_HasSignificantChange(){
        SensorEvent event1 = getSensorEvent(getMockedSensor(Sensor.TYPE_ACCELEROMETER),
                sensorValues);

        roverControlActivity.onSensorChanged(event1);

        // first exec, update it because default values were zero
        assertEquals(sensorValues[0], roverControlActivity.lAx, 0.00001);
        assertEquals(sensorValues[1], roverControlActivity.lAy, 0.00001);
        assertEquals(sensorValues[2], roverControlActivity.lAz, 0.00001);

        SensorEvent event2 = getSensorEvent(getMockedSensor(Sensor.TYPE_ACCELEROMETER),
                barelyCrossingLimitValues);

        roverControlActivity.onSensorChanged(event2);

        // still old values, because the change was only AXIS_CHANGE_TOLERANCE
        assertEquals(sensorValues[0], roverControlActivity.lAx, 0.00001);
        assertEquals(sensorValues[1], roverControlActivity.lAy, 0.00001);
        assertEquals(sensorValues[2], roverControlActivity.lAz, 0.00001);
    }

    @Test
    public void updateLastValues_When_HasSignificantChange(){
        SensorEvent event1 = getSensorEvent(getMockedSensor(Sensor.TYPE_ACCELEROMETER),
                sensorValues);

        roverControlActivity.onSensorChanged(event1);

        // first exec, update because default values were zero
        assertEquals(sensorValues[0], roverControlActivity.lAx, 0.00001);
        assertEquals(sensorValues[1], roverControlActivity.lAy, 0.00001);
        assertEquals(sensorValues[2], roverControlActivity.lAz, 0.00001);

        SensorEvent event2 = getSensorEvent(getMockedSensor(Sensor.TYPE_ACCELEROMETER),
                crossedLimitValues);

        roverControlActivity.onSensorChanged(event2);

        // still old values, because the change was only AXIS_CHANGE_TOLERANCE
        assertEquals(crossedLimitValues[0], roverControlActivity.lAx, 0.00001);
        assertEquals(crossedLimitValues[1], roverControlActivity.lAy, 0.00001);
        assertEquals(crossedLimitValues[2], roverControlActivity.lAz, 0.00001);
    }

    @Test
    public void doNotFireEventToArduino_When_Doesnot_HasSignificantChange(){
        SensorEvent event1 = getSensorEvent(getMockedSensor(Sensor.TYPE_ACCELEROMETER),
                sensorValues);
        SensorEvent event2 = getSensorEvent(getMockedSensor(Sensor.TYPE_ACCELEROMETER),
                barelyCrossingLimitValues);

        roverControlActivity.onSensorChanged(event1);
        verify(arduinoCommandSender, times(1)).sendCommand(any(ForwardOrBackwardCommand.class));

        roverControlActivity.onSensorChanged(event2);
        verifyNoMoreInteractions(arduinoCommandSender);
    }


    @Test
    public void fireEventToArduino_When_HasSignificantChange(){
        SensorEvent event1 = getSensorEvent(getMockedSensor(Sensor.TYPE_ACCELEROMETER),
                sensorValues);
        SensorEvent event2 = getSensorEvent(getMockedSensor(Sensor.TYPE_ACCELEROMETER),
                crossedLimitValues);

        roverControlActivity.onSensorChanged(event1);
        verify(arduinoCommandSender, times(1)).sendCommand(new ForwardOrBackwardCommand(sensorValues[1]));

        roverControlActivity.onSensorChanged(event2);
        verify(arduinoCommandSender, times(1)).sendCommand(new ForwardOrBackwardCommand(crossedLimitValues[1]));
    }

    @NonNull
    private SensorEvent sensorEvent(int type) {
        Sensor sensor = getMockedSensor(type);
        return getSensorEvent(sensor, sensorValues);
    }

    private SensorEvent getSensorEvent(Sensor sensor, float[] sensorValues) {
        return HardwareStuff.createSensorEvent(sensor, sensorValues);
    }

    @NonNull
    private Sensor getMockedSensor(int sensorType) {
        Sensor sensor = mock(Sensor.class);
        when(sensor.getType()).thenReturn(sensorType);
        return sensor;
    }
}
