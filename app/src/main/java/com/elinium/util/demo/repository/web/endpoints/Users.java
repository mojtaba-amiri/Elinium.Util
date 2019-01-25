package com.elinium.util.demo.repository.web.endpoints;

import com.elinium.util.demo.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by amiri on 9/10/2017.
 */

public interface Users {
    @POST("users/")
    Call<List<User>> getFriends(@Body String userId);
}
