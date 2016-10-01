package com.hevilavio.ardurover.message;

/**
 * Created by hevilavio on 07/08/2016.
 */
public interface MessageHandler {

    /**
     * The identifier of the class responsible for message dealing.
     * */
    String getType();

    /**
     * The content from buffer, excluding the type and size.
     * */
    void onReceive(String content);

}
