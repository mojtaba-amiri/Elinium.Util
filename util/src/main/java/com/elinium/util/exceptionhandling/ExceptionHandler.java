package com.elinium.util.exceptionhandling;

import android.content.Context;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Created by amiri on 9/7/2017.
 */

public class ExceptionHandler implements UncaughtExceptionHandler {
    IExceptionHandler exceptionHandler = null;

    public interface IExceptionHandler {
        void onException(String threadName, Throwable throwable);
    }

    public static void register(IExceptionHandler iExceptionHandler) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(iExceptionHandler));
    }

    public ExceptionHandler(IExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            // Log error
            if (exceptionHandler != null) exceptionHandler.onException(t.getName(), e);
        } catch (Exception ex) {
            // Log Exception Handler Exception!
        }
    }
}
