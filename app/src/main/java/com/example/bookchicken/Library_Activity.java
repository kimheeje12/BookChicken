package com.example.bookchicken;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Library_Activity extends AppCompatActivity {

    public static ArrayList<Library_Data> arrayDataList = new ArrayList<>();
    private Library_Adapter library_adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_library);

        //도서관 정보 검색
        Button search = (Button) findViewById(R.id.btn_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getResultSearch();
            }
        });
    }


    private String getResultSearch() {

        arrayDataList.clear(); // arrayDataList 초기화!(비워주기)

        EditText librarysearch = (EditText) findViewById(R.id.search);
        String booksearch = librarysearch.getText().toString();

        LibraryInterface apiInterface = LibraryApi.getInstance().create(LibraryInterface.class);
        Call<String> call = apiInterface.getSearchResult();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String result = response.body();
                    Log.d(TAG, "성공: " + result);
                    jsonParsing(result);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
        return booksearch;
    }

    //json 객체 파싱
    private void jsonParsing(String json) {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_library);
        recyclerView.setHasFixedSize(true);

        library_adapter = new Library_Adapter(arrayDataList);
        recyclerView.setAdapter(library_adapter);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray lbArr = jsonObject.getJSONArray("row");

            for (int i = 0; i<lbArr.length(); i++) {
                JSONObject lbObj = lbArr.getJSONObject(i);

                Library_Data library_data = new Library_Data();

                library_data.setLibraryname(lbObj.getString("LBRRY_NAME"));
                library_data.setcodevalue(lbObj.getString("CODE_VALUE"));
                library_data.setfdrm_close_date(lbObj.getString("FDRM_CLOSE_DATE"));
                library_data.settel_no(lbObj.getString("TEL_NO"));

                //link 정보도 담기(클릭 시 넘어갈 수 있도록)
//                library_data.setBooklink(bookObj.getString("link"));

                arrayDataList.add(library_data);
            }
            library_adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

