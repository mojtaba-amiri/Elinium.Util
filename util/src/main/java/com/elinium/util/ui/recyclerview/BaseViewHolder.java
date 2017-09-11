package com.elinium.util.ui.recyclerview;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;

/**
 * Created by amiri on 9/9/2017.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
  private Map<Integer, View> views = new ArrayMap<>();

  BaseViewHolder(View itemView) {
    super(itemView);
  }

  public <T extends View> T get(int resourceId) {
    if (!views.containsKey(resourceId)) views.put(resourceId, itemView.findViewById(resourceId));
    return (T) views.get(resourceId);
  }
}
