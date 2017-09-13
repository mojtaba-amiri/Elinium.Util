package com.elinium.pattern.repository;

import java.util.List;

/**
 * Created by amiri on 9/10/2017.
 * In this repository pattern you should be able to :
 * - Make databases based on any type of ORM (GreenDao, Room, Realm...)
 * - Sync any data object with server
 * - Do save and retrieve mechanism in minimum number of code
 * - Easily understand the code
 * - Use retrofit calls to make syncs and data loadings
 * <p>
 * Always check to see if data is synced or not.
 * If Data did not exist in local or not synced (due to a failed service call), then make a call to sync it
 * and return the result instead.
 * If Data
 */

public interface ERepository<T, KEY_TYPE> {

    Class<T> getEntityClass();

    Class<KEY_TYPE> getKeyType();

    //region CREATE
    int create(T... instances);
    //endregion

    //region READ
    T read(T instance);

    T read(KEY_TYPE... instance);

    T readSynchronized(KEY_TYPE... instance);
    //endregion

    //region UPDATE
    int update(T... instances);
    //endregion

    //region DELETE
    int delete(T... instances);

    int deleteByKey(KEY_TYPE... keys);
    //endregion

    RepositorySynchronizer<T,KEY_TYPE> getSynchronizer();

}
