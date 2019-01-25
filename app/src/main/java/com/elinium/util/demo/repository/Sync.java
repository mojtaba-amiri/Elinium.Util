package com.elinium.util.demo.repository;

import com.elinium.repository.base.ILocalRepository;
import com.elinium.repository.base.IWebRepository;
import com.elinium.repository.synchronization.RepositorySynchronizer;
import com.elinium.util.demo.model.User;

/**
 * Created by amiri on 9/12/2017.
 */

public class Sync extends RepositorySynchronizer<User, Long> {

    public Sync(ILocalRepository<User, Long> ILocalRepository, IWebRepository<User, Long> EWebRepository) {
        super(ILocalRepository, EWebRepository);
    }
}
