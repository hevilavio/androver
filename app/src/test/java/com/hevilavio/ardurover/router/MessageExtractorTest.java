package com.hevilavio.ardurover.router;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hevilavio on 07/08/2016.
 */
public class MessageExtractorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionNullMessage() throws Exception {
        new MessageExtractor().extractMessageType(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testExceptionEmptyMessage() throws Exception {
        new MessageExtractor().extractMessageType("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionSpacedMessage() throws Exception {
        new MessageExtractor().extractMessageType("     ");
    }

    @Test
    public void testExctractMessageType_01() throws Exception {
        String messageType = new MessageExtractor().extractMessageType("01ASDFG");
        assertEquals("01", messageType);
    }

    @Test
    public void testExctractMessageType_AC() throws Exception {
        String messageType = new MessageExtractor().extractMessageType("AC1ASDFG");
        assertEquals("AC", messageType);
    }

    // validate

    @Test
    public void testExctractMessageContent() throws Exception {
        String content = new MessageExtractor().extractMessageContent("AC1ASDFG");
        assertEquals("1ASDFG", content);
    }
}