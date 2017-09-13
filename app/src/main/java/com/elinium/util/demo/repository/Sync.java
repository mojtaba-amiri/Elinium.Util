package com.elinium.util.demo.repository;

import com.elinium.pattern.repository.ELocalRepository;
import com.elinium.pattern.repository.EWebRepository;
import com.elinium.pattern.repository.RepositorySynchronizer;
import com.elinium.util.demo.model.User;

/**
 * Created by amiri on 9/12/2017.
 */

public class Sync extends RepositorySynchronizer<User, Long> {

    public Sync(ELocalRepository<User, Long> ELocalRepository, EWebRepository<User, Long> EWebRepository) {
        super(ELocalRepository, EWebRepository);
    }
}
