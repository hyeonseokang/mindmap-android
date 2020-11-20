package com.example.mindmap;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    final ArrayList<MindMapData> data = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_list);

        loadData();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final MindMapAdapter adapter = new MindMapAdapter(data);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MindMapAdapter.OnItemClickListener() {
            @Override
            public void OnItemCLick(View v, int pos) {
                Toast.makeText(ListActivity.this, Integer.toString(pos), Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnOptionClickListener(new MindMapAdapter.OnOptionClickListener() {
            @Override
            public void OnOptionClick(View v, int pos) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ListActivity.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_list);
                bottomSheetDialog.show();

                Toast.makeText(ListActivity.this, Integer.toString(pos), Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton fab = findViewById(R.id.addFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, TemplateActivity.class);
                startActivity(intent);
            }
        });

        final TextView noResultText;
        noResultText = findViewById(R.id.noResultText);

        final EditText search;
        search = findViewById(R.id.searchEditText);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void onTextChanged(CharSequence seq, int start, int before, int count) {
                String charText = seq.toString();
                adapter.filter(charText);

                if(adapter.getItemCount() == 0) noResultText.setVisibility(View.VISIBLE);
                else  noResultText.setVisibility(View.GONE);
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

    public void loadData()
    {
        final SaveNodeFirebase db = new SaveNodeFirebase();

        db.readAllMindMapInfo(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i < db.mindMapdataList.size(); i++){
                    MindMapData mindMapData = db.mindMapdataList.get(i);

                    data.add((mindMapData));
                }
            }
        });
    }
}
