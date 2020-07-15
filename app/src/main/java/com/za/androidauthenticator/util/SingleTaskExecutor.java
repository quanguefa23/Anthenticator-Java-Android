package com.za.androidauthenticator.util;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Single Executor handle all database task (singleton implement)
 */
public final class SingleTaskExecutor {
    private static ScheduledExecutorService mExecutor;

    private SingleTaskExecutor() {
    }

    static {
        mExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    public static void queueRunnable(Runnable runnable) {
        mExecutor.execute(runnable);
    }
}
