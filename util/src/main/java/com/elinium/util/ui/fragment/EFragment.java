package com.elinium.util.ui.fragment;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elinium.mvc.BaseOperation;
import com.elinium.util.ui.layout.Layout;

import java.io.IOException;
import java.util.concurrent.Callable;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public abstract class EFragment extends Fragment {
    protected final String TAG = getClass().getSimpleName();
    private View rootView;
    private Unbinder unbinder;

    @Nullable
    private ViewLifecycleOwner viewLifecycleOwner;
    static class ViewLifecycleOwner implements LifecycleOwner {
        private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

        @Override
        public LifecycleRegistry getLifecycle() {
            return lifecycleRegistry;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewLifecycleOwner = new ViewLifecycleOwner();
        viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (viewLifecycleOwner != null) {
            viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_START);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewLifecycleOwner != null) {
            viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        }
    }

    @Override
    public void onPause() {
        if (viewLifecycleOwner != null) {
            viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (viewLifecycleOwner != null) {
            viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    public void requestActivityAction(String action, Object... data) {
        ((FragmentAction) getActivity()).onFragmentActionRequest(action, data);
    }

    @Override
    public void onDestroy() {
        if (viewLifecycleOwner != null) {
            viewLifecycleOwner.getLifecycle().handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
            viewLifecycleOwner = null;
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
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

    public <T> void callAndBack(BaseOperation.AsyncOperation<T> operation, BaseOperation.OperationCallback<T> callback) {
        Single.fromCallable(new Callable<T>() {
            @Override
            public T call() {
                return operation.Do();
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

    public <T> void callWeb(Call<T> call, BaseOperation.OperationCallback<T> callback) {
        try {
            Response<T> result = call.execute();
            T response = result.body();

            if (response == null) {
                if (result.errorBody() != null) {
                    Log.e(TAG, "CallWeb error body:" + result.errorBody().string());
                    callback.onDone(null, new Exception(result.errorBody().string()));
                } else {
                    Log.e(TAG, "CallWeb error body: null");
                }
                return;
            }
            callback.onDone(response, null);
        } catch (IOException e) {
            Log.e(TAG, "Call Web Error:" + e.getMessage());
        }

    }
}
