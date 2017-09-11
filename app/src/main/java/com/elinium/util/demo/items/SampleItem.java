package com.elinium.util.demo.items;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.elinium.util.demo.R;
import com.elinium.util.ui.recyclerview.BaseItem;
import com.elinium.util.ui.recyclerview.BaseViewHolder;
import com.elinium.util.ui.recyclerview.Layout;

/**
 * Created by amiri on 9/9/2017.
 */

@Layout(id = R.layout.item_sample)
public class SampleItem extends BaseItem<BaseViewHolder, String> {
  private String label;
  private int index;

  public SampleItem(int index, String label) {
    this.label = label;
    this.index = index;
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

  @Override public View.OnClickListener onClicked(final int position) {
    return new View.OnClickListener() {
      @Override public void onClick(View view) {
        Log.d("SampleItem", "Click:" + position);
      }
    };
  }
}
