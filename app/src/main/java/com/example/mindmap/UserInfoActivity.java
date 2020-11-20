package com.example.mindmap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


/**
 * 개발자 : 20191546 강현서
 * 날짜 : 2020-11-20
 * 기능 : 로그인후 유저정보 출력해주는 popup Activity
 */
public class UserInfoActivity extends Activity {
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);

        ImageView imageView = findViewById(R.id.profileImage);
        TextView profileName = findViewById(R.id.profileName);
        TextView profileEmail = findViewById(R.id.profileEmail);

        Log.d("uri22", UserInfo.getInstance().getPhotoUri().toString());
        final Uri photoUri = Uri.parse(UserInfo.getInstance().getPhotoUri().toString());
        imageView.setImageURI(photoUri);
        profileName.setText(UserInfo.getInstance().getName());
        profileEmail.setText(UserInfo.getInstance().getUserId());
        Thread mTrhead = new Thread(){
            @Override
            public void run(){
                try{
                    URL url = new URL(photoUri.toString());
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                }catch (Exception e){

                }
            }
        };
        mTrhead.start();
        try{
            mTrhead.join();
            imageView.setImageBitmap(bitmap);
        }catch (InterruptedException e){

        }

    }

    //확인 버튼 클릭
    public void mOnClose(View v) {
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;

    }
}
