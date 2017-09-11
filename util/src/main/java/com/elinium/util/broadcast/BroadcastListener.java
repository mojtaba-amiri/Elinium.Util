package com.elinium.util.broadcast;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.ArrayMap;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by amiri on 9/8/2017.
 */

public class BroadcastListener implements LifecycleObserver {
    private final String TAG = getClass().getSimpleName();
    private Context context;
    private IntentFilter localIntentFilters;
    private IntentFilter publicIntentFilters;
    private Map<String, String> localAnnotatedActions = new ArrayMap<>();
    private Map<String, String> publicAnnotatedActions = new ArrayMap<>();

    public static <T extends Context & LifecycleOwner> void initialize(T context) {
        context.getLifecycle().addObserver(new BroadcastListener(context));
    }

    public BroadcastListener(Context context) {
        this.context = context;
    }

    //region broadcast receivers (local and public) that invoke methods
    private BroadcastReceiver localReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "RECEIVER!");
            try {
                if (localAnnotatedActions != null && localAnnotatedActions.containsKey(intent.getAction())) {
                    String methodName = localAnnotatedActions.get(intent.getAction());
                    if (methodName != null)
                        context.getClass().getMethod(methodName).invoke(context, intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver publicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "PUBLIC RECEIVER!");
            if (publicAnnotatedActions != null && publicAnnotatedActions.containsKey(intent.getAction())) {
                try {
                    String methodName = publicAnnotatedActions.get(intent.getAction());
                    if (methodName != null)
                        context.getClass().getMethod(methodName).invoke(context, intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //endregion

    //region creating intent filters for local and public broadcasts

    private void getLocalAnnotatedActions() {
        for (Method method : context.getClass().getMethods()) {
            OnBroadcastReceived annotation = method.getAnnotation(OnBroadcastReceived.class);
            if (annotation != null && annotation.scope() == OnBroadcastReceived.Scope.LOCAL)
                localAnnotatedActions.put(annotation.actionName(), method.getName());
        }
    }

    private void getPublicAnnotatedActions() {
        for (Method method : context.getClass().getMethods()) {
            OnBroadcastReceived annotation = method.getAnnotation(OnBroadcastReceived.class);
            if (annotation != null && annotation.scope() == OnBroadcastReceived.Scope.PUBLIC)
                publicAnnotatedActions.put(annotation.actionName(), method.getName());
        }
    }

    @Nullable
    private IntentFilter getLocalIntentFilter() {
        try {
            getLocalAnnotatedActions();
            if (localAnnotatedActions.size() > 0) {
                IntentFilter intentFilter = new IntentFilter();
                for (String action : localAnnotatedActions.keySet()) intentFilter.addAction(action);
                return intentFilter;
            }
        } catch (Exception e) {
            Log.e(TAG, "EActivity.getLocalIntentFilter:" + e.getMessage());
        }

        return null;
    }

    @Nullable
    private IntentFilter getPublicIntentFilter() {
        try {
            getPublicAnnotatedActions();
            if (publicAnnotatedActions.size() > 0) {
                // Log.d(TAG, "method:" + actions.get(0));
                IntentFilter intentFilter = new IntentFilter();
                for (String action : publicAnnotatedActions.keySet())
                    intentFilter.addAction(action);
                return intentFilter;
            }
        } catch (Exception e) {
            Log.e(TAG, "EActivity.getPublicIntentFilter:" + e.getMessage());
        }
        return null;
    }
    //endregion

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void registerReceivers() {
        localIntentFilters = getLocalIntentFilter();
        publicIntentFilters = getPublicIntentFilter();
        try {
            if (localIntentFilters != null)
                LocalBroadcastManager.getInstance(context).registerReceiver(localReceiver, localIntentFilters);
            if (publicIntentFilters != null)
                context.registerReceiver(publicReceiver, publicIntentFilters);
        } catch (Exception e) {
            Log.e(TAG, "registerReceiver error:" + e.getMessage());
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void unregisterReceivers() {
        try {
            if (localIntentFilters != null)
                LocalBroadcastManager.getInstance(context).unregisterReceiver(localReceiver);
            if (publicIntentFilters != null)
                context.unregisterReceiver(publicReceiver);
        } catch (Exception e) {
            Log.e(TAG, "EActivity.onDestroy:" + e.getMessage());
        }
    }
}
