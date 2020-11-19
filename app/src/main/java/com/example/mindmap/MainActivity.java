package com.example.mindmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //Intent intent = new Intent(this, ListActivity.class);
        Intent intent = new Intent(this, MindMapEditorActivity.class);
        startActivity(intent);
    }
}

class TestGetMindMap{
    SaveNodeFirebase db;
    TestGetMindMap(){
        db = new SaveNodeFirebase();
        db.readAllMindMapInfo(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<db.mindMapdataList.size();i++){
                    MindMapData mindMapData = db.mindMapdataList.get(i);
                    Log.d("id 값", mindMapData.getId());
                    Log.d("image string 값",mindMapData.getId());
                    Log.d("설명", mindMapData.getExplain());
                }
            }
        });
    }
}