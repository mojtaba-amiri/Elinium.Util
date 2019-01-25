package com.elinium.util.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class ApplicationBackgroundListener implements Application.ActivityLifecycleCallbacks {

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    public interface ApplicationBackgroundCallback {
        void onApplicationEnterBackground();

        void onApplicationEnterForeground();
    }

    ApplicationBackgroundCallback callback;

    public ApplicationBackgroundListener(ApplicationBackgroundCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            if (callback != null) callback.onApplicationEnterForeground();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            if (callback != null) callback.onApplicationEnterBackground();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
