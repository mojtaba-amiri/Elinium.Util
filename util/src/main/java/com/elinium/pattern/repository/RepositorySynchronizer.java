package com.elinium.pattern.repository;

import android.content.Context;
import android.os.AsyncTask;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by amiri on 9/11/2017.
 */

public class RepositorySynchronizer<T, KEY_TYPE> implements Runnable {
    LocalRepository<T, KEY_TYPE> localRepository;
    WebRepository<T, KEY_TYPE> webRepository;
    List<KEY_TYPE> tobeDeletedFromLocal;
    List<KEY_TYPE> tobeGetFromServer;

    @Override
    public void run() {
    }

    public RepositorySynchronizer(LocalRepository<T, KEY_TYPE> localRepository, WebRepository<T, KEY_TYPE> webRepository) {
        this.localRepository = localRepository;
        this.webRepository = webRepository;
    }

    public void startSyncing(Context context, AsyncLocalUpdater.UpdateListener<KEY_TYPE> listener) throws Exception {
        if (localRepository == null || webRepository == null) {
            throw new Exception("The local repository or web repository are null.");
        }

        Map<KEY_TYPE, Long> localTimeStaps = localRepository.getTimeStamps();
        Map<KEY_TYPE, Long> webTimeStaps = webRepository.getTimeStamps();
        tobeDeletedFromLocal = new ArrayList<>();
        tobeGetFromServer = new ArrayList<>();

        for (KEY_TYPE key : localTimeStaps.keySet()) {
            if (webTimeStaps.containsKey(key)) {
                if (localTimeStaps.get(key) != webTimeStaps.get(key)) {
                    /// Needs to be updated from server side ///
                    tobeGetFromServer.add(key);
                }
            } else {
                /// Record does not exist in server list.
                tobeDeletedFromLocal.add(key);
            }
        }

        for (KEY_TYPE key : webTimeStaps.keySet()) {
            if (!localTimeStaps.containsKey(key)) {
                /// Needs to be updated from server side ///
                tobeGetFromServer.add(key);
            }
        }

        AsyncLocalUpdater asyncLocalUpdater = new AsyncLocalUpdater(context, localRepository, webRepository, tobeDeletedFromLocal, tobeGetFromServer);
        asyncLocalUpdater.addUpdateListener(listener);
        asyncLocalUpdater.execute();
    }

}
