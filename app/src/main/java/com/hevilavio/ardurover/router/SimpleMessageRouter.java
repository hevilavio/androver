package com.hevilavio.ardurover.router;

import android.util.Log;

import com.hevilavio.ardurover.message.MessageHandler;
import com.hevilavio.ardurover.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hevilavio on 07/08/2016.
 *
 * Route a received message to the correspondent class.
 *
 * 01 - PingResult
 *
 */
public class SimpleMessageRouter {

    // linearMapping of <messageType, messageExecutor>
    Map<String, MessageHandler> executorMap;
    MessageExtractor messageExtractor;

    private static SimpleMessageRouter instance;

    SimpleMessageRouter() {
        this.executorMap = new HashMap<>();
        this.messageExtractor = new MessageExtractor();
    }

    public static SimpleMessageRouter getInstance(){
        if(instance == null){
            instance = new SimpleMessageRouter();
        }
        return instance;
    }

    public void registerMessageHandler(MessageHandler messageExecutor){
        if(messageExecutor == null || messageExecutor.getType() == null){
            throw new IllegalArgumentException();
        }

//        Log.d(Constants.LOG_TAG, "registering handler of type[" +  messageExecutor.getType() + "]");
        this.executorMap.put(messageExecutor.getType(), messageExecutor);
    }

    public void route(String message){
        Log.d(Constants.LOG_TAG, "received message[" +  message + "], number of routers["
                + executorMap.size() + "]");

        String type;
        try{
            type = messageExtractor.extractMessageType(message);
        }catch (IllegalArgumentException e){
            Log.e(Constants.LOG_TAG, e.getMessage());
            return;
        }

        MessageHandler executor = executorMap.get(type);
        if(executor == null){
            Log.w(Constants.LOG_TAG, "invalid message[" +  message + "]");
            return;
        }

        String content = messageExtractor.extractMessageContent(message);

        Log.d(Constants.LOG_TAG, "routing type[" + type + "] with content [" + content + "]");
        executor.onReceive(content);
    }
}
