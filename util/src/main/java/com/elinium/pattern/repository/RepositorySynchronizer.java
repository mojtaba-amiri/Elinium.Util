package com.elinium.pattern.repository;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by amiri on 9/11/2017.
 */

public class RepositorySynchronizer<T, KEY_TYPE> {
    ELocalRepository<T, KEY_TYPE> ELocalRepository;
    EWebRepository<T, KEY_TYPE> EWebRepository;
    List<KEY_TYPE> tobeDeletedFromLocal;
    List<KEY_TYPE> tobeGetFromServer;

    public RepositorySynchronizer(ELocalRepository<T, KEY_TYPE> ELocalRepository, EWebRepository<T, KEY_TYPE> EWebRepository) {
        this.ELocalRepository = ELocalRepository;
        this.EWebRepository = EWebRepository;
    }

    public void startSyncing(Context context, AsyncLocalUpdater.UpdateListener<KEY_TYPE> listener) throws Exception {
        if (ELocalRepository == null || EWebRepository == null) {
            throw new Exception("The local repository or web repository are null.");
        }

        Map<KEY_TYPE, Long> localTimeStaps = ELocalRepository.getLocalTimeStamps();
        Map<KEY_TYPE, Long> webTimeStaps = EWebRepository.getWebTimeStamps();
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

        AsyncLocalUpdater asyncLocalUpdater = new AsyncLocalUpdater(context, ELocalRepository, EWebRepository, tobeDeletedFromLocal, tobeGetFromServer);
        asyncLocalUpdater.addUpdateListener(listener);
        asyncLocalUpdater.execute();
    }

}
