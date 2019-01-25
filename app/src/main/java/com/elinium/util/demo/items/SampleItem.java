package com.elinium.util.demo.items;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.elinium.util.demo.R;
import com.elinium.util.ui.layout.Layout;
import com.elinium.util.ui.recyclerview.BaseItem;
import com.elinium.util.ui.recyclerview.BaseViewHolder;

/**
 * Created by amiri on 9/9/2017.
 */

@Layout(id = R.layout.item_sample)
public class SampleItem extends BaseItem<BaseViewHolder, String> {
  private String label;
  private int index;

  public SampleItem(String data, OnItemClicked<String> onItemClicked) {
    super(data, onItemClicked);
    this.label = data;
  }

  @Override public void bind(BaseViewHolder viewHolder, final int position) {
    TextView txtLabel = viewHolder.get(R.id.txtLabel);
    TextView txtIndex = viewHolder.get(R.id.txtIndex);
    txtIndex.setText(String.valueOf(index));
    txtLabel.setText(label);
  }

  @Override public int getDataItemId() {
    return index;
  }

  @Override public String getDataItem() {
    return null;
  }
}
