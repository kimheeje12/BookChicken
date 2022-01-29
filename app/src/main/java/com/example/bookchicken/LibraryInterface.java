package com.example.bookchicken;

import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LibraryInterface {

    @GET("757978474a6b696d37385a52744952/json/SeoulLibraryTimeInfo/1/1000")
    Call<String> getSearchResult(
    );
}
