package com.elinium.pattern.repository;

import android.content.Context;

/**
 * Created by amiri on 9/10/2017.
 */

public interface IDataController {
    /// Generally we need appContext to create an instance of Database
    void initializeDatabase(Context appContext);

    /// Based on retrofit requirements ///
    void initializeWebClient(String serverBaseUrl, Class<? extends Object> webCallsClass);
}
