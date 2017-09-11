package com.elinium.pattern.repository.retrofit;

import com.elinium.pattern.repository.room.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by amiri on 9/10/2017.
 */

public interface ServerApi {
    @POST("accounts/")
    Observable<User> signIn(@Body String id, @Body String pass);

    @POST("users/")
    Observable<List<User>> getFriends(@Body String userId);
}
