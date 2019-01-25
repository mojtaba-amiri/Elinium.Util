package com.elinium.util.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.elinium.mvc.BaseOperation;
import com.elinium.util.exceptionhandling.ExceptionHandler;
import com.elinium.util.broadcast.BroadcastListener;
import com.elinium.util.ui.layout.Layout;

import java.util.concurrent.Callable;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by amiri on 9/6/2017.
 */


public abstract class EActivity extends AppCompatActivity implements ExceptionHandler.IExceptionHandler {
    protected final String TAG = getClass().getSimpleName();
    private boolean initialized = false;
    private Unbinder unbinder;
    private BroadcastListener broadcastListener;

    public void addLocalBroadcastAction(String action, String methodName) {
        broadcastListener.addLocalAnnotatedAction(action, methodName);
    }

    public void registerReceivers() {
        if (!broadcastListener.isLifecycleObserverInit()) broadcastListener.registerReceivers();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            broadcastListener = BroadcastListener.initialize(this);

            ExceptionHandler.register(this);
            initialized = true;
            Layout layout = getLayout();
            if (layout == null) {
                Log.e(TAG, "you must add @Layout annotation to you activity class");
                return;
            }

            if (layout.windowFeature() >= 0) {
                requestWindowFeature(layout.windowFeature());
            }

            if (layout.noTitle()) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                if (getSupportActionBar() != null) getSupportActionBar().hide();
            }

            if (layout.transparent()) {
                getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }

            if (layout.fullScreen()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            setContentView(layout.id());
            unbinder = ButterKnife.bind(this);
        } catch (Exception e) {
            Log.e(TAG, "EActivity onCreate error:" + e.getMessage());
        }


    }

    private Layout getLayout() throws Exception {
        Class<?> c = getClass();
        if (!c.isAnnotationPresent(Layout.class)) {
            Timber.e("@Layout Annotation not found");
        }
        Layout layout = c.getAnnotation(Layout.class);
        if (layout != null) {
            return layout;
        } else {
            throw new Exception("EActivity layout id is not specified. use @Layout annotation above your Activity class.");
        }
    }

    private int getLayoutId() {
        Layout layout = null;
        try {
            layout = getLayout();
            return layout.id();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        if (!broadcastListener.isLifecycleObserverInit()) broadcastListener.unregisterReceivers();
        super.onDestroy();
    }

    @Override
    public void onException(String threadName, Throwable throwable) {
        Log.e("EActivity", "" + getClass().getSimpleName() + " Exception:" + throwable.getMessage());
        onUnhandledException(threadName, throwable);
    }

    public void showMessageDialog(String title, String text, String buttonText,
                                  DialogInterface.OnClickListener onButtonClick) {
        AlertDialog.Builder dialogBuilder = getBuilderFor(this, title, text, 0);
        dialogBuilder.setPositiveButton(buttonText, onButtonClick);
        dialogBuilder.create().show();
    }

    public void showTwoOptionsDialog(String title,
                                     String text, String option1Text, String option2Text,
                                     DialogInterface.OnClickListener onOption1Click,
                                     DialogInterface.OnClickListener onOption2Click) {
        AlertDialog.Builder dialogBuilder = getBuilderFor(this, title, text, 0);

        dialogBuilder.setNegativeButton(option2Text, onOption2Click);
        dialogBuilder.setPositiveButton(option1Text, onOption1Click);
        dialogBuilder.create().show();
    }


    public void showTwoOptionsDialogWithCustomView(View view, String title,
                                                   String option1Text, String option2Text,
                                                   DialogInterface.OnClickListener onOption1Click,
                                                   DialogInterface.OnClickListener onOption2Click) {
        AlertDialog.Builder dialogBuilder = getBuilderFor(this, title, "", 0);

        dialogBuilder.setView(view);
        dialogBuilder.setNegativeButton(option2Text, onOption2Click);
        dialogBuilder.setPositiveButton(option1Text, onOption1Click);
        dialogBuilder.create().show();
    }

    public AlertDialog.Builder getBuilderFor(Context context, String title, String msg, int iconResId) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }

        return builder.setTitle(title).setMessage(msg).setIcon(iconResId);
    }

    public <T> void callAndBack(BaseOperation.AsyncContextOperation<T> operation, BaseOperation.OperationCallback<T> callback) {
        callAndBack(null, operation, callback);
    }

    public <T> void callAndBack(Context context, BaseOperation.AsyncContextOperation<T> operation, BaseOperation.OperationCallback<T> callback) {
        Single.fromCallable(new Callable<T>() {
            @Override
            public T call() {
                return operation.Do(context);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T obj) throws Exception {
                        callback.onDone(obj, null);
                    }
                }, throwable -> {
                    callback.onDone(null, throwable);
                });
    }

    public <T> void callAndBack(Callable<T> operation, Consumer<T> callback, Consumer<Throwable> onError) {
        Single.fromCallable(operation).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(callback, onError);
        // callAndBack(null, operation, callback);
    }

    public abstract void onUnhandledException(String threadName, Throwable throwable);

    public FragmentTransaction addFragment(int container, Fragment fragment) {
        return getSupportFragmentManager().beginTransaction().add(container, fragment);
    }

    public FragmentTransaction replaceFragment(int container, Fragment fragment) {
        fragment.setEnterTransition(new Fade());
        return getSupportFragmentManager().beginTransaction().replace(container, fragment);
    }

    public int getBackStackCount() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    @Override
    public void onBackPressed() {
        // Use getSupportFragmentManager() to support older devices
        //FragmentManager fragmentManager = getFragmentManager();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            if (fragmentManager.getBackStackEntryCount() < 1) {
                super.onBackPressed();
            } else {
                fragmentManager.popBackStack();
            }
        }
    }

    public void startActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();
    }

}
