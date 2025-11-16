package me.vrxdev.itsMyConfigConsoleSupport;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import java.lang.reflect.Field;

public class LogEventMutator {

    public static void setMessage(LogEvent event, String newMessage) {
        try {
            Field messageField = event.getClass().getDeclaredField("message");
            messageField.setAccessible(true);
            messageField.set(event, new SimpleMessage(newMessage));
        } catch (Exception e) {
            try {
                if (event instanceof org.apache.logging.log4j.core.impl.MutableLogEvent mutable) {
                    mutable.setMessage(new SimpleMessage(newMessage));
                }
            } catch (Exception ignored) {}
        }
    }
}