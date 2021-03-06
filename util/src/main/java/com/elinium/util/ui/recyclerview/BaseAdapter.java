package com.elinium.util.ui.recyclerview;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by amiri on 9/9/2017.
 */

public class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IItemObserver {
    private List<BaseItem> items;
    private Map<Integer, BaseItem> itemLayouts;
//    private Map<Long, Integer> groupFirstPositions;
//    private Map<Long, Integer> groupLastPositions;

    private class Ascending implements Comparator<BaseItem> {
        @Override
        public int compare(BaseItem t1, BaseItem t2) {
            if (t1.getGroupId() == t2.getGroupId()) {
                if (t1.getSortIndex() == t2.getSortIndex()) {
                    return (int) (t1.getDataItemId() - t2.getDataItemId());
                } else {
                    return (int) (t1.getSortIndex() - t2.getSortIndex());
                }
            } else {
                return (int) (t1.getGroupId() - t2.getGroupId());
            }
        }
    }

    private class Descending implements Comparator<BaseItem> {
        @Override
        public int compare(BaseItem t1, BaseItem t2) {
            if (t1.getGroupId() == t2.getGroupId()) {
                if (t1.getSortIndex() == t2.getSortIndex()) {
                    return (int) (t1.getDataItemId() - t2.getDataItemId());
                } else {
                    return (int) (t1.getSortIndex() - t2.getSortIndex());
                }
            } else {
                return (int) (t1.getGroupId() - t2.getGroupId());
            }
        }
    }

    public void sortAscending() {
        Collections.sort(items, new Ascending());
        notifyDataSetChanged();
    }

    public void sortDescending() {
        Collections.sort(items, new Descending());
        notifyDataSetChanged();
    }

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

    public <T extends BaseItem> ArrayList<T> getAllItemsByType(Class<T> itemClass) {
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
        //updateGroupPositions();//updateFirstAndLastGroupPositions(item);
    }

    public int addOrUpdate(BaseItem item) {
        return addOrUpdate(item, null);
    }

    public int addOrUpdate(BaseItem item, String payLoad) {
        int position = updateIfExist(item, payLoad);
        if (position < 0) add(item);//if not exist add it.
        return position;
    }

    public void addOrUpdateToTopOfGroup(BaseItem item, long groupId, String payLoad) {
        if (updateIfExist(item, payLoad) < 0) {
            int firstPosition = getFirstOf(groupId);
            if (firstPosition >= 0) {
                addItemAtPosition(item, firstPosition);
            } else {
                addItemToEnd(item);
            }
        }
    }

    public void addOrUpdateToBottomOfGroup(BaseItem item, long groupId) {
        if (updateIfExist(item, null) < 0) {
            int lastPosition = getLastOf(groupId);
            if (lastPosition >= 0) {
                lastPosition++;
                addItemAtPosition(item, lastPosition);
            } else {
                addItemToEnd(item);
            }
        }
    }

    public void addOrUpdateToBottomOfGroup(BaseItem item, long groupId, Object payLoad) {
        if (updateIfExist(item, payLoad) < 0) {
            int lastPosition = getLastOf(groupId);
            if (lastPosition >= 0) {
                lastPosition++;
                addItemAtPosition(item, lastPosition);
            } else {
                addItemToEnd(item);
            }
        }
    }

    public void addBatch(List<? extends BaseItem> items) {
        if (items == null || items.size() == 0) return;
        BaseItemDiffCallback callback = new BaseItemDiffCallback(this.items, items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        for (BaseItem item : items) item.setItemObserver(this);
        diffResult.dispatchUpdatesTo(this);

        this.items.addAll(items);
        addToItemLayouts(items);
        //updateGroupPositions();//updateFirstAndLastGroupPositions(items);
    }

    public void addBatchWithoutDispatch(List<BaseItem> items) {
        for (BaseItem item : items) {
            item.setItemObserver(this);
        }
        this.items.addAll(items);
        addToItemLayouts(items);
        //updateGroupPositions();//updateFirstAndLastGroupPositions(items);
    }

    public void addBatchAtIndex(List<BaseItem> items, int index) {
        BaseItemDiffCallback callback = new BaseItemDiffCallback(this.items, items);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        for (BaseItem item : items) item.setItemObserver(this);
        this.items.addAll(index, items);
        addToItemLayouts(items);
        diffResult.dispatchUpdatesTo(this);
        //updateGroupPositions();//updateFirstAndLastGroupPositions(items);
    }

    public void addItemToEnd(BaseItem item) {
        int position = getItemCount();
        item.setItemObserver(this);
        items.add(position, item);
        addToItemLayouts(item);
        //int currentCount = getItemCountOf(item.getClass());
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());
        //updateGroupPositions();
        //updateFirstAndLastGroupPositions(item);
    }

