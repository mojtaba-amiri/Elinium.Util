package com.elinium.util.ui.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.elinium.util.ui.layout.Layout;

import butterknife.ButterKnife;

/**
 * Created by amiri on 9/29/2017.
 */

public class EDialog<INPUT_TYPE, OUTPUT_TYPE> extends AppCompatDialog {
    INPUT_TYPE dialogInput;
    OnDialogResult callback;

    public interface OnDialogResult<OUTPUT> {
        void onDialogResult(OUTPUT output);
    }

    public EDialog(Context context, OnDialogResult<OUTPUT_TYPE> resultCallback) {
        super(context);
        callback = resultCallback;
    }

    public EDialog(Context context, INPUT_TYPE input, OnDialogResult<OUTPUT_TYPE> resultCallback) {
        super(context);
        dialogInput = input;
        callback = resultCallback;
    }

    public void returnResult(OUTPUT_TYPE output) {
        if (callback != null) callback.onDialogResult(output);
        dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Layout layout = getLayout();
            if (layout.windowFeature() >= 0) {
                requestWindowFeature(layout.windowFeature());
            }

            if (layout.noTitle()) {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
            }

            if (layout.transparent()) {
                getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }

            if (layout.fullScreen()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            setContentView(getLayoutId());
            ButterKnife.bind(this);
        } catch (Exception e) {
            Log.e("EDialog", "onCreate:" + e.getMessage());
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

}
