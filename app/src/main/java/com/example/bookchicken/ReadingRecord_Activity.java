package com.example.bookchicken;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ReadingRecord_Activity extends AppCompatActivity {

    public static ArrayList<ReadingRecord_Data> arrayDataList = new ArrayList<>();
    private ReadingRecord_Adapter readingRecordAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager linearLayoutManager;

    ActivityResultLauncher<Intent> resultLauncher;
    ActivityResultLauncher<Intent> updateLauncher;

    private AdView mAdview; //애드뷰 변수 선언(구글 애드몹)

    @Override
    protected void onStart() {
        super.onStart();

        arrayDataList = readbookrecorddata("ReadingRecord");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_readingrecord);
        recyclerView.setHasFixedSize(true);

        readingRecordAdapter = new ReadingRecord_Adapter(arrayDataList);

        recyclerView.setAdapter(readingRecordAdapter);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        readingRecordAdapter.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ReadingRecord_Activity.this, ReadingRecordEdit.class);
                intent.putExtra("position", position);
                updateLauncher.launch(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                new AlertDialog.Builder(ReadingRecord_Activity.this) // TestActivity 부분에는 현재 Activity의 이름 입력.
                        .setMessage("기록을 삭제하시겠습니까?")     // 제목 부분 (직접 작성)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {      // 버튼1 (직접 작성)
                            public void onClick(DialogInterface dialog, int which) {
                                readingRecordAdapter.remove(position);
                                readingRecordAdapter.notifyDataSetChanged();
                                savebookrecorddata("ReadingRecord", arrayDataList);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {     // 버튼2 (직접 작성)
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });

        updateLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 9) {

                            arrayDataList = readbookrecorddata("ReadingRecord");
                            readingRecordAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_readingrecord);

        //구글 애드몹
        MobileAds.initialize(this, new OnInitializationCompleteListener() {  // 광고 초기화
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdview = findViewById(R.id.adView); // 배너광고  레이아웃 가져오기
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);  //광고사이즈는 배너사이즈로 설정

    }

    private void savebookrecorddata(String key, ArrayList<ReadingRecord_Data> arrayDataList){
        SharedPreferences sf = getSharedPreferences("ReadingRecord", MODE_PRIVATE);
        Type listType = new TypeToken<ArrayList<ReadingRecord_Data>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(arrayDataList, listType);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("ReadingRecord", json);
        editor.apply();
    }

    private ArrayList<ReadingRecord_Data> readbookrecorddata(String key){
        SharedPreferences sharedPreferences = getSharedPreferences("ReadingRecord", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("ReadingRecord",null);
        Type listType = new TypeToken<ArrayList<ReadingRecord_Data>>(){}.getType();
        arrayDataList = gson.fromJson(json, listType);
        return arrayDataList;
    }
}