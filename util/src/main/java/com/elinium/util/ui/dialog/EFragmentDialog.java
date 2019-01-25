package com.elinium.util.ui.dialog;

import android.app.Dialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.elinium.util.ui.layout.Layout;

import java.lang.reflect.ParameterizedType;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public abstract class EFragmentDialog<T extends ViewModel, INPUT_TYPE, OUTPUT_TYPE> extends AppCompatDialogFragment {
    protected INPUT_TYPE dialogInput;
    protected T viewModel;

    private Layout layout;
    private EDialog.OnDialogResult<OUTPUT_TYPE> callback;
    Unbinder unbinder;

    public EFragmentDialog() {
        callback = null;
    }

    public EFragmentDialog(EDialog.OnDialogResult<OUTPUT_TYPE> resultCallback) {
        callback = resultCallback;
    }

    public EFragmentDialog(INPUT_TYPE input, EDialog.OnDialogResult<OUTPUT_TYPE> resultCallback) {
        dialogInput = input;
        callback = resultCallback;
    }

    public EFragmentDialog(INPUT_TYPE input, boolean fullScreen, EDialog.OnDialogResult<OUTPUT_TYPE> resultCallback) {
        dialogInput = input;
        callback = resultCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        try {
            if (layout == null) layout = getLayout();
        } catch (Exception e) {
            Timber.e(e);
        }
        view = inflater.inflate(layout.id(), container);
        setCancelable(layout.cancelable());
        unbinder = ButterKnife.bind(this, view);

        if (viewModel == null) setViewModel();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (viewModel == null) setViewModel();
    }

    private void setViewModel() {
        try {
            Class<T> persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            viewModel = ViewModelProviders.of(getActivity()).get(persistentClass);
        } catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }
    }

    private Layout getLayout() throws Exception {
        Layout layout = getClass().getAnnotation(Layout.class);
        if (layout != null) {
            return layout;
        } else {
            throw new Exception("layout id is not specified. use @Layout annotation above your Dialog class.");
        }
    }

    @Override
    public void onResume() {
        try {
            if (layout == null) layout = getLayout();
            if (layout.fullScreen()) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(getDialog().getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                getDialog().getWindow().setAttributes(lp);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        try {
            if (layout == null) layout = getLayout();
            if (layout.noTitle())
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            if (layout.fullScreen())
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setCancelable(layout.cancelable());
        } catch (Exception e) {
            Timber.e(e);
        }
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    protected void returnResult(OUTPUT_TYPE output) {
        if (callback != null) callback.onDialogResult(output);
        dismissAllowingStateLoss();
    }
}