package com.elinium.util.ui.recyclerview;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by O1 on 2016-12-06.
 */

public class BaseItemDiffCallback extends DiffUtil.Callback {

  private final List<? extends BaseItem> oldList;
  private final List<? extends BaseItem> newList;

  public BaseItemDiffCallback(List<? extends BaseItem> oldList, List<? extends BaseItem> newList) {
    this.oldList = oldList;
    this.newList = newList;
  }

  @Override
  public int getOldListSize() {
    return oldList != null ? oldList.size() : 0;
  }

  @Override
  public int getNewListSize() {
    return newList != null ? newList.size() : 0;
  }

  @Override
  public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
    BaseItem oldItem = oldList.get(oldItemPosition);
    BaseItem newItem = newList.get(newItemPosition);

    if (oldItem.getLayout() != newItem.getLayout()) {
      return false;
    }

    return oldItem.getDataItemId() == newItem.getDataItemId();
  }

  @Override
  public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
    return newList.get(newItemPosition).equals(oldList.get(oldItemPosition));
  }

  @Nullable
  @Override
  public Object getChangePayload(int oldItemPosition, int newItemPosition) {
    // Implement method if you're going to use ItemAnimator
    return super.getChangePayload(oldItemPosition, newItemPosition);
  }
}
