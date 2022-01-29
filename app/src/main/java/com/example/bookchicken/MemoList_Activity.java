package com.example.bookchicken;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MemoList_Activity extends AppCompatActivity{

    // ArrayList란? 여러 데이터를 하나의 이름으로 그룹핑해서 관리하기 위한 자료구조, index(값에 대한 유일무이한 식별자=주민번호)와 값의 쌍으로 구성
    // 배열 선언 시 배열의 인덱스를 다 채우거나 못 채울 수 있는데 만약 다 채웠다면 더 이상 배열에다가 값을 넣지 못하고 다 못 채웠다면 그만큼 메모리가 낭비된다.
    // 배열의 크기를 임의로 변화시킬 수 있고 List에 들어갈 수 있는 타입들을 사용자가 직접 설정할 수 있다.(인덱스를 통한 검색 가능, 연속적이므로 메모리 관리 편리)
    // 일반적으로 기본형 타입도 가능하고 자신이 만든 클래스의 객체타입도 설정할 수 있다.
    // 선언방식: ArrayList<test> myList = new ArrayList<test>(); -> test클래스의 객체를 List에 타입으로 설정하겠다는 뜻!
    // 변수 선언? 어떤 수, 문자 혹은 위치정보 등 무언가를 담을 수 있는 저장공간을 만들겠다는 의미
    public static ArrayList<MemoList_Data> arrayDataList = new ArrayList<>();
    private MemoList_Adapter memoListAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager mGridLayoutManager;


    ActivityResultLauncher<Intent> resultLauncher;
    ActivityResultLauncher<Intent> updateLauncher;

    @Override
    protected void onStart() {
        super.onStart();

        arrayDataList = readmemodata("memolist");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_memolist);
        recyclerView.setHasFixedSize(true);
        //setHasFixedSize? Adapter Item View의 내용이 변경되어도 '리사이클러뷰의 크기는 고정하겠다'는 의미
        //아이템 항목을 추가할 때마다 리사이클러뷰의 크기는 변경되고 레이아웃을 그릴 때 크기를 측정하고 다시 그리는 것을 반복할 것이다.
        //setHasFixedSize를 true로 설정함으로써 변경되지 않는다는 것을 명시하는 게 좋다.

        //어댑터(데이터와 리스트 뷰 사이의 통신을 위한 다리) 연결
        memoListAdapter = new MemoList_Adapter(arrayDataList);

        recyclerView.setAdapter(memoListAdapter);

        //뷰(안드로이드 기본화면을 구성하는 모든 기본 화면의 요소)의 구성을 관리하고 레이아웃 형태를 결정
        //linearLayoutManager: 리사이클러뷰에서 가장 많이 쓰이는 레이아웃으로 수평, 수직 스크롤을 제공하는 리스트를 만들 수 있다.
        mGridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mGridLayoutManager);

        //인터페이스 listener를 만들어서 리사이클러뷰 내 클릭이벤트 재정의
        memoListAdapter.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

//                클릭이벤트 발생 시 그에 해당하는 정보를 번들에 넣고 memoview activity로 전환
//                MemoList_Data item = MemoList_Adapter.getItem(position);

                Intent intent = new Intent(MemoList_Activity.this, MemoEdit.class);
//                Bundle bundle = new Bundle();
//
//                bundle.putString("memodate", item.getMemodate());
//                bundle.putString("booktitle", item.getBooktitle());
//                bundle.putString("bookmemo", item.getBookmemo());

