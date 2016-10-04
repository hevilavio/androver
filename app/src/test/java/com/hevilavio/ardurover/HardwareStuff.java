package com.hevilavio.ardurover;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import org.mockito.Mockito;

import java.lang.reflect.Field;

public class HardwareStuff {
    public static SensorEvent createSensorEvent(Sensor sensor, float[] sensorValue) {
        SensorEvent sensorEvent = Mockito.mock(SensorEvent.class);

        try {
            Field valuesField = SensorEvent.class.getField("values");
            Field sensorField = SensorEvent.class.getField("sensor");

            valuesField.setAccessible(true);
            sensorField.setAccessible(true);

            try {
                valuesField.set(sensorEvent, sensorValue);
                sensorField.set(sensorEvent, sensor);


            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return sensorEvent;
    }
}