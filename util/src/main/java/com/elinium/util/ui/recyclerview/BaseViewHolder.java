package com.elinium.util.ui.recyclerview;

import android.support.annotation.LayoutRes;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.util.Map;

/**
 * Created by amiri on 9/9/2017.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private Map<Integer, View> views = new ArrayMap<>();
    private boolean animateOnAdd = false;
    private int animateLayoutId;
    private int animationDuration;
    private Animation animation;

    public void animatesOnAdd(boolean val) {
        animateOnAdd = val;
    }

    public boolean animatesOnAdd() {
        return animateOnAdd;
    }

    public void setAnimateLayoutId(int id) {
        animateLayoutId = id;
    }

    public int animateLayoutId() {
        return animateLayoutId;
    }

    public void setAnimation(Animation anim) {
        animation = anim;
    }

    public Animation getAnimation() {
        return animation;
    }

    protected BaseViewHolder(View itemView) {
        super(itemView);
    }

    public <T extends View> T get(int resourceId) {
        if (!views.containsKey(resourceId))
            views.put(resourceId, itemView.findViewById(resourceId));
        return (T) views.get(resourceId);
    }


    public int getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }
}
