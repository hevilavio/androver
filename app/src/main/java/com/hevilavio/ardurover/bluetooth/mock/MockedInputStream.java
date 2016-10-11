package com.hevilavio.ardurover.bluetooth.mock;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hevilavio on 10/11/16.
 */

public class MockedInputStream extends InputStream {

    @Override
    public int read() throws IOException {
        // blocks for 1 hour
        try {
            Thread.sleep(1000 * 60 * 60);
        } catch (InterruptedException e) {
            return 0;
        }
        return 0; // Z
    }
}
