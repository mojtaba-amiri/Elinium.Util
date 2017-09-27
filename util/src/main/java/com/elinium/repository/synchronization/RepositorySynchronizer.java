package com.elinium.repository.synchronization;

import android.content.Context;

import com.elinium.repository.base.ILocalRepository;
import com.elinium.repository.base.IWebRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by amiri on 9/11/2017.
 */

public class RepositorySynchronizer<T, KEY_TYPE> {
    ILocalRepository<T, KEY_TYPE> ILocalRepository;
    IWebRepository<T, KEY_TYPE> EWebRepository;
    List<KEY_TYPE> tobeDeletedFromLocal;
    List<KEY_TYPE> tobeGetFromServer;

    public RepositorySynchronizer(ILocalRepository<T, KEY_TYPE> ILocalRepository, IWebRepository<T, KEY_TYPE> EWebRepository) {
        this.ILocalRepository = ILocalRepository;
        this.EWebRepository = EWebRepository;
    }

    public void startSyncing(Context context, AsyncLocalUpdater.UpdateListener<KEY_TYPE> listener) throws Exception {
        if (ILocalRepository == null || EWebRepository == null) {
            throw new Exception("The local repository or web repository are null.");
        }

        Map<KEY_TYPE, Long> localTimeStaps = ILocalRepository.getLocalTimeStamps();
        Map<KEY_TYPE, Long> webTimeStaps = EWebRepository.getWebTimeStampsSynchronized();
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

        AsyncLocalUpdater asyncLocalUpdater = new AsyncLocalUpdater(context, ILocalRepository, EWebRepository, tobeDeletedFromLocal, tobeGetFromServer);
        asyncLocalUpdater.addUpdateListener(listener);
        asyncLocalUpdater.execute();
    }

}
