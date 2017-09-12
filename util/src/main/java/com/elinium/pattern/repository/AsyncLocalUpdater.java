package com.elinium.pattern.repository;

import android.arch.persistence.room.Update;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by amiri on 9/11/2017.
 */

public class AsyncLocalUpdater<T, KEY_TYPE> extends AsyncTask<T, KEY_TYPE, Integer> {
    LocalRepository<T, KEY_TYPE> localRepository;
    WebRepository<T, KEY_TYPE> webRepository;

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

    public AsyncLocalUpdater(Context context, LocalRepository<T, KEY_TYPE> localRepository, WebRepository<T, KEY_TYPE> webRepository, List<KEY_TYPE> tobeDeletedFromLocal, List<KEY_TYPE> tobeGetFromServer) {
        this.tobeDeletedFromLocal = tobeDeletedFromLocal;
        this.tobeGetFromServer = tobeGetFromServer;
        this.localRepository = localRepository;
        this.webRepository = webRepository;
        this.context = context;
    }

    public void addUpdateListener(UpdateListener<KEY_TYPE> updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    protected Integer doInBackground(T... ts) {
        int sum = 0;
        if (tobeDeletedFromLocal != null) {
            for (KEY_TYPE key : tobeDeletedFromLocal) {
                int deleted = localRepository.delete(key);
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
                T newObj = (T) webRepository.readSynchronized(key);
                localRepository.update(newObj);
                sum++;
                if (updateListener != null) {
                    updateHappened = true;
                    publishProgress(key);
                }
            }
        }
        updateHappened = false;

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
