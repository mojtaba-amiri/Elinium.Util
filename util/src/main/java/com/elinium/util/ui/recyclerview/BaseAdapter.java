package com.elinium.util.ui.recyclerview;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by amiri on 9/9/2017.
 */

public class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IItemObserver {
    private List<BaseItem> items;
    private Map<Integer, BaseItem> itemLayouts;


    public BaseAdapter() {
        items = new ArrayList<>();
        itemLayouts = new ArrayMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int layoutResId) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        BaseItem item = itemLayouts.get(layoutResId);
        if (item != null) return item.onCreateViewHolder(inflater, parent);
        return null;
    }

    public int getAdapterPosition(BaseItem item) {
        return items.indexOf(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Binding through payload
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position,
                                 List<Object> payloads) {
        final BaseItem item = items.get(position);
        item.bind((BaseViewHolder) holder, position, payloads);
    }

    @Override
    public int getItemViewType(int position) {
        BaseItem contentItem = items.get(position);
        if (contentItem == null) throw new RuntimeException("Invalid position " + position);
        return items.get(position).getLayout();
    }

    public List<BaseItem> getItems() {
        return items;
    }

    public BaseItem getItemAt(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public <T extends BaseItem> ArrayList<T> getAllItemsByType(Class<? extends BaseItem> itemClass) {
        ArrayList<T> tempItems = new ArrayList<>();
        for (BaseItem item : items) {
            if (itemClass.isInstance(item)) {
                tempItems.add((T) item);
            }
        }
        return tempItems;
    }

    //region layout types management
    public void addToItemLayouts(BaseItem newItem) {
        if (!itemLayouts.containsKey(newItem.getLayout()))
            itemLayouts.put(newItem.getLayout(), newItem);
    }

    public void addToItemLayouts(List<? extends BaseItem> newItems) {
        for (BaseItem item : newItems)
            if (!itemLayouts.containsKey(item.getLayout())) itemLayouts.put(item.getLayout(), item);
    }

    public void removeFromItemLayouts(List<BaseItem> removedItems) {
        for (BaseItem item : removedItems)
            if (itemLayouts.containsKey(item.getLayout())) itemLayouts.remove(item.getLayout());
    }

    public void removeFromItemLayouts(BaseItem removedItem) {
        if (itemLayouts.containsKey(removedItem.getLayout()))
            itemLayouts.remove(removedItem.getLayout());
    }
    //endregion

    public int getItemCountOf(Class<? extends BaseItem> itemClass) {
        int cnt = 0;
        for (BaseItem item : items) if (itemClass.isInstance(item)) cnt++;
        return cnt;
    }

    //region Add Item Functions
    public void add(BaseItem item) {
        int itemCountBefore = getItemCount();
        item.setItemObserver(this);
        items.add(item);
        addToItemLayouts(item);
        notifyItemInserted(itemCountBefore);
    }

    public void addBatch(List<? extends BaseItem> items) {
        BaseItemDiffCallback callback = new BaseItemDiffCallback(this.items, items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        for (BaseItem item : items) item.setItemObserver(this);
        diffResult.dispatchUpdatesTo(this);
        this.items.addAll(items);
        addToItemLayouts(items);
    }

    public void addBatchWithoutDispatch(List<BaseItem> items) {
        for (BaseItem item : items) {
            item.setItemObserver(this);
        }
        this.items.addAll(items);
        addToItemLayouts(items);
    }

    public void addBatchAtIndex(List<BaseItem> items, int index) {
        BaseItemDiffCallback callback = new BaseItemDiffCallback(this.items, items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        for (BaseItem item : items) item.setItemObserver(this);
        this.items.addAll(index, items);
        addToItemLayouts(items);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addItemAtPosition(BaseItem item, int position) {
        position = position < 0 ? 0 : (position > getItemCount()) ? getItemCount() : position;
        item.setItemObserver(this);
        items.add(position, item);
        addToItemLayouts(item);
        int currentCount = getItemCountOf(item.getClass());
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());
    }
    //endregion

    //region Remove Item Functions
    public void removeItemAtPosition(int position) {
        removeItemAtPosition(position, true);
    }

    public void removeItemAtPosition(int position, boolean notify) {
        if (position < 0 || position > getItemCount()) {
            return;
        }

        BaseItem item = items.get(position);

        /// If this is the last item of this Type
        if (getItemCountOf(item.getClass()) == 1) removeFromItemLayouts(item);

        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void removeAllItemsByType(Class<? extends BaseItem> itemClass) {
        if (getItemCountOf(itemClass) == 0) return;

        List<BaseItem> tempItems = new ArrayList<>();
        BaseItem deletedInstance = null;
        for (BaseItem item : items) {
            if (!itemClass.isInstance(item)) {
                tempItems.add(item);
            } else {
                if (deletedInstance == null) deletedInstance = item;
            }
        }

        if (tempItems.size() != items.size()) {
            BaseItemDiffCallback callback = new BaseItemDiffCallback(this.items, tempItems);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
            //typeCount.delete(itemHash);
            diffResult.dispatchUpdatesTo(this);
            items.clear();
            items.addAll(tempItems);
            if (deletedInstance != null) removeFromItemLayouts(deletedInstance);
        }
    }

    public void removeAll() {
        for (BaseItem item : items) {
            item.setItemObserver(null);
        }
        items.clear();
        itemLayouts = new ArrayMap<>();
        notifyDataSetChanged();
    }
    //endregion

    public void replaceAllItems(List<BaseItem> newItems) {
        items = new ArrayList<>(newItems);
        itemLayouts = new ArrayMap<>();
        addToItemLayouts(newItems);
        notifyDataSetChanged();
    }

    @Override
    public void onItemChanged(BaseItem item) {
        notifyItemChanged(getAdapterPosition(item));
    }

    @Override
    public void onItemChanged(BaseItem item, Object payload) {
        notifyItemChanged(getAdapterPosition(item), payload);
    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        BaseItem item = (BaseItem) holder.itemView.getTag();
        if (item != null) {
            item.unbindView((BaseViewHolder) holder);
        }
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return true;
    }
}
