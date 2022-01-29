package com.example.bookchicken;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("search/{type}")
    Call<String> getSearchResult(
            @Header("X-Naver-Client-Id") String id,
            @Header("X-Naver-Client-Secret") String pw,
            @Path("type") String type,
            @Query("query") String query
    );
}
