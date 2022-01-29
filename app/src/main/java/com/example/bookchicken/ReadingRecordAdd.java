package com.example.bookchicken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ReadingRecordAdd extends AppCompatActivity {

    private String uri_String;
    private String ratingbar_String;
    private Uri uri;

    public static final int REQUEST_PERMISSION = 11;

    private final int GALLERY_CODE = 12;

    private ImageView imageView;
    private String img_Path = ""; // 절대경로를 저정할 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_record_add);

        EditText booktitle = (EditText) findViewById(R.id.booktitle);
        EditText author = (EditText) findViewById(R.id.author);
        EditText bookrecord = (EditText) findViewById(R.id.bookreord);
        imageView = findViewById(R.id.bookimage);

        //BookEdit(읽고 있는 책)로부터 날아온 데이터
        Intent intent = getIntent();


        booktitle.setText(intent.getStringExtra("booktitle")); //책 제목
        author.setText(intent.getStringExtra("author")); // 작가

        try{
            Uri img_uri = Uri.parse(intent.getStringExtra("bookimage")); //책 이미지
            Glide.with(this).load(img_uri).into(imageView);
            uri = img_uri;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingBar.setRating(v);
                ratingbar_String = String.valueOf(ratingBar);
            }
        });

        //완료 버튼
        Button btn_add = (Button) findViewById(R.id.btn_complete);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadingRecordAdd.this, ReadingRecord_Activity.class);
                ReadingRecord_Activity.arrayDataList.add(new ReadingRecord_Data(
                        uri.toString(),
                        booktitle.getText().toString(),
                        author.getText().toString(),
                        ratingBar.getRating(),
                        bookrecord.getText().toString()));
                savebookrecorddata("ReadingRecord", ReadingRecord_Activity.arrayDataList);
                finish();
            }
        });

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
}