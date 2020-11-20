package com.example.mindmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

public class MindMapEditorActivity extends AppCompatActivity {

    private ArrayList<NodeFragment> nodeFragments;
    public NodeFragment movingFragment;

    private int prevX, prevY;

    private DrawView drawView;

    private View mindMapLayout;

    private CrawlingThread crawling;

    private Random randomFragmentMargins;

    private SaveNodeFirebase db;

    // 현재 노드와 자식 노드까지 한번에 움직임
    public static void moveRecursively(NodeFragment fragment, int deltaX, int deltaY)
    {
        try
        {
            move(fragment, deltaX, deltaY);
        }
        catch(Exception e)
        {

        }

        for (Node child : fragment.node.children)
        {
            moveRecursively(child.fragment, deltaX, deltaY);
        }
    }

    // 상단바와 타이틀바의 길이를 합한 값을 반환
    public int getBarHseight() {
        View decorView = getWindow().getDecorView();
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        View contentView = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        int[] location = new int[2];
        contentView.getLocationInWindow(location);
        int titleBarHeight = location[1] - statusBarHeight;

        return statusBarHeight + titleBarHeight;
    }

    // move, moveTo 함수들: 지정한 노드를 움직임

    public static void move(NodeFragment fragment, int deltaX, int deltaY)
    {
        move(fragment.node, fragment.getView(), deltaX, deltaY);
    }

    public static void move(Node node, View view, int deltaX, int deltaY)
    {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
        moveTo(node, view, params.leftMargin + deltaX, params.topMargin + deltaY);
    }

    public static void moveTo(NodeFragment fragment, int x, int y)
    {
        moveTo(fragment.node, fragment.getView(), x, y);
    }

    public static void moveTo(Node node, View view, int x, int y)
    {
        node.leftMargin = x;
        node.topMargin = y;

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
        params.setMargins(x, y, 0, 0);
        view.requestLayout();
    }

    // 루트 노드를 얻음
    public Node getRootNode()
    {
        return nodeFragments.get(0).node;
    }

    // 노트 리스트를 얻음
    public ArrayList<NodeFragment> getNodeFragments()
    {
        return nodeFragments;
    }

    // 노드 단위의 메뉴를 염
    public void openNodeMenu(final NodeFragment fragment)
    {
        final MindMapEditorActivity activity = this;

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View nodeMenu = layoutInflater.inflate(R.layout.fragment_node_menu, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(nodeMenu);

        nodeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

        Button btnAdd = nodeMenu.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("단어 입력");

                final EditText input = new EditText(activity);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("생성", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addNode(fragment, input.getText().toString());
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                dialog.dismiss();
            }
        });

        Button btnRecommand = nodeMenu.findViewById(R.id.btn_recommand);
        btnRecommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crawling = new CrawlingThread() {
                    @Override
                    public void CompleteCrawling() {
                    }
                };