    public void addItemAfterType(BaseItem item, Class<? extends BaseItem> itemClass) {
        int maxPosition = -1;
        for (BaseItem sameType : items) {
            if (itemClass.isInstance(sameType)) {
                if (maxPosition < sameType.position) maxPosition = sameType.position;
            }
        }
        maxPosition++;
        item.setItemObserver(this);
        items.add(maxPosition, item);
        addToItemLayouts(item);
        notifyItemInserted(maxPosition);
        notifyItemRangeChanged(maxPosition, getItemCount());
        //updateGroupPositions();//updateFirstAndLastGroupPositions(item);
    }

    public void addItemAfter(BaseItem item, BaseItem afterItem) {
        int maxPosition = -1;
        for (BaseItem sameItem : items) {
            if (afterItem.equals(sameItem)) {
                maxPosition = sameItem.position;
            }
        }
        maxPosition++;
        item.setItemObserver(this);
        items.add(maxPosition, item);
        addToItemLayouts(item);
        notifyItemInserted(maxPosition);
        notifyItemRangeChanged(maxPosition, getItemCount());
        //updateGroupPositions();//updateFirstAndLastGroupPositions(item);
    }

    public void addItemAtPosition(BaseItem item, int position) {
        int cnt = getItemCount();
        position = position < 0 ? 0 : (position > cnt) ? cnt : position;
        item.setItemObserver(this);
        items.add(position, item);
        //updateGroupPositions();//updateFirstAndLastGroupPositions(item);
        addToItemLayouts(item);
        int currentCount = getItemCountOf(item.getClass());
        notifyItemInserted(position);
        notifyItemRangeChanged(position, cnt);

    }
    //endregion

    //region update First And Last Group Positions
//    private int updateFirstPositionForGroupId(long groupId) {
//        int min = -1;
//        for (int i = getItemCount() - 1; i >= 0; i--) {
//            if (items.get(i).getGroupId() == groupId) min = i;
//        }
//        if (min >= 0) groupFirstPositions.put(groupId, min);
//        Log.w("BaseAdapter", "group " + groupId + " first index: " + min);
//        return min;
//    }
//
//    public int updateLastPositionForGroupId(long groupId) {
//        int max = -1;
//        for (int i = 0; i < getItemCount(); i++) {
//            if (items.get(i).getGroupId() == groupId) max = i;
//        }
//        if (max >= 0) groupLastPositions.put(groupId, max);
//        Log.w("BaseAdapter", "group " + groupId + " last index: " + max);
//        return max;
//    }
//
//    private void updateFirstAndLastGroupPositions(List<? extends BaseItem> items) {
//        List<Long> groups = new ArrayList<>();
//        for (BaseItem item : items) {
//            if (!groups.contains(item.getGroupId())) groups.add(item.getGroupId());
//        }
//
//        for (long grp : groups) {
//            updateFirstAndLastGroupPositions(grp);
//        }
//    }
//
//    private void updateFirstAndLastOfGroups(List<Long> groups) {
//        for (long grp : groups) {
//            updateFirstAndLastGroupPositions(grp);
//        }
//    }

//
//    private void updateFirstAndLastGroupPositions(BaseItem item) {
//        updateFirstAndLastGroupPositions(item.getGroupId());
//    }

//    private void updateGroupPositions() {
//        for (long groupId : groupFirstPositions.keySet()) updateFirstAndLastGroupPositions(groupId);
//    }

//    private void updateFirstAndLastGroupPositions(long group) {
////        groupFirstPositions.remove(group);
////        groupLastPositions.remove(group);
//
//        updateLastPositionForGroupId(group);
//        updateFirstPositionForGroupId(group);
//    }
    //endregion

    //region Remove Item Functions
    public void removeItemAtPosition(int position) {
        removeItemAtPosition(position, true);
    }

    public void removeItemByGroupId(long group, long id) {
        int position = -1;
        //if (groupFirstPositions.get(group) != null) first = groupFirstPositions.get(group);
        int last = items.size() - 1;
        //if (groupLastPositions.get(group) != null) last = groupLastPositions.get(group);
        if (last >= 0)
            for (int i = 0; i <= last; i++) {
                if (items.get(i).getGroupId() == group && items.get(i).getDataItemId() == id) {
                    position = i;
                    break;
                }
            }
        if (position >= 0) removeItemAtPosition(position, true);
    }

