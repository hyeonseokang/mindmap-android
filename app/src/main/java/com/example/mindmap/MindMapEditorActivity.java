package com.example.mindmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MindMapEditorActivity extends AppCompatActivity {

    private ArrayList<NodeFragment> nodeFragments;
    public NodeFragment movingFragment;

    private int prevX, prevY;

    private DrawView drawView;

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

    public static void move(Fragment fragment, int deltaX, int deltaY)
    {
        move(fragment.getView(), deltaX, deltaY);
    }

    public static void move(View view, int deltaX, int deltaY)
    {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
        moveTo(view, params.leftMargin + deltaX, params.topMargin + deltaY);
    }

    public static void moveTo(Fragment fragment, int x, int y)
    {
        moveTo(fragment.getView(), x, y);
    }

    public static void moveTo(View view, int x, int y)
    {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
        params.setMargins(x, y, 0, 0);
        view.requestLayout();
    }

    public Node getRootNode()
    {
        return nodeFragments.get(0).node;
    }

    public ArrayList<NodeFragment> getNodeFragments()
    {
        return nodeFragments;
    }

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
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                builder.setTitle("추천단어");

                final String[] items = new String[] {"a", "b", "c"};

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
            }
        });

        Button btnDefinition = nodeMenu.findViewById(R.id.btn_definition);
        btnDefinition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                builder.setTitle("뜻").setMessage("뜻이다.");

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

    public NodeFragment addNode(NodeFragment parent, String text)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        NodeFragment fragment = new NodeFragment(this, text);
        fragmentTransaction.add(R.id.node_container, fragment);
        fragmentTransaction.commit();

        nodeFragments.add(fragment);

        if (parent != null)
        {
            parent.node.addChild(fragment.node);
        }

        return fragment;
    }

    public void renameNode(NodeFragment fragment, String text)
    {
        fragment.rename(text);
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mind_map_editor);

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
                        dialog.dismiss();
                    }
                });

                Button btnShare = menu.findViewById(R.id.btn_share);
                btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button btnDelete = menu.findViewById(R.id.btn_delete);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        nodeFragments = new ArrayList<>();

        prevX = prevY = -1;

        addNode(null, "Root");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
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