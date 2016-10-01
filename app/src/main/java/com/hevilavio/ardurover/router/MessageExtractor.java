package com.hevilavio.ardurover.router;

/**
 * Created by hevilavio on 07/08/2016.
 */
public class MessageExtractor {

    /**
     * How many chars will be used to determine the
     * type of message.
     * <p/>
     * Today, the first 2 chars.
     */
    final int messageTypeIndex = 2;

    public String extractMessageType(String message) {
        validate(message);
        return message.substring(0, messageTypeIndex);
    }

    /**
     * Return the message without the messageType.
     */

    public String extractMessageContent(String message) {
        // todo unit tests for validate
        validate(message);
        return message.substring(messageTypeIndex, message.length());

    }

    private void validate(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("message can not be null");
        }
    }

}
