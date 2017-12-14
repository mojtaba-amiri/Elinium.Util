package com.elinium.util.ui.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.elinium.mvc.BaseOperation;
import com.elinium.util.exceptionhandling.ExceptionHandler;
import com.elinium.util.broadcast.BroadcastListener;
import com.elinium.util.ui.layout.Layout;

import java.util.concurrent.Callable;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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

    public void registerReceivers(){
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

            setContentView(getLayoutId());
            unbinder = ButterKnife.bind(this);
        } catch (Exception e) {
            Log.e(TAG, "EActivity onCreate error:" + e.getMessage());
        }


    }

    private Layout getLayout() throws Exception {
        Layout layout = getClass().getAnnotation(Layout.class);
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

    public <T> void DoAsync(BaseOperation.AsyncOperation<T> operation, BaseOperation.OperationCallback<T> callback) {
        DoAsync(null, operation, callback);
    }


    public <T> void DoAsync(Context context, BaseOperation.AsyncOperation<T> operation, BaseOperation.OperationCallback<T> callback) {
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


    public abstract void onUnhandledException(String threadName, Throwable throwable);
}
