package com.example.bookchicken;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MemoAdd extends AppCompatActivity {

    private String uri_String;

    public static final int REQUEST_PERMISSION = 11;
    //    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int GALLERY_CODE = 12;

    private ImageView imageView;
    private String img_Path = ""; // 절대경로를 저정할 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_add);

        EditText memodate = (EditText) findViewById(R.id.memodate);
        EditText booktitle = (EditText) findViewById(R.id.booktitle);
        EditText bookmemo = (EditText) findViewById(R.id.bookmemo);

        Button btn_add = (Button) findViewById(R.id.btn_complete);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemoAdd.this, MemoList_Activity.class);

                MemoList_Activity.arrayDataList.add(new MemoList_Data(
                        memodate.getText().toString(),
                        booktitle.getText().toString(),
                        uri_String,
                        bookmemo.getText().toString()));
                savememoData("memolist", MemoList_Activity.arrayDataList);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

//                SharedPreferences sharedPreferences = getSharedPreferences("BookMemo", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                Gson gson = new GsonBuilder().create();
//                String MemoListData = gson.toJson("MemoList_Data", MemoList_Data.class);
//                editor.putString("MemoList",MemoListData);
//                editor.apply();

//                try {/
//                    Log.d(TAG, "bitmap : " + imageBitmap);
////                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
////                    Bitmap bitmap = drawable.getBitmap();
//                    //ByteArrayOutputStream(바이트 단위로 데이터를 전송하는 데이터를 다루기 위한 추상개념)
//                    //Stream? 데이터를 주고 받는 통로
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                    byte[] byteArray = stream.toByteArray();
//                    intent.putExtra("image", byteArray);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

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
    private void savememoData(String key, ArrayList<MemoList_Data> arrayDataList) {
        SharedPreferences sf = getSharedPreferences("memolist", MODE_PRIVATE);
        Type listType = new TypeToken<ArrayList<MemoList_Data>>() {}.getType();
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
}


    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == 1){
//            if(resultCode == RESULT_OK){
//
//                Uri uri = null;
//                if(data != null){
//                    uri = data.getData();
//                }
//                if(uri != null){
//                    imageView.setImageURI(uri);
//                }
//
//                try {
//                    InputStream in = getContentResolver().openInputStream(data.getData());
//
//                    Bitmap img = BitmapFactory.decodeStream(in);
//                    in.close();
//                    imageView.setImageBitmap(img);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }


    //    public void capture() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        if (intent.resolveActivity(getPackageManager()) != null) { // 인텐트를 처리할 카메라 activity가 있는지 확인
//
//            File photoFile = null; // 촬영할 사진을 저장할 파일 생성
//
//            try{
//                File tempDir = getCacheDir(); // 임시로 사용할 파일이므로 경로는 캐시폴더로
//
//                //임시촬영파일 셋팅
//                String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
//                String imageFileName = "Capture_" + timeStamp + "_";
//
//                File tempImage = File.createTempFile(
//                    imageFileName, // 파일이름
//                        ".jpg", // 파일형식
//                        tempDir // 경로
//                );
//
//                mCurrentPhotoPath = tempImage.getAbsolutePath(); // Action_view 인텐트를 사용할 경로(임시파일경로)
//
//                photoFile = tempImage;
//
//            }catch(IOException e){
//                Log.w(TAG,"파일 생성 에러",e);
//            }
//
//            //파일이 정상적으로 생성되었다면 계속 진행
//            if(photoFile != null){
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.bookchicken.fileprovider", photoFile);
//
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI); // 인텐트 uri에 담기
//                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE); // 인텐트 실행
//            }else{
//                Uri photoURI = Uri.fromFile(tempFile);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
//    }
//
//        protected void onActivityResult(int requestcode, int resultCode, Intent data) {
//        super.onActivityResult(requestcode, resultCode, data);
//        try{
//            switch(requestcode){
//                case REQUEST_IMAGE_CAPTURE: {
//                    if(resultCode == RESULT_OK){
//
//                        File file = new File(mCurrentPhotoPath);
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
//
//                        if(bitmap != null){
//                            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
//                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                                    ExifInterface.ORIENTATION_UNDEFINED);
//
//                            Bitmap rotateBitmap = null;
//                            switch(orientation){
//                                case ExifInterface.ORIENTATION_ROTATE_90:
//                                    rotateBitmap = rotateImage(bitmap, 90);
//                                break;
//
//                                case ExifInterface.ORIENTATION_ROTATE_180:
//                                    rotateBitmap = rotateImage(bitmap, 180);
//                                break;
//
//                                case ExifInterface.ORIENTATION_ROTATE_270:
//                                    rotateBitmap = rotateImage(bitmap, 270);
//                                break;
//
//                                case ExifInterface.ORIENTATION_NORMAL:
//                              default:
//                                  rotateBitmap = bitmap;
//                                break;
//                            }
//                            imageView.setImageBitmap(rotateBitmap); // 이미지 저장
//                        }
//                    }
//                }
//                break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        }
//
//    //카메라에 맞게 이미지 로테이션
//    public static Bitmap rotateImage(Bitmap source, float angle){
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//    }

