package com.example.mindmap;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;

/**
 * 개발자 : 20191546 강현서
 * 날짜 : 2020-11-19
 * 기능 : 로그인 정보 저장 Class
 */
public class UserInfo {
    private static UserInfo instance = null;

    public static UserInfo getInstance(){
        if(instance == null){
            instance = new UserInfo();
        }

        return instance;
    }

    private String userId;
    private String name;
    private Uri photoUri;

    public void setUserId(FirebaseUser user){
        for(com.google.firebase.auth.UserInfo profile: user.getProviderData()){
            userId = profile.getEmail().replace(".","_");
            name = profile.getDisplayName();
            photoUri = profile.getPhotoUrl();
        }


        Log.d("userid", userId);
        Log.d("name", name);
        Log.d("photoUri", photoUri.toString());
    }

    public String getUserId(){
        return userId;
    }
    public String getName() {return name;}
    public Uri getPhotoUri() {return photoUri;}
}