                try {
                    crawling.start(fragment.node.text);
                }
                catch(Exception e)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                    builder.setTitle("알림").setMessage("단어를 찾을 수 없습니다.");

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    dialog.dismiss();
                    crawling = null;
                }

                try {
                    crawling.join();

                    ArrayList<String> words = crawling.getSimilarWords("비슷한말");
                    if (words.size() == 0)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                        builder.setTitle("알림").setMessage("추천할 단어가 없습니다.");

                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        dialog.dismiss();

                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                    builder.setTitle("추천단어");

                    final String[] items = new String[words.size()];
                    words.toArray(items);

                    builder.setItems(items, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int pos)
                        {
                            addNode(fragment, items[pos]);
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    dialog.dismiss();
                    crawling = null;
                }
                catch(Exception e)
                {

                }
            }
        });

        Button btnDefinition = nodeMenu.findViewById(R.id.btn_definition);
        btnDefinition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crawling = new CrawlingThread() {
                    @Override
                    public void CompleteCrawling() {
                    }
                };
                crawling.start(fragment.node.text);
                try {
                    crawling.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                ArrayList<String> words = crawling.getSimilarWords("meaning");
                if (words.size() == 0)
                {
                    builder.setTitle("알림").setMessage("단어를 찾을수 없습니다.");

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    dialog.dismiss();

                    return;
                }
                String wordMean = "";
                for (int i=0;i<words.size();i++){
                    wordMean = wordMean + (i+1) + ". " + words.get(i) + "\n";
                }
                builder.setTitle("뜻").setMessage(wordMean);

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                dialog.dismiss();
            }
        });

        Button btnEdit = nodeMenu.findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("단어 수정");

                final EditText input = new EditText(activity);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(fragment.node.text);
                builder.setView(input);

                builder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        renameNode(fragment, input.getText().toString());
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                dialog.dismiss();
            }
        });

        Button btnMove = nodeMenu.findViewById(R.id.btn_move);
        Button btnRemove = nodeMenu.findViewById(R.id.btn_remove);

        if (fragment.node.parent != null)
        {
            btnMove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    movingFragment = fragment;
                    dialog.dismiss();
                }
            });

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeNode(fragment);
                    dialog.dismiss();
                }
            });
        }
        else
        {
            btnMove.setVisibility(View.GONE);
            btnRemove.setVisibility(View.GONE);
        }
    }

    private NodeFragment createNodeFragment(NodeFragment nodeFragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        NodeFragment fragment = nodeFragment;
        fragmentTransaction.add(R.id.node_container, fragment);
        fragmentTransaction.commit();

        return fragment;
    }

    public void clearNodes()
    {
        for (NodeFragment fragment : nodeFragments)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }

        nodeFragments.clear();
    }

    // 노드를 추가함
    public NodeFragment addNode(final NodeFragment parent, String text)
    {
        final NodeFragment fragment = createNodeFragment(new NodeFragment(this, text));

        nodeFragments.add(fragment);

        if (parent != null)
        {
            parent.node.addChild(fragment.node);
        }

        fragment.onAddToLayout = new Runnable() {
            @Override
            public void run() {
                int leftMargin = randomFragmentMargins.nextInt(200) - 99;
                int topMargin = randomFragmentMargins.nextInt(200) - 99;

                if (parent != null)
                {
                    leftMargin += parent.node.leftMargin;
                    topMargin += parent.node.topMargin;
                }

                moveTo(fragment, leftMargin, topMargin);
            }
        };

        return fragment;
    }

    private NodeFragment _createNodeFragmentHierarchy(final Node node)
    {
        final NodeFragment fragment = createNodeFragment(new NodeFragment(this, node));

        fragment.onAddToLayout = new Runnable() {
            @Override
            public void run() {
                moveTo(fragment, node.leftMargin, node.topMargin);
            }
        };

        nodeFragments.add(fragment);

        for (Node child : node.children)
        {
            _createNodeFragmentHierarchy(child);
        }

        return fragment;
    }

    public NodeFragment createNodeFragmentHierarchy(Node root)
    {
        NodeFragment rootFragment = _createNodeFragmentHierarchy(root);
        rootFragment.makeRoot();

        return rootFragment;
    }

    // 노드에서 표시되는 텍스트를 수정함
    public void renameNode(NodeFragment fragment, String text)
    {
        fragment.rename(text);
    }

    // 노드를 제거함
    public boolean removeNode(Node node)
    {
        NodeFragment nodeFragment = null;

        for (NodeFragment fragment : nodeFragments)
        {
            if (fragment.node == node)
            {
                nodeFragment = fragment;
            }
        }

        if (nodeFragment == null)
        {
            return false;
        }

        removeNode(nodeFragment);

        return true;
    }

    // 노드를 제거함
    public void removeNode(NodeFragment fragment)
    {
        for (Node child : fragment.node.children)
        {
            removeNode(child.fragment);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();

        nodeFragments.remove(fragment);
    }

    // 마인드맵을 비트맵으로 캡쳐
    public Bitmap captureMindMap()
    {
        mindMapLayout.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(mindMapLayout.getDrawingCache());
        mindMapLayout.setDrawingCacheEnabled(false);

        return bitmap;
    }

    // 비트맵을 Base64로 인코딩
    public static String convertBitmapToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Base64로부터 비트맵을 디코딩
    public static Bitmap convertBase64ToBitmap(String base64)
    {
        byte[] byteArray = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    // 마인드맵을 캡쳐하여 Base64로 인코딩하여 반환
    public String captureMipMapAsBase64()
    {
        return convertBitmapToBase64(captureMindMap());
    }

    // 마인드맵 공유
    public void shareMindMap()
    {
        Bitmap icon = captureMindMap();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);

        OutputStream outstream;
        try {
            outstream = getContentResolver().openOutputStream(uri);
            icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image"));
    }

    public void save()
    {
        db.writeNodes(getRootNode());
        db.writeMineMapImage(captureMipMapAsBase64());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mind_map_editor);

        getSupportActionBar().hide();

        randomFragmentMargins = new Random();

        mindMapLayout = findViewById(R.id.mind_map_layout);

        ViewGroup drawViewContainer = (ViewGroup)findViewById(R.id.draw_view_container);

        drawView = new DrawView(this);
        drawViewContainer.addView(drawView);

        final MindMapEditorActivity activity = this;

        FloatingActionButton floatingButton = findViewById(R.id.floating_button);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View menu = layoutInflater.inflate(R.layout.fragment_editor_floating_button_menu, null);

                final BottomSheetDialog dialog = new BottomSheetDialog(activity);
                dialog.setContentView(menu);

                menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button btnSave = menu.findViewById(R.id.btn_save);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save();
                        dialog.dismiss();
                    }
                });

                Button btnShare = menu.findViewById(R.id.btn_share);
                btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareMindMap();
                        dialog.dismiss();
                    }
                });

                Button btnDelete = menu.findViewById(R.id.btn_delete);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage("정말 삭제하시겠습니까?");

                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.removeMindeMap(db.getCurrentId());
                                activity.finish();
                            }
                        });
                        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                activity.finish();
                            }
                        });

                        builder.show();

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("저장하시겠습니까?");

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        save();
                        activity.finish();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        activity.finish();
                    }
                });

                builder.show();
            }
        });

        nodeFragments = new ArrayList<>();

        prevX = prevY = -1;

        db = new SaveNodeFirebase();
        db.setCurrentId(getIntent().getExtras().getString("currentId"));

        db.loadNodes(new Runnable() {
            @Override
            public void run() {
                clearNodes();
                createNodeFragmentHierarchy(db.CreateNode(db.getPost(), null));
            }
        });

        //addNode(null, "마인드맵").makeRoot();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // 선택된 노드를 이동

        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                prevX = x;
                prevY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaX = x - prevX, deltaY = y - prevY;

                if (movingFragment != null)
                {
                    moveRecursively(movingFragment, deltaX, deltaY);
                }
                else
                {
                    for (NodeFragment fragment : nodeFragments) {
                        move(fragment, deltaX, deltaY);
                    }
                }

                prevX = x;
                prevY = y;
                break;

            case MotionEvent.ACTION_UP:
                if (movingFragment != null)
                {
                    movingFragment = null;
                }

                prevX = prevY = -1;
                break;
        }

        return false;
    }
}