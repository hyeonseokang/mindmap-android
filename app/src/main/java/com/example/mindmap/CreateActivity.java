package com.example.mindmap;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;

/**
 * 개발자 : 20191583 나민형
 * 마지막 수정일 : 2020-11-20
 * 기능 : 마인드맵 생성 액티비티
 * 추가 설명 : 주제, 설명 입력 후 마인드맵 생성
 */

public class CreateActivity extends AppCompatActivity {

    SaveNodeFirebase db = new SaveNodeFirebase();
    TemplateActivity templateActivity = (TemplateActivity)TemplateActivity.activity;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_create);

        Button backToTemplate;
        backToTemplate = findViewById(R.id.backToTemplateButton);
        backToTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final EditText startingWord;
        startingWord = findViewById(R.id.startingWordEdit);

        final EditText descriptionEdit;
        descriptionEdit = findViewById(R.id.descriptionEdit);

        Button createIdea;
        createIdea = findViewById(R.id.createIdeaButton);
        createIdea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startingWord.getText().toString().length() == 0){
                    Toast.makeText(CreateActivity.this, "주제를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    createNewMindMap(startingWord.getText().toString(), descriptionEdit.getText().toString());

                    Intent intent = new Intent(CreateActivity.this, MindMapEditorActivity.class);
                    intent.putExtra("currentId", db.getCurrentId());
                    startActivity(intent);

                    templateActivity.finish();
                    finish();
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    public void createNewMindMap(String startWord, String explain){
        String id = db.createNewMindMapId(); // 새롭게 id 만들고
        db.setCurrentId(id);
        db.writeNodes(new Node(null, startWord)); // 노드 생성하고 데이터베이스 보내고
        db.writeMindMapExplain(explain); // 설명 데이터베이스에 보내고
        db.writeMineMapImage(null);
        // id 는 createNewMindMapId() 넣으면 자동 동기화
    }
}
