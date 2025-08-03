package com.susu.processclient;

public class ChainIndexHolder {
    private static final ThreadLocal<Integer> holder = new ThreadLocal<>();

    public static void setChainIndex(Integer index) {
        holder.set(index);
    }

    public static Integer getChainIndex() {
        return holder.get();
    }

    public static void clear() {
        holder.remove();
    }
}