//                intent.putExtras(bundle);
                intent.putExtra("position",position);
                updateLauncher.launch(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                new AlertDialog.Builder(MemoList_Activity.this) // TestActivity 부분에는 현재 Activity의 이름 입력.
                        .setMessage("메모를 삭제하시겠습니까?")     // 제목 부분 (직접 작성)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {      // 버튼1 (직접 작성)
                            public void onClick(DialogInterface dialog, int which){
                                memoListAdapter.remove(position);
                                memoListAdapter.notifyDataSetChanged();
                                savememodata("memolist", arrayDataList);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {     // 버튼2 (직접 작성)
                            public void onClick(DialogInterface dialog, int which){
                            }
                        })
                        .show();
            }
        });

        //완료 버튼 누르면 새로운 액티비티로 이동 후 정보들이 callback할 때 어떻게 행동할 것인지 선언
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {

                            arrayDataList = readmemodata("memolist");
                            memoListAdapter.notifyDataSetChanged();

//                        Bitmap image;
//                        Intent intent = result.getData();
//                        Bundle bundle = intent.getExtras();
//                        Log.d("구현","제발 들어와라");

                            // 각 자료들 분류해서 얻어내기
//                        String memodate = intent.getStringExtra("memodate");
//                        String booktitle = intent.getStringExtra("booktitle");
//                        String bookmemo = intent.getStringExtra("bookmemo");
//                        byte[] byteArray = intent.getByteArrayExtra("image");

//                        if(bundle.getByteArray("image") == null){
//                            image = null;
//                        }else{
//                            image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
//                        }
//                        Log.d("이미지 확인2","이미지 check2" + image);
//                        getStringArrayPref(MemoList_Activity.this, "memolist");
//                        Log.d("JSON2","무슨 데이터?"+getStringArrayPref(MemoList_Activity.this, "memolist"));
//                        MemoList_Data item = new MemoList_Data(memodate,booktitle,bookmemo);
//                        arrayDataList.add(item);
//                        Log.d("JSON2","무슨 데이터?" + ReadMemoData("memolist"));

//                        ReadMemoData("memolist");
                            //아이템 변경(데이터가 업데이트 되었지만 위치는 변하지 않았을 때), 구조적 변경(아이템 간에 삽입, 삭제, 이동이 일어났을때)에 사용
                            //리스트 항목 뷰에 들어간 데이터가 바뀌었을 때 호출한다.
                            //만약 현재 화면에 보이는 리스트 항목 View 한 개의 데이터가 바뀌어 다른 데이터를 호출한다고 하면 해당 메서드를 호출하면 된다.
                            Log.d("clear", "들어왔어!?");
                        }
                    }
                });

        updateLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == 10) {
//                           Intent intent = result.getData();
//                           Bundle bundle = intent.getExtras();
//                           int position = intent.getIntExtra("position",1);
                            Log.d("편집1","편집 데이터1");

                            // 각 자료들 분류해서 얻어내기
//                            String memodate = intent.getStringExtra("memodate");
//                            String booktitle = intent.getStringExtra("booktitle");
//                            String bookmemo = intent.getStringExtra("bookmemo");
//                            String bookimage = intent.getStringExtra("bookimage");

//                            MemoList_Data item = new MemoList_Data(memodate, booktitle, bookimage, bookmemo);
//                            memoListAdapter.setItem(position, item);

                            arrayDataList = readmemodata("memolist");
                            memoListAdapter.notifyDataSetChanged();

                            //ArrayList에 작성날짜, 책 제목, 책 메모 넣기
                            //아이템 변경(데이터가 업데이트 되었지만 위치는 변하지 않았을 때), 구조적 변경(아이템 간에 삽입, 삭제, 이동이 일어났을때)에 사용
                            //리스트 항목 뷰에 들어간 데이터가 바뀌었을 때 호출한다.
                            //만약 현재 화면에 보이는 리스트 항목 View 한 개의 데이터가 바뀌어 다른 데이터를 호출한다고 하면 해당 메서드를 호출하면 된다.
                            Log.d("편집2","편집 데이터2");
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        savememodata("memolist", arrayDataList);
    }

    //Bundle이란? 여러가지 타입 값을 저장하는 MAP클래스이다. ex) string 값을 Bundle클래스에서 Mapping(대응, 변환)하는 것이다.
    //안드로이드에서는 Activity간 데이터를 주고 받을 때 Bundle 클래스를 사용하여 데이터를 전송한다.
    //Activity를 생성할 때는 Bundle savedInstanceState 객체를 가져와서, Activity를 중단할 떄는 savedInstanceState 메서드를 호출하여 임시적으로 저장한다.
    //Bundle은 아무거나 포장할 수 있는 상자를 의미하고 이 포장박스를 이용하여 이리저리 인텐트도 오고갈 수 있고, 다양한 데이터 통신에 이용할 수 있다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_memo_list);
        //setContentView? XML파일에서 레이아웃을 생성하고 이 레이아웃을 보여주기 위해 사용
        //XML의 내용을 파싱하여 뷰를 생성하고 뷰에 정의된 속성을 설정

        //메모 추가 버튼
        Button btn_add = (Button) findViewById(R.id.btn_memoadd);
        btn_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("메인 엑티비티", "인터페이스 메서드");
                Intent intent = new Intent(MemoList_Activity.this, MemoAdd.class);
                resultLauncher.launch(intent);
            }
        });

        //홈
        Button home = (Button) findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MemoList_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void savememodata(String key, ArrayList<MemoList_Data> arrayDataList){
        SharedPreferences sf = getSharedPreferences("memolist", MODE_PRIVATE);
        Type listType = new TypeToken<ArrayList<MemoList_Data>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(arrayDataList, listType);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("memolist", json);
        editor.apply();
    }

    private ArrayList<MemoList_Data> readmemodata(String key){
        SharedPreferences sharedPreferences = getSharedPreferences("memolist", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("memolist",null);
        Type listType = new TypeToken<ArrayList<MemoList_Data>>(){}.getType();
        arrayDataList = gson.fromJson(json, listType);
        return arrayDataList;
    }
}