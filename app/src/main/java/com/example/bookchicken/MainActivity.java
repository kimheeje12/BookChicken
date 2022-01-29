package com.example.bookchicken;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long backKeyPressedTime = 0;

    //핸들러 & 쓰레드
    private TextView textview;
    private Handler handler = new Handler();

    String[] mArr = {
                "Stay Hungry, Stay Foolish",
                "측정할 수 없으면 개선할 수 없다",
                "이 또한 지나가리라",
                "Hope for the best, Plan for the worst",
                "가야할 때 가지 않으면 가려할 때는 갈 수 없다",
                "JUST DO IT.",
                "지나침은 모자람만 못하다",
                "Think different.",
        };

        //핸들러로 전달할 runnable 객체
        //Handler? Worker thread에서 main thread로 메시지를 전달해주는 역할
        //-> 메시지를 전달받아야 하는 (수신대상) 스레드에서 핸들러 객체를 생성한다 - 보통 main thread
        //Runnable이란? Thread의 인터페이스화 상태, 자바는 다중상속이 금지
        //Thread는 한 번 사용하면 재사용이 불가능하지만 Runnable을 이용하면 Thread 재사용가능
        //Thread? 프로세스 내에서 순차적으로 실행되는 실행흐름의 최소 단위

    int i = 0;

    Runnable runnable = new Runnable() {

        public void run() {
            //for(String mArray: mArr){
            //  Log.d("배열","배열 데이터"+mArray);
            //   textview.setText(mArray);

            textview.setText(mArr[i]); // 분명 간단한 식인데.. 이게 어려웠다.. 반성하자
            if(i<mArr.length-1){
                i++;
            }else{
                i=0;
            }
        }
    };

    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis() > backKeyPressedTime + 2000){
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(System.currentTimeMillis() <= backKeyPressedTime + 2000){
            onDestroy();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //java 다중상속이 불가능, Thread 상속받지 못할 경우 implements로 Runnable을 받아 구현
       class NewRunnable implements Runnable{
           public void run(){

               textview = (TextView) findViewById(R.id.phrase);

               while(true){
                  try{
                     Thread.sleep(3000); //3초

                   }catch (Exception e){
                      e.printStackTrace();
                   }
                   handler.post(runnable); //위에서 만든 Runnable 객체를 수신측 스레드로 보낸다
                }
           }
       }

       NewRunnable nr = new NewRunnable();
       Thread t = new Thread(nr);
       t.start();

        //책 검색 이동
        Button booksearch = (Button) findViewById(R.id.btn_booksearch);
        booksearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookSearch_Activity.class);
                startActivity(intent);
            }
        });

        //읽고 있는 책 이동
        Button readingbook = (Button) findViewById(R.id.btn_readingbook);
        readingbook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookList_Activity.class);
                startActivity(intent);
            }
        });

        //책 메모 이동
        Button bookmemo = (Button) findViewById(R.id.btn_bookmemo);
        bookmemo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MemoList_Activity.class);
                startActivity(intent);
            }
        });

        //완독 기록 이동
        Button calendar = (Button) findViewById(R.id.btn_ReadingRecord);
        calendar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReadingRecord_Activity.class);
                startActivity(intent);
            }
        });

        //yes24 베스트셀러로 이동
        Button library = (Button) findViewById(R.id.btn_library);
        library.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.yes24.com/24/Category/BestSeller"));
                startActivity(intent);
//                Intent intent = new Intent(MainActivity.this, Library_Activity.class);
//                startActivity(intent);
            }
        });

//        //도서관 정보 이동 / yes24 베스트셀러로 이동
//        Button api = (Button) findViewById(R.id.btn_api);
//        api.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, Library_Activity.class);
//                startActivity(intent);
//            }
//        });

    }
}