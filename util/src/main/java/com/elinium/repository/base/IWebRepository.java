package com.elinium.repository.base;

import java.util.Map;

/**
 * Created by amiri on 9/11/2017.
 */

public interface IWebRepository<T, KEY_TYPE> extends IRepository<T, KEY_TYPE> {
     Map<KEY_TYPE, Long> getWebTimeStampsSynchronized(T... instances);
}
