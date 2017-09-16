package com.elinium.pattern.repository;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amiri on 9/16/2017.
 */

public class SynchronizerJobService extends JobService implements AsyncLocalUpdater.UpdateListener {
    private static final String TAG = "SynchronizerJobService";
    private List<RepositorySynchronizer> synchronizers = new ArrayList<>();

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob:");

        for (Class repository : RepositoryManager.getRepositories()) {
            try {
                new RepositorySynchronizer((ELocalRepository) repository.newInstance(), (EWebRepository) repository.newInstance()).startSyncing(RepositoryManager.getContext(), this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob:");
        return false;
    }

    @Override
    public void onDeleted(Object[] keys) {

    }

    @Override
    public void onUpdated(Object[] keys) {

    }

    @Override
    public void onCompleted(int sumRecordsAffected) {

    }
}