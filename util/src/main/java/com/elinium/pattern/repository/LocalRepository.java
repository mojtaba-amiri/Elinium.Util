package com.elinium.pattern.repository;

import android.util.LongSparseArray;

import java.util.List;
import java.util.Map;

/**
 * Created by amiri on 9/11/2017.
 */

public interface LocalRepository<T,KEY_TYPE> extends ERepository {
    KEY_TYPE getId(T instance);
    List<KEY_TYPE> getIds(T... instances);
    List<T> query(KEY_TYPE key);
    Map<KEY_TYPE, Long> getTimeStamps(T... instances);
}
