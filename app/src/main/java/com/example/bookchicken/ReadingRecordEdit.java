package com.example.bookchicken;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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

public class ReadingRecordEdit extends AppCompatActivity {

    private String uri_String;
    private String ratingbar_String;
    private Uri uri;

    public static final int REQUEST_PERMISSION = 13;

    private final int GALLERY_CODE = 12;

    private ImageView imageView;
    private String img_Path = ""; // 절대경로를 저정할 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_record_add);

        //기존 데이터 불러오기
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", -1);

        EditText booktitle = (EditText) findViewById(R.id.booktitle);
        EditText author = (EditText) findViewById(R.id.author);
        EditText bookrecord = (EditText) findViewById(R.id.bookreord);
        ImageView bookimage = (ImageView) findViewById(R.id.bookimage);

        //기존 데이터 셋팅(책 제목, 작가, 서평)
        booktitle.setText(ReadingRecord_Activity.arrayDataList.get(position).getBooktitle());
        author.setText(ReadingRecord_Activity.arrayDataList.get(position).getAuthor());
        bookrecord.setText(ReadingRecord_Activity.arrayDataList.get(position).getBookrecord());

        //URI 경로에 따른 이미지 파일을 로드한다
        try{
            Uri img_uri = Uri.parse(ReadingRecord_Activity.arrayDataList.get(position).getBookimage());
            Glide.with(this).load(img_uri).into(bookimage);
            uri = img_uri;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //ratingbar 셋팅
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setRating(ReadingRecord_Activity.arrayDataList.get(position).getRatingBar());
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
                Intent intent = new Intent(ReadingRecordEdit.this, ReadingRecord_Activity.class);
                ReadingRecord_Activity.arrayDataList.set(position, new ReadingRecord_Data(
                        uri.toString(),
                        booktitle.getText().toString(),
                        author.getText().toString(),
                        ratingBar.getRating(),
                        bookrecord.getText().toString()));
                savebookrecorddata("ReadingRecord", ReadingRecord_Activity.arrayDataList);
                setResult(9, intent);
                finish();
            }
        });

        //이미지 추가/수정
        imageView = findViewById(R.id.bookimage);
        Button btn_imageadd = (Button) findViewById(R.id.btn_imageadd);
        btn_imageadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, GALLERY_CODE);
            }
        });
    }

    //선택된 이미지를 imageView에 세팅하기
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE) {
            if (resultCode == RESULT_OK) {

                    uri = data.getData();

                    imageView.setImageURI(uri);

                    uri_String = uri.toString();
                    //절대 경로 얻을 수 있는 메서드 사용
                    img_Path = createCopyAndReturnRealPath(this, uri);

            }
        }
    }

    //절대경로 파악할 때 사용된 메서드
    public static String createCopyAndReturnRealPath(Context context, Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver == null)
            return null;
        //파일 경로 만듦
        String filePath = context.getApplicationInfo().dataDir + File.separator
                + System.currentTimeMillis();

        File file = new File(filePath);
        try {
            //매개변수로 받은 uri를 통해 이미지에 필요한 데이터를 불러들인다
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;
            //이미지 데이터를 다시 내보내면서 file객체에 만들었던 경로를 이용한다
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);
            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }
        return file.getAbsolutePath();
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

    //권한 확인
    public void checkPermission() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //권한이 없으면 권한 요청
        if (permissionCamera != PackageManager.PERMISSION_GRANTED
                || permissionRead != PackageManager.PERMISSION_GRANTED
                || permissionWrite != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "이 앱을 실행하기 위해 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
    }
}