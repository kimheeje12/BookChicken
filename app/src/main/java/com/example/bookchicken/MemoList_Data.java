package com.example.bookchicken;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;

public class MemoList_Data implements Serializable {
    private String memodate;
    private String booktitle;
    private String bookmemo;
    private String bookimage;

    // 생성자? 객체가 객체화될 때 호출되며 해당 클래스의 객체를 초기화한다.
    // 클래스 객체명 = new 클래스명(); -> 클래스명() 부분이 생성자다.
    // 생성자에 값을 넣는다? 클래스의 객체를 만들 떄, 내가 입력해준 값을 갖는 객체를 만들겠다는 뜻
    public MemoList_Data(String memodate, String booktitle, String bookimage, String bookmemo){
        this.memodate = memodate;
        this.booktitle = booktitle;
        this.bookimage = bookimage;
        this.bookmemo = bookmemo;
    }

    public String getBookimage(){return bookimage;}

    public String getBooktitle(){return booktitle;}

    public String getMemodate(){return memodate;}

    public String getBookmemo(){return bookmemo;}

}