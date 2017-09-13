package com.elinium.pattern.repository;

import android.util.LongSparseArray;

import java.util.List;
import java.util.Map;

/**
 * Created by amiri on 9/11/2017.
 */

public interface ELocalRepository<T, KEY_TYPE> extends ERepository<T, KEY_TYPE> {
    KEY_TYPE getId(T instance);

    List<KEY_TYPE> getIds(T... instances);

    List<T> query(KEY_TYPE key);

    Map<KEY_TYPE, Long> getLocalTimeStamps(T... instances);
}
