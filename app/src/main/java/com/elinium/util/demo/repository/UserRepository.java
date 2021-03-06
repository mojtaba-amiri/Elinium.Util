package com.elinium.util.demo.repository;

import com.elinium.repository.base.ILocalRepository;
import com.elinium.repository.base.IWebRepository;
import com.elinium.repository.synchronization.RepositorySynchronizer;
import com.elinium.util.demo.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by amiri on 9/12/2017.
 */

public class UserRepository implements ILocalRepository<User, Long>, IWebRepository<User, Long> {

    @Override
    public Long getId(User instance) {
        return Long.valueOf(instance.getUid());
    }

    @Override
    public List<Long> getIds(User... usrs) {
        List<Long> ids = new ArrayList<>();
        for (User user : usrs) {
            ids.add(Long.valueOf(user.getUid()));
        }
        return ids;
    }

    @Override
    public List<User> query(Long key) {
        return null;
    }

    @Override
    public Map<Long, Long> getLocalTimeStamps(User... instances) {
        return null;
    }

    @Override
    public Map<Long, Long> getWebTimeStampsSynchronized(User... instances) {
        return null;
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public Class<Long> getKeyType() {
        return Long.class;
    }

    @Override
    public int create(User... instances) {
        return 0;
    }

    @Override
    public User read(User instance) {
        return null;
    }

    @Override
    public User read(Long... ids) {
        return null;
    }

    @Override
    public User readSynchronized(Long... ids) {
        return null;
    }

    @Override
    public int update(User... instances) {
        return 0;
    }

    @Override
    public int delete(User... instances) {
        return 0;
    }

    @Override
    public int deleteByKey(Long... keys) {
        return 0;
    }

//    @Override
//    public RepositorySynchronizer<User, Long> getSynchronizer() {
//        return null;
//    }

}
