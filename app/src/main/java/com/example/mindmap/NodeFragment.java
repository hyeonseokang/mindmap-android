package com.example.mindmap;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class NodeFragment extends Fragment {

    public Node node;

    private ImageButton button;
    private TextView topText;

    private TextView text;
    private View view;

    private MindMapEditorActivity activity;

    private boolean root = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Node.
     */
    // TODO: Rename and change types and number of parameters
    public static NodeFragment newInstance(String param1, String param2) {
        NodeFragment fragment = new NodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NodeFragment() {
        // Required empty public constructor

    }

    public NodeFragment(MindMapEditorActivity activity, String text)
    {
        node = new Node(this, text);

        this.activity = activity;
    }

    public void rename(String text)
    {
        node.text = text;
        this.text.setText(text);
    }

    public void makeRoot()
    {
        root = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_node, container, false);

        text = view.findViewById(R.id.node_text);
        text.setText(node.text);

        final NodeFragment fragment = this;

        button = view.findViewById(R.id.node_img);
        topText = view.findViewById(R.id.node_text_top);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //node.addChild(activity.addNode(node.text + "_node", node).node);
                activity.openNodeMenu(fragment);
            }
        });

        if (root)
        {
            button.setImageDrawable(getResources().getDrawable(R.drawable.node_big));
            topText.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
        }

        return view;
    }
}