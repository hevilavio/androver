package com.hevilavio.ardurover.router;

import com.hevilavio.ardurover.message.MessageHandler;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Created by hevilavio on 07/08/2016.
 */
public class SimpleMessageRouterTest {

    @Test
    public void testCanRegisterExecutor() throws Exception {

        SimpleMessageRouter simpleMessageRouter = new SimpleMessageRouter();
        assertEquals(0, simpleMessageRouter.executorMap.size());
        MessageHandler messageExecutor = new MessageHandler() {
            @Override
            public String getType() {
                return "xxx";
            }

            @Override
            public void onReceive(String content) {

            }
        };
        simpleMessageRouter.registerMessageHandler(messageExecutor);
        assertEquals(1, simpleMessageRouter.executorMap.size());
        assertEquals(messageExecutor, simpleMessageRouter.executorMap.get("xxx"));

    }
}