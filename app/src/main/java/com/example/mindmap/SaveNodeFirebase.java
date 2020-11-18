package com.example.mindmap;

import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 개발자 : 20191546 강현서
 * 날짜 : 2020-11-18
 * 기능 : Node Class 를 가져와서 Json 파일로 만들어 Firebase 에 저장하고 불러오는 클래스
 */

public class SaveNodeFirebase {
    private DatabaseReference mDatabase;
    private String post;

    public SaveNodeFirebase(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    // 실제 사용하는 메소드
    public String loadNodes(){
        readNodes();
        return post;
    }

    /** CreateNode
     * 개발자 : 고현성이 수정 예정
     * 날짜 : 2020-11-18
     */
    // 버리는 메소드
    public Node CreateNode(String json, NodeFragment nodeFragment) {
        Node rootNode = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            String text = jsonObject.getString("text");
            int leftMargin = jsonObject.getInt("leftMargin");
            int topMargin = jsonObject.getInt("topMargin");

            rootNode = new Node(nodeFragment, text);
            rootNode.leftMargin = leftMargin;
            rootNode.topMargin = topMargin;

            JSONArray children = jsonObject.getJSONArray("children");

            for(int i=0;i<children.length();i++){
                JSONObject childrenJson = children.getJSONObject(i);
                Node childNode = CreateNode(childrenJson.toString(), nodeFragment);
                rootNode.addChild(childNode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootNode;
    }

    public void writeNodes(Node nodes){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = createJsonNodes(nodes);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        writeJson("1", jsonObject);
    }

    public JSONObject createJsonNodes(Node node) throws JSONException {
        ArrayList<Node> children = node.children;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", node.text);
        jsonObject.put("leftMargin",node.leftMargin);
        jsonObject.put("topMargin", node.topMargin);

        JSONArray childrenArray = new JSONArray();
        for(int i=0;i<children.size();i++){
            Node child = children.get(i);
            JSONObject childJson = createJsonNodes(child);
            childrenArray.put(childJson);
        }
        if(childrenArray.length() >= 1)
            jsonObject.put("children", childrenArray);

        return jsonObject;
    }

    private void readNodes(){
        mDatabase.child("users").child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class) != null){
                    post = dataSnapshot.getValue(String.class);
                    Log.w("FireBaseData", "getData" + post);
                } else {
                    Log.d("실패", "실패");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("실패", "실패");
            }
        });
    }

    private void writeJson(String userId, JSONObject jsonObject) {

        mDatabase.child("users").child(userId).setValue(jsonObject.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("성공", "성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("실패", "실패");
                    }
                });

    }
}
