package com.elinium.pattern.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

/**
 * Created by amiri on 9/11/2017.
 */

public class AsyncLocalUpdater<T, KEY_TYPE> extends AsyncTask<T, KEY_TYPE, Integer> {
    ELocalRepository<T, KEY_TYPE> ELocalRepository;
    EWebRepository<T, KEY_TYPE> EWebRepository;

    List<KEY_TYPE> tobeDeletedFromLocal;
    List<KEY_TYPE> tobeGetFromServer;

    boolean deleteHappened = false;
    boolean updateHappened = false;
    Context context;

    UpdateListener<KEY_TYPE> updateListener = null;

    public interface UpdateListener<KEY_TYPE> {
        void onDeleted(KEY_TYPE... keys);

        void onUpdated(KEY_TYPE... keys);

        void onCompleted(int sumRecordsAffected);
    }

    public AsyncLocalUpdater(Context context, ELocalRepository<T, KEY_TYPE> ELocalRepository, EWebRepository<T, KEY_TYPE> EWebRepository, List<KEY_TYPE> tobeDeletedFromLocal, List<KEY_TYPE> tobeGetFromServer) {
        this.tobeDeletedFromLocal = tobeDeletedFromLocal;
        this.tobeGetFromServer = tobeGetFromServer;
        this.ELocalRepository = ELocalRepository;
        this.EWebRepository = EWebRepository;
        this.context = context;
    }

    public void addUpdateListener(UpdateListener<KEY_TYPE> updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    protected Integer doInBackground(T... ts) {
        int sum = 0;
        try {
            if (tobeDeletedFromLocal != null) {
                for (KEY_TYPE key : tobeDeletedFromLocal) {
                    int deleted = ELocalRepository.deleteByKey(key);
                    sum += deleted;
                    if (updateListener != null && deleted > 0) {
                        deleteHappened = true;
                        publishProgress(key);
                    }
                }
            }

            deleteHappened = false;

            if (tobeGetFromServer != null) {
                for (KEY_TYPE key : tobeGetFromServer) {
                    T newObj = (T) EWebRepository.readSynchronized(key);
                    ELocalRepository.update(newObj);
                    sum++;
                    if (updateListener != null) {
                        updateHappened = true;
                        publishProgress(key);
                    }
                }
            }
            updateHappened = false;
        } catch (Exception e) {
            Log.e("AsyncLocalUpdater", "Error:" + e.getMessage());
        }
        return sum;
    }

    @Override
    protected void onProgressUpdate(KEY_TYPE... values) {
        super.onProgressUpdate(values);
        if (deleteHappened) updateListener.onDeleted(values);
        if (updateHappened) updateListener.onUpdated(values);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (updateListener != null) updateListener.onCompleted(integer);
    }
}
