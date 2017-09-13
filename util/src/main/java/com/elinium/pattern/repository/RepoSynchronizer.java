package com.elinium.pattern.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amiri on 9/12/2017.
 */

public class RepoSynchronizer {

    static List<RepositorySynchronizer> synchronizers = new ArrayList<>();

    public static void init(List<Class<? extends ERepository>> repositories) {
        for (Class repository : repositories) {
            try {
                synchronizers.add(new RepositorySynchronizer((ELocalRepository) repository.newInstance(), (EWebRepository) repository.newInstance()));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
