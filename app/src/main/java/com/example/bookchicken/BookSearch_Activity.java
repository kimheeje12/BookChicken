package com.example.bookchicken;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookSearch_Activity extends AppCompatActivity {

    //데이터를 생성하기 위해서
    public static ArrayList<BookSearch_Data> arrayDataList = new ArrayList<>();
    private BookSearch_Adapter bookSearchAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager linearLayoutManager;

    private final String TAG = this.getClass().getSimpleName();

    private String client_id = "3X4s9I8hBJKZaZljSwK3";
    private String client_pw = "cJV0U7d2iZ";

    //핸들러 & 쓰레드
    private ImageView imageView;
    private Handler handler = new Handler();

    private ArrayList<Drawable> mList = new ArrayList<>();

    int i = 0;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //어린왕자
            mList.add(getDrawable(R.drawable.littleprincess));
            mList.add(getDrawable(R.drawable.littleprincess2));
            mList.add(getDrawable(R.drawable.littleprincess3));
            mList.add(getDrawable(R.drawable.littleprincess4));
            mList.add(getDrawable(R.drawable.littleprincess5));

                Drawable drawable = mList.get(i % mList.size());
                imageView.setImageDrawable(drawable);
                i++;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_book_search);

//        //구글 애드몹
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {  // 광고 초기화
//            @Override
//            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
//            }
//        });
//
//        mAdview = findViewById(R.id.adView); // 배너광고  레이아웃 가져오기
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdview.loadAd(adRequest);
//        AdView adView = new AdView(this);
//        adView.setAdSize(AdSize.BANNER);  //광고사이즈는 배너사이즈로 설정

        //java 다중상속이 불가능, Thread 상속받지 못할 경우 implements로 Runnable을 받아 구현
        class NewRunnable implements Runnable{
            public void run(){

                imageView = findViewById(R.id.littleprincess);

                while(true){
                    try{
                        Thread.sleep(3000); //3초
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    handler.post(runnable);
                }
            }
        }

        NewRunnable nr = new NewRunnable();
        Thread t = new Thread(nr);
        t.start();

        //책 검색 버튼(해당 도서 리사이클러뷰로 나타내기)
        Button search = (Button) findViewById(R.id.btn_search);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getResultSearch();
            }
        });
    }

    // 레트로핏을 통한 결과값 얻어오기
    private String getResultSearch(){

        arrayDataList.clear(); // arrayDataList 초기화!(비워주기)

        EditText search = (EditText) findViewById(R.id.search);
        String booksearch = search.getText().toString(); // 4번째 인자에 EditText 값 String으로 만들기

        ApiInterface apiInterface = ApiClient.getInstance().create(ApiInterface.class);
        Call<String> call = apiInterface.getSearchResult(client_id, client_pw,"book.json", booksearch);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String result = response.body();
                    Log.d(TAG, "성공: " + result);

                    jsonParsing(result);

                    //아이템 클릭 시 해당 아이템으로 이동!
                    try{
                        bookSearchAdapter.setOnItemClickListener(new MyItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arrayDataList.get(position).getBooklink()));
                                startActivity(intent);
                            }
                            @Override
                            public void onItemLongClick(View view, int position) {
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
        return booksearch;
    }

    //json 객체 파싱
    private void jsonParsing(String json){

        recyclerView = (RecyclerView) findViewById(R.id.recycler_booksearch);
        recyclerView.setHasFixedSize(true);

        bookSearchAdapter = new BookSearch_Adapter(arrayDataList);
        recyclerView.setAdapter(bookSearchAdapter);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        try{
//            String jsonfile = getResultSearch();
//            Gson gson = new Gson();
//            BookSearch_Data bookSearchData = gson.fromJson(jsonfile,BookSearch_Data.class);

          JSONObject jsonObject = new JSONObject(json);
          JSONArray bookArr = jsonObject.getJSONArray("items");

//            JsonParser parser = new JsonParser();
//            JsonElement obj = parser.parse(getResultSearch());
//
//            JsonObject jsonObject = obj.getAsJsonObject();
//            JSONArray bookArr =  jsonObject.get("items").getAsJsonArray();

            for(int i=0; i<bookArr.length(); i++){
                JSONObject bookObj = bookArr.getJSONObject(i);

                BookSearch_Data booksearch_data = new BookSearch_Data();

                booksearch_data.setBookImage(bookObj.getString("image"));
                booksearch_data.setAuthor(bookObj.getString("author"));
                booksearch_data.setPublisher(bookObj.getString("publisher"));
                booksearch_data.setBookprice(bookObj.getString("price") + "원");

                //link 정보도 담기(클릭 시 넘어갈 수 있도록)
                booksearch_data.setBooklink(bookObj.getString("link"));

                arrayDataList.add(booksearch_data);
            }
            bookSearchAdapter.notifyDataSetChanged();

        }catch(JSONException e){
            e.printStackTrace();
        }
    }

}