//    //이미지 저장 메서드
//    private void saveImg(){
//        try{
//            //저장할 파일 경로
//            File storageDir = new File(getFilesDir() + "/capture");
//            if(!storageDir.exists()) // 폴더가 없으면 생성.
//                storageDir.mkdirs();
//
//            String filename = "캡쳐파일" + ".jpg";
//
//            //기존에 있다면 삭제
//            File file = new File(storageDir, filename);
//            boolean deleted = file.delete();
//            Log.w(TAG,"Delete Dup Check" + deleted);
//            FileOutputStream output = null;
//
//            try{
//                output = new FileOutputStream(file);
//                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//                Bitmap bitmap = drawable.getBitmap();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
//            }catch (FileNotFoundException e){
//                e.printStackTrace();
//            }finally{
//                try{
//                    assert output != null;
//                    output.close();
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//            }
//        }catch (Exception e){
//        }
//    }

//    public void onResume(){
//        super.onResume();
//        checkPermission(); // 권한체크
//    }




//    //카메라에서 이미지 가져오기
//    public void capture() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//
//            try{
//                tempFile = createImageFile();
//            }catch(IOException e){
//                Toast.makeText(this,"이미지 처리 오류! 다시 시도해주세요",Toast.LENGTH_SHORT).show();
//                finish();
//                e.printStackTrace();
//            }
//            if(tempFile != null){
//                Uri photoUri = FileProvider.getUriForFile(this,
//                        "{applicationId}.provider", tempFile);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//
//            }else{
//                Uri photoUri = Uri.fromFile(tempFile);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
//    }

//    //카메라에 찍은 사진을 저장할 파일 만들기(파일명과 폴더명 작성, 카메라에서 찍은 사진을 내가 생성한 파일에 저장하고 받아올 수 있다)
//    private File createImageFile() throws IOException{
//        //이미지 파일 이름
//        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
//        String imageFileName = "bookimage_" + timeStamp + "_";
//
//        //이미지가 저장될 폴더 이름(bookimage)
//        File storageDir = new File(Environment.getExternalStorageDirectory() + "/bookimage/");
//        if(!storageDir.exists()) storageDir.mkdirs();
//
//        //빈 파일 생성
//        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
//
//        return image;
//    }
//
//    //전역변수 tempFile의 경로를 불러와 bitmap 파일로 변형한 후 imageView에 해당 이미지를 넣어줍니다
//    private void setImage(){
//        ImageView imageView = findViewById(R.id.bookimage);
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
//
//        imageView.setImageBitmap(originalBm);
//    }


//    protected void onActivityResult(int requestcode, int resultCode, Intent data) {
//        super.onActivityResult(requestcode, resultCode, data);
//        if (requestcode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//           setImage();
//        }
//    }




//    private void saveBitmapToJpeg(Bitmap bitmap, String name){
//
//        //내부저장소 캐시 경로를 불러옵니다
//        File storage = getCacheDir();
//
//        //저장할 파일 이름
//        String fileName = name + ".jpg";
//
//        //storage에 파일 인스턴스를 생성합니다
//        File tempFile = new File(storage, fileName);
//
//        try{
//            //자동으로 빈 파일을 생성합니다
//            tempFile.createNewFile();
//
//            //파일을 쓸 수 있는 스트림을 준비합니다
//            FileOutputStream out = new FileOutputStream(tempFile);
//
//            //compress 함수를 통해 스트림에 비트맵을 저장합니다
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//
//            //스트림 사용 후 닫아줍니다
//            out.close();
//
//        }catch(FileNotFoundException e) {
//            Log.d(TAG, "FileNotFoundException: " + e.getMessage());
//        }catch(IOException e){
//            Log.e(TAG, "IOException: " + e.getMessage());
//        }
//    }
//
//    private void getBitmapFromCacheDir(){
//        String found;
//        Bitmap bitmap;
//
//        File file = new File(getCacheDir().toString());
//        File[] files = file.listFiles();
//
//        for(File tempFile : files){
//            if(tempFile.getName().contains("")){
//                found = (tempFile.getName());
//                String path = getCacheDir() + "/" + found;
//                bitmap = BitmapFactory.decodeFile(path);
//            }
//        }
//        return bitmap;
//    }

//    //setStringArrayPref는 ArrayList를 Json으로 변환하여 SharedPreferences에 String을 저장하는 코드
//    public static void setStringArrayPref(Context context, String key, ArrayList<MemoList_Data> values){
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = prefs.edit();
//        JSONArray a = new JSONArray();
//
//        for (int i = 0; i<values.size(); i++){
//            a.put(values.get(i));
//        }
//        if(!values.isEmpty()){
//            editor.putString(key, a.toString());
//        }else{
//            editor.putString(key, null);
//        }
//        editor.apply();
//    }
//}

//        Button btn_image = (Button) findViewById(R.id.btn_imageadd);
//        btn_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                resultLauncher.launch(intent);
//            }
//        });
//    }
//
//    private ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == RESULT_OK) {
//                        Intent intent = result.getData();
//                        Bundle extras = intent.getExtras();
//                        Bitmap imageBitmap = (Bitmap) extras.get("intent");
//                        imageView.setImageBitmap(imageBitmap);
//                    }
//                }
//            });


//        imageView = findViewById(R.id.bookimage);
//        Button btn_picture = (Button) findViewById(R.id.btn_imageadd);
//        btn_picture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                takePicture();
//            }
//        });
//    }
//
//    public void takePicture() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//        }
//    }
//
//    public void onActivityResult(int requestcode, int resultCode, Intent data) {
//        super.onActivityResult(requestcode, resultCode, data);
//        if (requestcode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);
//        }
//    }
//}

//    }
