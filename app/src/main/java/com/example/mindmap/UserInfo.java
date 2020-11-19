package com.example.mindmap;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;

public class UserInfo {
    private static UserInfo instance = null;

    public static UserInfo getInstance(){
        if(instance == null){
            instance = new UserInfo();
        }

        return instance;
    }

    private String userId;

    public void setUserId(FirebaseUser user){
        userId = user.getEmail().replace(".","_");
        Log.d("userid", userId);
    }

    public String getUserId(){
        return userId;
    }
}
