package com.example.mindmap;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 개발자 : 20191546 강현서
 * 날짜 : 2020-11-18
 * 기능 : 우리말샘 open api를 통해 얻은 Json 에서 특정 단어 유의어 추출하는 Class
 */

public class SimilarWordJsonParser {
    public HashMap<String, ArrayList<String>> parsing(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = new JSONArray(jsonObject.getString("children"));

        HashMap<String, ArrayList<String>> map = new HashMap<>();
        for(int i=0;i<jsonArray.length();i++){
            JSONObject wordJson = jsonArray.getJSONObject(i);
            String name = wordJson.getString("name");
            ArrayList<String> children = new ArrayList<>();

            JSONArray childrenArray = new JSONArray(wordJson.getString("children"));
            for(int j=0;j<childrenArray.length();j++){
                JSONObject child = childrenArray.getJSONObject(j);
                children.add(child.getString("name"));
            }
            map.put(name, children);
        }

        return map;
    }
}
