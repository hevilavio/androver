package com.hevilavio.ardurover.util;

import org.junit.Test;

/**
 * Created by hevilavio on 07/08/2016.
 */
public class TimeUtilsTest {

    @Test
    public void testLog() throws Exception {
        System.out.println("from system =" + System.nanoTime());
        System.out.println("from utils  =" + TimeUtils.getNanoTime());
    }
}