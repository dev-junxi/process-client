package com.susu.processclient;


public class ProcessClientHolder {
    private static ProcessClient CLIENT;

    protected static void set(ProcessClient client) {
        CLIENT = client;
    }

    public static ProcessClient get() {
        assert CLIENT != null;
        return CLIENT;
    }
}
