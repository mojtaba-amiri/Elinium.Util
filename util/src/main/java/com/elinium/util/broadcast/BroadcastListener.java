package com.elinium.util.broadcast;


import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.ArrayMap;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by amiri on 9/8/2017.
 */

public class BroadcastListener implements DefaultLifecycleObserver {
    private final String TAG = getClass().getSimpleName();
    private Context context;
    private IntentFilter localIntentFilters;
    private IntentFilter publicIntentFilters;
    private Map<String, String> localAnnotatedActions = new ArrayMap<>();
    private Map<String, String> publicAnnotatedActions = new ArrayMap<>();
    private boolean lifecycleObserverInit = false;

    public static <T extends Context & LifecycleOwner> BroadcastListener initialize(T context) {
        BroadcastListener broadcastListener = new BroadcastListener(context);
        try {
            context.getLifecycle().addObserver(broadcastListener);
            broadcastListener.setLifecycleObserverInit(true);
        } catch (Exception e) {
            Log.e("BroadcastListener", "initialize error:" + e.getMessage());
        }

        return broadcastListener;
    }

    public void addLocalAnnotatedAction(String action, String methodName) {
        localAnnotatedActions.put(action, methodName);
    }

    private BroadcastListener(Context context) {
        this.context = context;
    }

    //region broadcast receivers (local and public) that invoke methods
    private BroadcastReceiver localReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context cntxt, Intent intent) {
            try {
                if (localAnnotatedActions != null && localAnnotatedActions.containsKey(intent.getAction())) {
                    String methodName = localAnnotatedActions.get(intent.getAction());
                    if (methodName != null) {

                        boolean invoked = false;
                        try {
                            if (intent.getExtras() != null) {
                                Class<?>[] paramClasses = new Class<?>[intent.getExtras().size()];
                                Object[] paramValues = new Object[intent.getExtras().size()];
                                int i = 0;
                                for (String extraKey : intent.getExtras().keySet()) {
                                    paramValues[i] = intent.getExtras().get(extraKey);
                                    paramClasses[i] = Object.class;//paramValues[i].getClass();
                                    i++;
                                }
                                context.getClass().getMethod(methodName, paramClasses).invoke(context, paramValues);
                                invoked = true;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "localReceiver.onReceive error:" + e.getMessage());
                        }

                        if (invoked) return;
                        try {
                            context.getClass().getMethod(methodName, Intent.class).invoke(context, intent);
                            invoked = true;
                        } catch (Exception e) {
                            Log.e(TAG, "localReceiver.onReceive error:" + e.getMessage());
                        }

                        if (invoked) return;
                        try {
                            context.getClass().getMethod(methodName, Parcelable.class).invoke(context, intent);
                            invoked = true;
                        } catch (Exception e) {
                            Log.e(TAG, "localReceiver.onReceive error:" + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "localReceiver.onReceive error:" + e.getMessage());
            }
        }
    };

    private BroadcastReceiver publicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context cntxt, Intent intent) {
            Log.d(TAG, "PUBLIC RECEIVER!");
            if (publicAnnotatedActions != null && publicAnnotatedActions.containsKey(intent.getAction())) {
                try {
                    String methodName = publicAnnotatedActions.get(intent.getAction());
                    if (methodName != null)
                        context.getClass().getMethod(methodName, Intent.class).invoke(context, intent);
                } catch (Exception e) {
                    Log.e(TAG, "publicReceiver.onReceive error:" + e.getMessage());
                }
            }
        }
    };

    //endregion

    //region creating intent filters for local and public broadcasts

    private void getLocalAnnotatedActions() {
        for (Method method : context.getClass().getMethods()) {
            OnBroadcastReceived annotation = method.getAnnotation(OnBroadcastReceived.class);
            if (annotation != null)
                if (annotation.scope() == OnBroadcastReceived.Scope.LOCAL)
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

    public boolean isLifecycleObserverInit() {
        return lifecycleObserverInit;
    }

    public void setLifecycleObserverInit(boolean lifecycleObserverInit) {
        this.lifecycleObserverInit = lifecycleObserverInit;
    }
}
