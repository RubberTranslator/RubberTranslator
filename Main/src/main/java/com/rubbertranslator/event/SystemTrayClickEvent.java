package com.rubbertranslator.event;

public class SystemTrayClickEvent {
    public static final int EXIT = 0;
    public static final int SHOW_MAIN_WINDOW = 1;
    public int eventType;

    public SystemTrayClickEvent(int eventType) {
        this.eventType = eventType;
    }
}
