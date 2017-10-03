package com.elinium.util.ui.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elinium.util.ui.layout.Layout;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by amiri on 10/2/2017.
 */

public abstract class ETabFragment extends Fragment {
    protected final String TAG = getClass().getSimpleName();
    private View rootView;
    private Unbinder unbinder;

    public abstract @DrawableRes
    int getTabIcon();

    public abstract String getTabTitle();


    public abstract void onDataChanged(Object... data);

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
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
