package com.example.mindmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * 개발자 : 20191583 나민형
 * 마지막 수정일 : 2020-11-20
 * 기능 : recycleview 의 어댑터
 * 추가 설명 : 마인드맵 미리보기, 주제, 루트 노드, 자식 노드, 설명, 옵션 버튼을 표시함
 */

public class MindMapAdapter extends RecyclerView.Adapter<MindMapAdapter.ViewHolder>
{
    ArrayList<MindMapData> data = new ArrayList<>();
    ArrayList<MindMapData> filteredData = new ArrayList<>();

    private OnItemClickListener listener = null;
    private OnOptionClickListener optionistener = null;

    public interface OnItemClickListener{
        void OnItemCLick(View v, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnOptionClickListener{
        void OnOptionClick(View v, int pos);
    }

    public void setOnOptionClickListener(OnOptionClickListener listener){
        this.optionistener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView startingWordText;
        TextView wordsText;
        TextView descriptionText ;
        ImageView imageView;
        ImageButton optionButton;

        ViewHolder(View itemView) {
            super(itemView) ;

            startingWordText = itemView.findViewById(R.id.startingWordText);
            wordsText = itemView.findViewById(R.id.wordsText);
            descriptionText = itemView.findViewById(R.id.descriptionText) ;
            optionButton = itemView.findViewById(R.id.optionButton);
            imageView = itemView.findViewById(R.id.prevImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if(listener != null){
                            listener.OnItemCLick(v, pos);
                        }
                    }
                }
            });

            optionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if(optionistener != null){
                            optionistener.OnOptionClick(view, pos);
                        }
                    }
                }
            });
        }
    }

    MindMapAdapter(ArrayList<MindMapData> list) {
        data.addAll(list);
    }

    @NonNull
    @Override
    public MindMapAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_list, parent, false) ;
        MindMapAdapter.ViewHolder vh = new MindMapAdapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull MindMapAdapter.ViewHolder holder, int position) {
        MindMapData mindMapData = data.get(position) ;

        String children = "";
        for(int i = 0; i < mindMapData.getRootNode().children.size(); i++){
            children += mindMapData.getRootNode().children.get(i).text + ", ";
        }
        //children = children.substring(0, children.length() - 1);

        holder.startingWordText.setText(mindMapData.getRootNode().text);
        holder.wordsText.setText(children) ;
        holder.descriptionText.setText(mindMapData.getExplain()) ;
        if(mindMapData.getImage() != null)
            holder.imageView.setImageBitmap(MindMapEditorActivity.convertBase64ToBitmap(mindMapData.getImage())); ;
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public void filter(String filterStr){
        filteredData.clear();

        if (filterStr.length() == 0) {
            filteredData.addAll(data);
        }
        else {
            for (MindMapData data_i : data) {
                if(data_i.getExplain() == null) continue;
                if (data_i.getExplain().contains(filterStr)) {
                    filteredData.add(data_i);
                }
            }
        }

        notifyDataSetChanged();
    }
}
