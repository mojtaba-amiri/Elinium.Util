package com.elinium.util.ui.recyclerview;

import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by amiri on 9/9/2017.
 */

public abstract class BaseItem<T extends BaseViewHolder> {
    private T viewHolder;
    private IItemObserver parentItemObserver;

    public abstract void bind(BaseViewHolder viewHolder, int position);

    public abstract @LayoutRes
    int getLayout();

    protected BaseViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        try {
            return new BaseViewHolder(inflater.inflate(getLayout(), parent, false));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void bind(T viewHolder, int position, List<Object> payloads) {
        this.viewHolder = viewHolder;
        viewHolder.itemView.setTag(this);
        try {
            bind(viewHolder, position);
            setOnClickListener();
        } catch (Exception e) {
            Log.e("TAGGG", "err: " + e.getMessage());
        }
    }

    public void setOnClickListener() {
        if (getViewHolder() != null && getViewHolder().getOnItemClickListener() != null)
            getViewHolder().itemView.setOnClickListener(getViewHolder().getOnItemClickListener());
    }

    public void setItemObserver(IItemObserver itemObserver) {
        this.parentItemObserver = itemObserver;
    }


    public abstract int getDataItemId();

    public abstract Object getDataItem();

    public T getViewHolder() {
        return viewHolder;
    }

    public void unbindView(T viewHolder) {
    }
}