    public void removeAllByGroupId(long group) {
        //if (groupFirstPositions.get(group) != null) first = groupFirstPositions.get(group);
        int last = items.size() - 1;
        //if (groupLastPositions.get(group) != null) last = groupLastPositions.get(group);

        List<BaseItem> tempItems = new ArrayList<>();
        List<Long> groups = new ArrayList<>();
        BaseItem deletedInstance = null;
        if (last >= 0) {
            BaseItem item;
            for (int i = 0; i <= last; i++) {
                item = items.get(i);

                if (item.getGroupId() != group)
                    tempItems.add(items.get(i));
                else {
                    if (!groups.contains(item.getGroupId())) groups.add(item.getGroupId());
                    if (deletedInstance == null) deletedInstance = item;
                }
                //removeItemAtPosition(i, true);
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

        //updateGroupPositions();//updateFirstAndLastOfGroups(groups);


        //updateGroupPositions();
    }


    public void removeItemAtPosition(int position, boolean notify) {
        if (position < 0 || position > getItemCount()) {
            return;
        }

        BaseItem item = items.get(position);
        long group = item.getGroupId();
        /// If this is the last item of this Type
        if (getItemCountOf(item.getClass()) == 1) removeFromItemLayouts(item);

        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
        //updateGroupPositions();//updateFirstAndLastGroupPositions(group);
    }

    public void removeAllItemsByType(Class<? extends BaseItem> itemClass) {
        if (getItemCountOf(itemClass) == 0) return;

        List<BaseItem> tempItems = new ArrayList<>();
        List<Long> groups = new ArrayList<>();
        BaseItem deletedInstance = null;
        for (BaseItem item : items) {
            if (!itemClass.isInstance(item)) {
                tempItems.add(item);
            } else {
                if (!groups.contains(item.getGroupId())) groups.add(item.getGroupId());
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

        //updateGroupPositions();//updateFirstAndLastOfGroups(groups);
    }

    public void removeAllItemsByTypeAndGroup(Class<? extends BaseItem> itemClass, Long group) {
        if (getItemCountOf(itemClass) == 0) return;

        List<BaseItem> tempItems = new ArrayList<>();
        List<Long> groups = new ArrayList<>();
        BaseItem deletedInstance = null;
        for (BaseItem item : items) {
            if ((itemClass.isInstance(item) && item.getGroupId() == group)) {
                if (!groups.contains(item.getGroupId())) groups.add(item.getGroupId());
                if (deletedInstance == null) deletedInstance = item;
            } else {
                tempItems.add(item);
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

        //updateGroupPositions();//updateFirstAndLastOfGroups(groups);
    }

    public void removeAll() {
        for (BaseItem item : items) {
            item.setItemObserver(null);
        }
        items.clear();
        itemLayouts = new ArrayMap<>();
//        groupFirstPositions.clear();
//        groupLastPositions.clear();
        notifyDataSetChanged();
    }
    //endregion

    public int getPositionOf(long groupId, long id) {
        int position = -1;
        int first = getFirstOf(groupId);
        int last = getLastOf(groupId);
        if (first >= 0 && last >= 0) {
            for (int i = first; i <= last; i++) {
                BaseItem item = items.get(i);
                if (item.getGroupId() == groupId && item.getDataItemId() == id) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    public int getPositionOf(BaseItem item) {

        int position = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(item)) return i;
        }
        return position;
    }

    private int updateIfExist(BaseItem item, Object payload) {
        int position = getPositionOf(item);
        if (position >= 0) {
            items.get(position).data = item.getDataItem();
            // notifyItemChanged(position);
            notifyItemChanged(position, payload);
        }
        //if (item.getGroupId() == 0) {
        // Log.d("BASE_ADAPTER", "position of item.id " + item.getDataItemId() + " is " + position);
        //}
        return position;
    }

    private int getFirstOf(long groupId) {
        int last = items.size() - 1;
        if (last >= 0) {
            for (int i = 0; i <= last; i++) {
                if (items.get(i).getGroupId() == groupId) return i;
            }
        }

        return -1;
    }

    private int getLastOf(long groupId) {
        int last = items.size() - 1;
        if (last >= 0) {
            for (int i = last; i >= 0; i--) {
                if (items.get(i).getGroupId() == groupId) return i;
            }
        }

        Log.d("BASE_ADAPTER", " last of groupid " + groupId + " is " + last);
        return last;
    }

    public void replaceAllItems(List<BaseItem> newItems) {
//        items = new ArrayList<>(newItems);
//        itemLayouts = new ArrayMap<>();
//        groupFirstPositions.clear();
//        groupLastPositions.clear();
//
//        addToItemLayouts(newItems);
//        notifyDataSetChanged();
        removeAll();
        addBatch(newItems);
    }

    public int getGroupCount(long group) {
        int cnt = 0;
        for (BaseItem item : items) if (item.getGroupId() == group) cnt++;
        return cnt;
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
//        if (holder.itemView.getTag() instanceof BaseItem) {
//            BaseItem item = (BaseItem) holder.itemView.getTag();
//            if (item != null) {
//                item.unbindView((BaseViewHolder) holder);
//            }
//        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
//        if (holder instanceof BaseViewHolder) {
//            Log.d("BaseAdapter", "Clear Animation");
//            //((BaseViewHolder) holder).itemView.clearAnimation();
//        }
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return true;
    }

    public void notifyItemChanged(long group, long id) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).group == group && items.get(i).id == id)
                notifyItemChanged(i);
    }
}
