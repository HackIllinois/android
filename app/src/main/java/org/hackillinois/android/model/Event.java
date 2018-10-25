package org.hackillinois.android.model;

public class Event {
    private String name;
    private String description;
    private long startTime;
    private long endTime;

    public Event(String name, String description, long startTime, long endTime) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }
}
