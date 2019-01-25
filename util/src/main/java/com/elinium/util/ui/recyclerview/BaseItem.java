package com.elinium.util.ui.recyclerview;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elinium.util.ui.layout.Layout;

import java.util.Comparator;
import java.util.List;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by amiri on 9/9/2017.
 */

public abstract class BaseItem<T extends BaseViewHolder, DATA_TYPE> implements Comparable<BaseItem> {
    private static final String TAG = "BaseItem";
    private IItemObserver parentItemObserver;
    protected DATA_TYPE data;
    protected int position;
    protected long id;
    protected long group;
    protected boolean clickHandlesBySubUiItems = false;
    protected OnItemClicked<DATA_TYPE> onItemClicked;
    protected BaseViewHolder viewHolder;

    public interface OnItemClicked<DATA_TYPE> {
        void onItemClicked(View v, DATA_TYPE data, int position);
    }

    public abstract void bind(BaseViewHolder viewHolder, int position);

    public BaseItem(@NonNull long group, @NonNull long id, DATA_TYPE data, OnItemClicked<DATA_TYPE> onItemClicked) {
        this.data = data;
        this.onItemClicked = onItemClicked;
        this.group = group;
        this.id = id;
    }

    public BaseItem(DATA_TYPE data, OnItemClicked<DATA_TYPE> onItemClicked) {
        this.data = data;
        this.onItemClicked = onItemClicked;
    }

    public void setOnItemClicked(OnItemClicked onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

    public @LayoutRes
    int getLayout() {

        try {
            Layout layout = getClass().getAnnotation(Layout.class);
            if (layout != null) return layout.id();
        } catch (Exception e) {
            Log.e(TAG,
                    "BaseItem layout id is not specified. use @Layout annotation above your BaseItem class.");
        }
        Log.e(TAG,
                "BaseItem layout id is not specified. use @Layout annotation above your BaseItem class.");
        return 0;
    }

    protected BaseViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        try {
            return new BaseViewHolder(inflater.inflate(getLayout(), parent, false));
        } catch (Exception e) {
            Log.e(TAG, "BaseItem.onCreateViewHolder error: " + e.getMessage());
        }
        return null;
    }

    public void bind(BaseViewHolder viewHolder, int position, List<Object> payloads) {
        this.viewHolder = viewHolder;
        viewHolder.itemView.setTag(this);
        try {
            bind(viewHolder, position);
            if (!clickHandlesBySubUiItems) setOnClickListener(viewHolder);
        } catch (Exception e) {
            Log.e(TAG, "BaseItem.bind error: " + e.getMessage());
        }
    }

    public void setOnClickListener(final BaseViewHolder viewHolder) {
        if (onItemClicked != null && viewHolder != null && viewHolder.getAdapterPosition() != NO_POSITION) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        onItemClicked.onItemClicked(view, data, viewHolder.getAdapterPosition());
                    } catch (Exception e) {
                        Log.e(TAG, "BaseItem.OnClickListener error: " + e.getMessage());
                    }
                }
            });
        }
    }

    public void setItemObserver(IItemObserver itemObserver) {
        this.parentItemObserver = itemObserver;
    }

    public long getDataItemId() {
        return id;
    }

    public abstract long getSortIndex();

    public long getGroupId() {
        return group;
    }

    protected DATA_TYPE getDataItem() {
        return data;
    }

    public void unbindView(T viewHolder) {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != getClass()) return false;
        BaseItem objItem = (BaseItem) obj;
        if (objItem.getGroupId() != getGroupId()) return false;
        if (objItem.getDataItemId() != getDataItemId()) return false;
        return true;
    }

    @Override
    public int compareTo(@NonNull BaseItem baseItem) {
        return Long.compare(id, baseItem.id);
    }
}
