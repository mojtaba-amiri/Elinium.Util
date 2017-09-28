package com.elinium.util.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.elinium.util.exceptionhandling.ExceptionHandler;
import com.elinium.util.broadcast.BroadcastListener;
import com.elinium.util.ui.layout.Layout;

import butterknife.ButterKnife;

/**
 * Created by amiri on 9/6/2017.
 */


public abstract class EActivity extends AppCompatActivity implements ExceptionHandler.IExceptionHandler {
    protected final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            BroadcastListener.initialize(this);
            ExceptionHandler.register(this);

            Layout layout = getLayout();
            if (layout.windowFeature() >= 0) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
            if (layout.fullScreen()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            setContentView(getLayoutId());
            ButterKnife.bind(this);
        } catch (Exception e) {
            Log.e(TAG, "setDefaultUncaughtExceptionHandler error:" + e.getMessage());
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
        super.onDestroy();
    }

    @Override
    public void onException(String threadName, Throwable throwable) {
        Log.e("EActivity", "" + getClass().getSimpleName() + " Exception:" + throwable.getMessage());
        onUnhandledException(threadName, throwable);
    }

    public abstract void onUnhandledException(String threadName, Throwable throwable);
}
