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
import java.util.Random;

/**
 * 개발자 : 20191546 강현서
 * 날짜 : 2020-11-18
 * 기능 : Node Class 를 가져와서 Json 파일로 만들어 Firebase 에 저장하고 불러오는 클래스
 */

public class SaveNodeFirebase {
    private DatabaseReference mDatabase;
    private String post;
    private String currentId = "hello";

    public SaveNodeFirebase(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public String createNewMindMapId(){
        // 랜덤 id값 만든후 검사 무한반복
        return "hello";
    }

    // 처음 마인드맵 새로 만들때 쓰는 거
    public void createNewMindMap(String startWord, String explain){
        String id = this.createNewMindMapId(); // 새롭게 id 만들고
        this.setCurrentId(id);
        this.writeNodes(new Node(null, startWord)); // 노드 생성하고 데이터베이스 보내고
        this.writeMindMapExplain(explain); // 설명 데이터베이스에 보내고
        // id 는 createNewMindMapId() 넣으면 자동 동기화
    }

    public void writeMindMapExplain(String explain){
        mDatabase.child("users").child("1").child(currentId).child("explain").setValue(explain);
    }

    public void writeMineMapImage(String image){
        mDatabase.child("users").child("1").child(currentId).child("image").setValue(image);
    }

    public String getPost()
    {
        return post;
    }

    public void setCurrentId(String id){
        currentId = id;
    }

    public String getCurrentId(){
        return currentId;
    }

    // 실제 사용하는 메소드
    public String loadNodes(Runnable callback){
        readNodes(callback);
        return post;
    }

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

    private void readNodes(final Runnable callback){
        mDatabase.child("users").child("1").child(currentId).child("nodes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class) != null){
                    post = dataSnapshot.getValue(String.class);
                    Log.w("FireBaseData", "getData" + post);

                    callback.run();
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

        mDatabase.child("users").child(userId).child(currentId).child("nodes").setValue(jsonObject.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("성공", "성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("쓰기 실패", "실패");
                    }
                });

    }
}
