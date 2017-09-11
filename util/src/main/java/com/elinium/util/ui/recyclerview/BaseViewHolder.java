package com.elinium.util.ui.recyclerview;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by amiri on 9/9/2017.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private View.OnClickListener onClickListener;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public <T> T get(int resourceId) {
        return (T) itemView.findViewById(resourceId);
    }

    public void setOnItemClickListener(View.OnClickListener listener) {
        onClickListener = listener;
    }

    public View.OnClickListener getOnItemClickListener() {
        return onClickListener;
    }

}
