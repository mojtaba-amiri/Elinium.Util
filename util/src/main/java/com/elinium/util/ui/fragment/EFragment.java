package com.elinium.util.ui.fragment;

import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.elinium.mvc.BaseOperation;
import com.elinium.util.ui.DataModel;
import com.elinium.util.ui.dialog.EDialog;
import com.elinium.util.ui.layout.Layout;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.Callable;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public abstract class EFragment<T extends ViewModel> extends Fragment {
    protected final String TAG = getClass().getSimpleName();
    private View rootView;
    private Unbinder unbinder;
    protected T viewModel;
    protected CompositeDisposable disposable = new CompositeDisposable();

    private EDialog loadingDialog;

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

    private DataModel getDataModel() throws Exception {
        DataModel layout = getClass().getAnnotation(DataModel.class);
        if (layout != null) {
            return layout;
        } else {
            throw new Exception("EActivity layout id is not specified. use @Layout annotation above your Activity class.");
        }
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
        if (viewModel == null) setViewModel();
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

    protected void startActivity(Class activity) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), activity);
            startActivity(intent);
            getActivity().finish();
        }
    }

    public FragmentTransaction addFragment(int container, Fragment fragment) {
        return getFragmentManager().beginTransaction().add(container, fragment);
    }

    public FragmentTransaction replaceFragment(int container, Fragment fragment) {
        return getFragmentManager().beginTransaction().replace(container, fragment);
    }

    protected void showMessage(String msg) {

        Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG).show();
    }

    public void showTwoOptionsDialog(String title,
                                     String text, String option1Text, String option2Text,
                                     DialogInterface.OnClickListener onOption1Click,
                                     DialogInterface.OnClickListener onOption2Click) {
        AlertDialog.Builder dialogBuilder = getBuilderFor(getActivity(), title, text, 0);

        dialogBuilder.setNegativeButton(option2Text, onOption2Click);
        dialogBuilder.setPositiveButton(option1Text, onOption1Click);
        dialogBuilder.create().show();
    }


    public void showTwoOptionsDialogWithCustomView(View view, String title,
                                                   String option1Text, String option2Text,
                                                   DialogInterface.OnClickListener onOption1Click,
                                                   DialogInterface.OnClickListener onOption2Click) {
        AlertDialog.Builder dialogBuilder = getBuilderFor(getActivity(), title, "", 0);

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


    public <T> void callAndBack(Callable<T> callable, Consumer<T> resultConsumer, Consumer<Throwable> errorConsumer) {
        disposable.add(Observable.fromCallable(callable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(resultConsumer, errorConsumer));
    }

    protected <T extends EDialog> void showLoadingDialog(T dialog) {
        if (dialog != null && !dialog.isShowing())
            dialog.show();
        else
            return;
        loadingDialog = dialog;
    }

    protected void dismissLoading() {
        if (loadingDialog != null) loadingDialog.dismiss();
    }
}
