package com.elinium.pattern.repository;

import android.util.LongSparseArray;

import java.util.Map;

/**
 * Created by amiri on 9/11/2017.
 */

public interface EWebRepository<T, KEY_TYPE> extends ERepository<T, KEY_TYPE> {
     Map<KEY_TYPE, Long> getWebTimeStampsSynchronized(T... instances);
}
