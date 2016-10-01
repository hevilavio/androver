package com.hevilavio.ardurover.message;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * Created by hevilavio on 07/08/2016.
 */
public class PingMessageHandlerTest {

    //AND179597453598427ARD179597453598427
    //AND31708582490910ARD179597453598427
    //AND1795974534444598427ARD179597453598427

    @Test
    public void testGetAndroidSentTime() throws Exception {
        BigDecimal sentTime = new PingMessageHandler()
                .getAndroidSentTime("AND179597453598427ARD179597453598427");
        assertEquals(new BigDecimal(179597453598427L), sentTime);
    }

    @Test
    public void testGetAndroidSentTime_2() throws Exception {
        BigDecimal sentTime = new PingMessageHandler()
                .getAndroidSentTime("AND31708582490910ARD179597453598427");
        assertEquals(new BigDecimal(31708582490910L), sentTime);
    }

    @Test
    public void testGetAndroidSentTime_3() throws Exception {
        BigDecimal sentTime = new PingMessageHandler()
                .getAndroidSentTime("AND1795974534444598427ARD179597453598427");
        assertEquals(new BigDecimal(1795974534444598427L), sentTime);
    }
}