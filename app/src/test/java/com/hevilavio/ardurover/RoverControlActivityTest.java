package com.hevilavio.ardurover;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.widget.TextView;

import com.hevilavio.ardurover.util.AxisUiUpdater;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by hevilavio on 01/10/2016.
 */
@Ignore("I'm working on this")
public class RoverControlActivityTest {

    RoverControlActivity roverControlActivity;



    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testCanUpdateUiWhenValueChanges() throws Exception {

        AxisUiUpdater axisUpdater = mock(AxisUiUpdater.class);
        roverControlActivity = new RoverControlActivity(axisUpdater);

        SensorEvent event = mockedSensorEvent();
        roverControlActivity.onSensorChanged(event);

        verify(axisUpdater, times(1)).updateText(any(TextView.class), anyString());


        // ???
    }

    private SensorEvent mockedSensorEvent() {
        SensorEvent event = mock(SensorEvent.class);

        float[] values = {1, 2, 3};
        Sensor sensor = mock(Sensor.class);
        when(sensor.getType()).thenReturn(Sensor.TYPE_ACCELEROMETER);

        /**
         * org.mockito.exceptions.misusing.MissingMethodInvocationException:
         when() requires an argument which has to be 'a method call on a mock'.
         * */
        when(event.values).thenReturn(values);
        when(event.sensor).thenReturn(sensor);

        return event;
    }
}