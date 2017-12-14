package com.elinium.mvc;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.util.Log;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amiri on 9/26/2017.
 */

public abstract class BaseController<T> implements DefaultLifecycleObserver {
    protected final String TAG = getClass().getSimpleName().substring(0, Math.min(22, getClass().getSimpleName().length()));
    //private static Controller instance = null;
    private Object iController = null;
    protected T viewModel;

    public static <T extends LifecycleOwner> void register(T context, Class<? extends BaseController> clazz) {
        try {
            BaseController instance = clazz.newInstance();
            context.getLifecycle().addObserver(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Do(Context context, BaseOperation.AsyncOperation<T> operation, BaseOperation.OperationCallback<T> callback) {
        if (getInstance() == null) {
            Log.e(TAG, "You must first register controller");
            return;
        }
        new BaseOperation<>(operation, callback).execute(context);
    }

    public <T> void Do(BaseOperation.AsyncOperation<T> operation, BaseOperation.OperationCallback<T> callback) {
        if (getInstance() == null) {
            Log.e(TAG, "You must first register controller");
            return;
        }

        Single.fromCallable(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return operation.Do(null);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T obj) throws Exception {
                        callback.onDone(obj, null);
                    }
                });
        //new BaseOperation<>(operation, callback).execute(context);
    }


    public void register(LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(getInstance());
    }

    public abstract BaseController getInstance();

    public abstract void clearController();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onControllerOwnerDestroy() {
        clearController();
    }
    //    public static <INTERFACE> void register(INTERFACE iController) {
//        if (instance == null) instance = new Controller();
//        instance.setInterface(iController);
//    }
}
