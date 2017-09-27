package com.elinium.util.demo.repository.web.endpoints;

import com.elinium.util.demo.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by amiri on 9/16/2017.
 */

public interface Accounts {
    @POST("accounts/")
    Call<User> signIn(@Body String id, @Body String pass);


}
