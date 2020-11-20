package com.example.mindmap;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;

import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 개발자 : 20191583 나민형
 * 마지막 수정일 : 2020-11-20
 * 기능 : 시작 액티비티, 지금까지 만든 마인드맵이 리스트로 표시됨
 * 추가 설명 : 공유, 삭제, 편집 가능
 */

public class ListActivity extends AppCompatActivity {

    int selectedPos = -1;
    final SaveNodeFirebase db = new SaveNodeFirebase();
    String imageStr = "";
    String selectedId = "";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        Window window = getWindow();
        window.setStatusBarColor(Color.BLACK);
        setContentView(R.layout.activity_list);

        FloatingActionButton infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });
        loadData();

        FloatingActionButton fab = findViewById(R.id.addFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, TemplateActivity.class);
                startActivity(intent);
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

    public void loadData() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                final ArrayList<MindMapData> data = new ArrayList<>();
                data.addAll(db.mindMapdataList);

                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(ListActivity.this));

                final MindMapAdapter adapter = new MindMapAdapter(data);
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new MindMapAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemCLick(View v, int pos) {
                        Intent intent = new Intent(ListActivity.this, MindMapEditorActivity.class);
                        intent.putExtra("currentId",adapter.filteredData.get(pos).getId());
                        startActivity(intent);
                    }
                });

                adapter.setOnOptionClickListener(new MindMapAdapter.OnOptionClickListener() {
                    @Override
                    public void OnOptionClick(View v, int pos) {
                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ListActivity.this);
                        bottomSheetDialog.setContentView(R.layout.bottom_sheet_list);
                        //bottomSheetDialog.show();

                        new ListFragment().show(getSupportFragmentManager(), "Dialog");

                        selectedPos = pos;
                        imageStr = adapter.filteredData.get(selectedPos).getImage();
                        selectedId = adapter.filteredData.get(selectedPos).getId();
                    }
                });

                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_list, null);

                adapter.filter("");

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
        };

        db.readAllMindMapInfo(r);
    }
}
