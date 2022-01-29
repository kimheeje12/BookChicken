package com.example.bookchicken;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BookList_Activity extends AppCompatActivity {

    public static ArrayList<BookList_Data> arrayDataList = new ArrayList<>();
    private BookList_Adapter bookListAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager mGridLayoutManager;

    ActivityResultLauncher<Intent> resultLauncher;
    ActivityResultLauncher<Intent> updateLauncher;

    @Override
    protected void onStart() {
        super.onStart();

        arrayDataList = readbookdata("booklist");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_booklist);
        recyclerView.setHasFixedSize(true);
        //setHasFixedSize? Adapter Item View의 내용이 변경되어도 '리사이클러뷰의 크기는 고정하겠다'는 의미
        //아이템 항목을 추가할 때마다 리사이클러뷰의 크기는 변경되고 레이아웃을 그릴 때 크기를 측정하고 다시 그리는 것을 반복할 것이다.
        //setHasFixedSize를 true로 설정함으로써 변경되지 않는다는 것을 명시하는 게 좋다.

        //어댑터(데이터와 리스트 뷰 사이의 통신을 위한 다리) 연결
        bookListAdapter = new BookList_Adapter(arrayDataList);
        recyclerView.setAdapter(bookListAdapter);

        //뷰(안드로이드 기본화면을 구성하는 모든 기본 화면의 요소)의 구성을 관리하고 레이아웃 형태를 결정
        //linearLayoutManager: 리사이클러뷰에서 가장 많이 쓰이는 레이아웃으로 수평, 수직 스크롤을 제공하는 리스트를 만들 수 있다.

        mGridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mGridLayoutManager);


        //인터페이스 listener를 만들어서 리사이클러뷰 내 클릭이벤트 재정의
        bookListAdapter.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(BookList_Activity.this, BookEdit.class);
                intent.putExtra("position",position);
                updateLauncher.launch(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                new AlertDialog.Builder(BookList_Activity.this) // TestActivity 부분에는 현재 Activity의 이름 입력.
                        .setMessage("읽은 도서를 삭제하시겠습니까?")     // 제목 부분 (직접 작성)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {      // 버튼1 (직접 작성)
                            public void onClick(DialogInterface dialog, int which){
                                bookListAdapter.remove(position);
                                bookListAdapter.notifyDataSetChanged();
                                savebookdata("booklist",arrayDataList);
                                //변경된 데이터의 셋팅을 통보, 저장한다!!
                                //어댑터로 리스트와 리스트뷰를 연결하고 나서 마지막에는 꼭 써줘야한다!
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {     // 버튼2 (직접 작성)
                            public void onClick(DialogInterface dialog, int which){
                            }
                        })
                        .show();
            }
        });

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {

                            arrayDataList = readbookdata("booklist");
                            bookListAdapter.notifyDataSetChanged();

                            //아이템 변경(데이터가 업데이트 되었지만 위치는 변하지 않았을 때), 구조적 변경(아이템 간에 삽입, 삭제, 이동이 일어났을때)에 사용
                            //리스트 항목 뷰에 들어간 데이터가 바뀌었을 때 호출한다.
                            //만약 현재 화면에 보이는 리스트 항목 View 한 개의 데이터가 바뀌어 다른 데이터를 호출한다고 하면 해당 메서드를 호출하면 된다.
                        }
                    }
                });

        updateLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 10) {
                            arrayDataList = readbookdata("booklist");
                            bookListAdapter.notifyDataSetChanged();
                            //아이템 변경(데이터가 업데이트 되었지만 위치는 변하지 않았을 때), 구조적 변경(아이템 간에 삽입, 삭제, 이동이 일어났을때)에 사용
                            //리스트 항목 뷰에 들어간 데이터가 바뀌었을 때 호출한다.
                            //만약 현재 화면에 보이는 리스트 항목 View 한 개의 데이터가 바뀌어 다른 데이터를 호출한다고 하면 해당 메서드를 호출하면 된다.
                        }if(result.getResultCode() == 11){
                            Intent intent = result.getData();
                            int position = intent.getIntExtra("position", -1);
                            Log.d(TAG, "받는 값? " + position);
                            bookListAdapter.remove(position);
                            bookListAdapter.notifyDataSetChanged();
                            savebookdata("booklist",arrayDataList);

                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_book_list);

        //책 추가 버튼
        Button btn_add = (Button) findViewById(R.id.btn_bookadd);
        btn_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("메인 엑티비티", "인터페이스 메서드");
                Intent intent = new Intent(BookList_Activity.this, BookAdd.class);
                resultLauncher.launch(intent);
            }
        });

        //홈
        Button home = (Button) findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(BookList_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // sharedpreference 저장 메서드!
    private void savebookdata(String key, ArrayList<BookList_Data> arrayDataList) {
        SharedPreferences sf = getSharedPreferences("booklist", MODE_PRIVATE);
        Type listType = new TypeToken<ArrayList<BookList_Data>>() {}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(arrayDataList, listType);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("booklist", json);
        editor.apply();
    }

    private ArrayList<BookList_Data> readbookdata(String key){
        SharedPreferences sharedPreferences = getSharedPreferences("booklist", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("booklist",null);
        Type listType = new TypeToken<ArrayList<BookList_Data>>(){}.getType();
        arrayDataList = gson.fromJson(json, listType);
        return arrayDataList;
    }
}