package com.elinium.pattern.repository;

import android.util.LongSparseArray;

import java.util.Map;

/**
 * Created by amiri on 9/11/2017.
 */

public interface WebRepository<T, KEY_TYPE> extends ERepository {
    Map<KEY_TYPE, Long> getTimeStamps(T... instances);
}
