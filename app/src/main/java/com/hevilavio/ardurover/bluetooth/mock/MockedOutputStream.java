package com.hevilavio.ardurover.bluetooth.mock;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by hevilavio on 10/11/16.
 */

public class MockedOutputStream extends OutputStream {
    @Override
    public void write(int i) throws IOException {
        // do nothing
    }
}
