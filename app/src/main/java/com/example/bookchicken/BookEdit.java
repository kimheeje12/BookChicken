package com.example.bookchicken;

import static android.content.ContentValues.TAG;

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

public class BookEdit extends AppCompatActivity {

    public static final int REQUEST_PERMISSION = 11;

    private final int GALLERY_CODE = 12;
    private ImageView imageView;
    private String uri_String;
    private String img_Path = ""; // 절대경로를 저정할 변수 선언
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_add);

        EditText booktitle = (EditText) findViewById(R.id.booktitle);
        EditText author = (EditText) findViewById(R.id.author);
        EditText publisher = (EditText) findViewById(R.id.publisher);
        EditText bookpage = (EditText) findViewById(R.id.bookpage);
        ImageView bookimage = (ImageView) findViewById(R.id.bookimage);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", -1);

        booktitle.setText(BookList_Activity.arrayDataList.get(position).getBooktitle());
        author.setText(BookList_Activity.arrayDataList.get(position).getAuthor());
        publisher.setText(BookList_Activity.arrayDataList.get(position).getPublisher());
        bookpage.setText(BookList_Activity.arrayDataList.get(position).getBookpage());

        //URI 경로에 따른 이미지 파일을 로드한다
        try{
            Uri img_uri = Uri.parse(BookList_Activity.arrayDataList.get(position).getBookimage());
            Glide.with(this).load(img_uri).into(bookimage);
            uri = img_uri;
            uri_String=BookList_Activity.arrayDataList.get(position).getBookimage();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        savebookdata("booklist",BookList_Activity.arrayDataList);
        //완료 버튼
        Button btn_add = (Button) findViewById(R.id.btn_complete);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookEdit.this, BookList_Activity.class);
                BookList_Activity.arrayDataList.set(position, new BookList_Data(
                        uri_String,
                        booktitle.getText().toString(),
                        author.getText().toString(),
                        publisher.getText().toString(),
                        bookpage.getText().toString()));
                savebookdata("booklist", BookList_Activity.arrayDataList);
                setResult(10, intent);
                finish();
            }
        });

        //완독 버튼(완독 리포트로 이동) + 읽고 있는 책에서 삭제!
        Button btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookEdit.this,ReadingRecordAdd.class);
                intent.putExtra("bookimage",BookList_Activity.arrayDataList.get(position).getBookimage());
                intent.putExtra("booktitle",booktitle.getText().toString());
                intent.putExtra("author",author.getText().toString());
                startActivity(intent);

                Intent intent2 = new Intent();
                intent2.putExtra("position",position);
                setResult(11, intent2);
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
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                }
                if (uri != null) {
                    imageView.setImageURI(uri);

                    uri_String = uri.toString();

                    //절대 경로 얻을 수 있는 메서드 사용
                    img_Path = createCopyAndReturnRealPath(this, uri);
                }
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

    //사진 회전값 가져오기
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    //사진 정방향대로 회전하기
    private Bitmap rotate(Bitmap src, float degree) {
        Matrix matrix = new Matrix(); //Maxtrix 객체 생성
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    //사진 절대경로 구하기(Uri로부터 실제경로를 가져온다), contentUri: URI 경로, return String: 실제 파일 경로
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String uri = cursor.getString(columnIndex);
        cursor.close();
        return uri;
    }

    // sharedpreference 저장 메서드!
    private void savebookdata(String key, ArrayList<BookList_Data> arrayDataList) {
        SharedPreferences sf = getSharedPreferences("booklist", MODE_PRIVATE);
        Type listType = new TypeToken<ArrayList<BookList_Data>>(){}.getType();
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
        BookList_Activity.arrayDataList = gson.fromJson(json, listType);
        return BookList_Activity.arrayDataList;
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