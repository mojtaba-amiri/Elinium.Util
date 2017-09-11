package com.elinium.util.ui.recyclerview;

/**
 * Created by amiri on 9/9/2017.
 */

public interface IItemObserver {

    void onItemChanged(BaseItem item);
    void onItemChanged(BaseItem item, Object payload);

}