package com.example.mindmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TemplateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);

        Button backButton;
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayList<String> templates = new ArrayList<String>();
        templates.add("마인드맵");

        RecyclerView recyclerView = findViewById(R.id.templateRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TemplateAdapter adapter = new TemplateAdapter(templates);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new TemplateAdapter.OnItemClickListener() {
            @Override
            public void OnItemCLick(View v, int pos) {
               Intent intent = new Intent(TemplateActivity.this, CreateActivity.class);
               startActivity(intent);
            }
        });
    }
}