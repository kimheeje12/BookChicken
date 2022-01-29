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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class MemoEdit extends AppCompatActivity {

    public static final int REQUEST_PERMISSION = 14;

    private final int GALLERY_CODE = 12;

    private String uri_String;

    private String img_Path = ""; // 절대경로를 저정할 변수 선언
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_add);

        EditText memodate = (EditText) findViewById(R.id.memodate);
        EditText booktitle = (EditText) findViewById(R.id.booktitle);
        EditText bookmemo = (EditText) findViewById(R.id.bookmemo);
        ImageView bookimage = (ImageView) findViewById(R.id.bookimage);

        //기존에 저장되있던 데이터 불러오기!(날짜, 책 제목, 이미지, MEMO)
        Intent intent = getIntent();
//        Bundle bundle = getIntent().getExtras();

//        memodate.setText(bundle.getString("memodate"));
//        booktitle.setText(bundle.getString("booktitle"));
//        bookmemo.setText(bundle.getString("bookmemo"));
        int position = intent.getIntExtra("position", -1);

//      memodate.setText(MemoList_Activity.arrayDataList.indexOf(0));
        memodate.setText(MemoList_Activity.arrayDataList.get(position).getMemodate());
        booktitle.setText(MemoList_Activity.arrayDataList.get(position).getBooktitle());
        bookmemo.setText(MemoList_Activity.arrayDataList.get(position).getBookmemo());

        //URI 경로에 따른 이미지 파일을 로드한다
        try{
            Uri img_uri = Uri.parse(MemoList_Activity.arrayDataList.get(position).getBookimage());
//          imageView.setImageURI(uri);
            Glide.with(this).load(img_uri).into(bookimage);

            uri_String=MemoList_Activity.arrayDataList.get(position).getBookimage();

        }
        catch (Exception e){
            e.printStackTrace();
        }

        Button btn_complete = (Button) findViewById(R.id.btn_complete);
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemoEdit.this, MemoList_Activity.class);
                MemoList_Activity.arrayDataList.set(position, new MemoList_Data(
                        memodate.getText().toString(),
                        booktitle.getText().toString(),
                        uri_String,
                        bookmemo.getText().toString()));
                savememodata("memolist", MemoList_Activity.arrayDataList);
                setResult(10, intent);
                finish();
            }
        });

        imageView = findViewById(R.id.bookimage);
        Button btn_picture = (Button) findViewById(R.id.btn_imageadd);
        btn_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int permissionCheck = ContextCompat.checkSelfPermission(MemoAdd.this, Manifest.permission.CAMERA);
//                if(permissionCheck == PackageManager.PERMISSION_DENIED){
//                    ActivityCompat.requestPermissions(MemoAdd.this, new String[]{Manifest.permission.CAMERA},0);
                checkPermission();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image/*");
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
//            img_Path = getRealPathFromURI(data.getData());
//            Log.d(TAG, "아무거나:" + img_Path);
//            Glide.with(getApplicationContext()).load(img_Path).into(imageView);
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

    private void sendPicture(Uri imgUri) {
        String imagePath = getRealPathFromURI(imgUri);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath); //경로를 통해 비트맵으로 전환
        ImageView imageview = (ImageView) findViewById(R.id.bookimage);
        imageview.setImageBitmap(rotate(bitmap, exifDegree)); //이미지 뷰에 비트맵 넣기
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
    private void savememodata(String key, ArrayList<MemoList_Data> arrayDataList) {
        SharedPreferences sf = getSharedPreferences("memolist", MODE_PRIVATE);
        Type listType = new TypeToken<ArrayList<MemoList_Data>>() {
        }.getType();
        Gson gson = new Gson();
        String json = gson.toJson(arrayDataList, listType);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("memolist", json);
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

    public void onRequestPermissionResult(int requestCode, String permission[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                //권한이 취소되면 result 배열은 비어있다
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 확인", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();
                    finish(); // 권한이 없으면 앱 종료
                }
            }
        }
    }

    private ArrayList<MemoList_Data> readmemodata(String key){
        SharedPreferences sharedPreferences = getSharedPreferences("memolist", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("memolist",null);
        Type listType = new TypeToken<ArrayList<MemoList_Data>>(){}.getType();
        MemoList_Activity.arrayDataList = gson.fromJson(json, listType);
        return MemoList_Activity.arrayDataList;
    }
}