package com.elinium.util.ui.recyclerview;

import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by amiri on 9/9/2017.
 */

public abstract class BaseItem<T extends BaseViewHolder, DATA_TYPE> {
  private IItemObserver parentItemObserver;

  public abstract void bind(BaseViewHolder viewHolder, int position);

  public @LayoutRes int getLayout() {

    try {
      Layout layout = getClass().getAnnotation(Layout.class);
      if (layout != null) return layout.id();
    } catch (Exception e) {
      Log.e("BaseItem",
          "BaseItem layout id is not specified. use @Layout annotation above your BaseItem class.");
    }
    Log.e("BaseItem",
        "BaseItem layout id is not specified. use @Layout annotation above your BaseItem class.");
    return 0;
  }

  protected BaseViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
    try {
      return new BaseViewHolder(inflater.inflate(getLayout(), parent, false));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public void bind(T viewHolder, int position, List<Object> payloads) {
    viewHolder.itemView.setTag(this);
    try {
      bind(viewHolder, position);
      setOnClickListener(viewHolder);
    } catch (Exception e) {
      Log.e("TAGGG", "err: " + e.getMessage());
    }
  }

  public void setOnClickListener(BaseViewHolder viewHolder) {
    if (viewHolder != null && viewHolder.getAdapterPosition() != NO_POSITION) {
      viewHolder.itemView.setOnClickListener(onClicked(viewHolder.getAdapterPosition()));
    }
  }

  public void setItemObserver(IItemObserver itemObserver) {
    this.parentItemObserver = itemObserver;
  }

  public abstract int getDataItemId();

  public abstract DATA_TYPE getDataItem();

  public abstract View.OnClickListener onClicked(final int position);

  public void unbindView(T viewHolder) {
  }
}
