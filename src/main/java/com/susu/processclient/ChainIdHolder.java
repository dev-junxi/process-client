package com.susu.processclient;

public class ChainIdHolder {
    private static final ThreadLocal<String> chainIdHolder = new ThreadLocal<>();

    public static void setChainId(String chainId) {
        chainIdHolder.set(chainId);
    }

    public static String getChainId() {
        return chainIdHolder.get();
    }

    public static void clear() {
        chainIdHolder.remove();
    }
}
