package com.elinium.pattern.repository;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by amiri on 9/12/2017.
 */

@RepositorySet(repositories = {})
public class RepositoryManager {
    private static final String TAG = "RepositoryManager";
    private static final int INTERVAL = 3600; // in seconds
    private static final int JOB_WINDOW = 300; // in seconds
    private static final String JOB_TAG = "repo-sychronizer";

    private static RepositoryManager instance = null;
    private static List<Class<? extends ERepository>> repositories = new ArrayList<>();
    private FirebaseJobDispatcher dispatcher;
    private static Context context;


    static List<Class<? extends ERepository>> getRepositories() {
        return repositories;
    }

    public static Context getContext() {
        return context;
    }

    public static <T extends Context & RepositorySet> void initialize(T context) {
        RepositorySet annotation = context.getClass().getAnnotation(RepositorySet.class);
        if (instance == null) instance = new RepositoryManager();
        try {
            if (annotation != null) {
                instance.repositories.clear();
                instance.repositories.addAll(Arrays.asList(annotation.repositories()));
                instance.cancelJob();
                RepositoryManager.context = context;
                instance.init(context);
            } else {
                Log.e(TAG, "RepositoryManager.initialize failed. You must add @RepositorySet to the context. ");
            }
        } catch (Exception e) {
            Log.e(TAG, "RepositoryManager.initialize failed:" + e.getMessage());
        }
    }

    private void init(Context context) {
        try {
            dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
            Job myJob = dispatcher.newJobBuilder()
                    .setService(SynchronizerJobService.class) // the JobService that will be called
                    .setTag(JOB_TAG)        // uniquely identifies the job
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(INTERVAL, INTERVAL + JOB_WINDOW))
                    .build();
            dispatcher.mustSchedule(myJob);
        } catch (Exception e) {
            Log.e(TAG, "RepositoryManager init failed:" + e.getMessage());
        }
    }

    private void cancelJob() {
        if (dispatcher != null) dispatcher.cancel(JOB_TAG);
    }
}
