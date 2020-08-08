package com.nhq.authenticator.util;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Single Executor handle all database task (singleton implement)
 */
public final class SingleTaskExecutor {

    private static final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SingleTaskExecutor() { }

    public static ExecutorService getInstance() {
        return mExecutor;
    }

    public static void queueRunnable(Runnable runnable) {
        mExecutor.execute(runnable);
    }
}
