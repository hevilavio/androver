package com.hevilavio.ardurover.command;

/**
 * Created by hevilavio on 10/3/16.
 */
public class SimpleCommandSender {

    private static SimpleCommandSender instance;

    private SimpleCommandSender(){ }

    public synchronized static SimpleCommandSender getInstance(){

        if(instance == null){
            instance = new SimpleCommandSender();
        }

        return instance;
    }
}
