package com.nhq.authenticator.data.entity;

public class ResponseTime {
    private String timezone;
    private int unixtime;

    public String getTimeZone() {
        return timezone;
    }

    public int getUnixTime() {
        return unixtime;
    }
}