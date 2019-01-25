package com.elinium.mvc;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by amiri on 10/28/2017.
 */

public class BaseOperation<T> extends AsyncTask<Context, T, T> {
    private OperationCallback<T> callback;
    private AsyncContextOperation<T> operation;

    private Throwable throwable;

    public BaseOperation() {

    }

    public void setCallback(OperationCallback<T> callback) {
        this.callback = callback;
    }

    public void setOperation(AsyncContextOperation<T> operation) {
        this.operation = operation;
    }

    public BaseOperation(AsyncContextOperation<T> operation, OperationCallback callback) {
        this.callback = callback;
        this.operation = operation;
    }

    public interface AsyncContextOperation<T> {
        T Do(Context context);
    }

    public interface AsyncOperation<T> {
        T Do();
    }

    public interface OperationCallback<T> {
        void onDone(T latestVm, Throwable e);
    }

    @Override
    protected T doInBackground(Context... contexts) {
        try {
            synchronized (this) {
                if (operation != null)
                    return operation.Do(contexts.length == 0 ? null : contexts[0]);
            }
        } catch (Exception e) {
            throwable = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(T result) {
        super.onPostExecute(result);
        if (callback != null) {
            callback.onDone(result, throwable);
        }
    }
}