package com.example.mindmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MindMapAdapter extends RecyclerView.Adapter<MindMapAdapter.ViewHolder>
{
    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> descriptions =  new ArrayList<>();

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
        TextView descriptionText ;
        ImageButton optionButton;

        ViewHolder(View itemView) {
            super(itemView) ;

            descriptionText = itemView.findViewById(R.id.descriptionText) ;
            optionButton = itemView.findViewById(R.id.optionButton);

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

    MindMapAdapter(ArrayList<String> list) {
        data.addAll(list);
        descriptions.addAll(list);
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
        String text = descriptions.get(position) ;
        holder.descriptionText.setText(text) ;
    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }

    public void filter(String filterStr){
        descriptions.clear();

        if (filterStr.length() == 0) {
            descriptions.addAll(data);
        }
        else {
            for (String str : data) {
                if (str.contains(filterStr)) {
                    descriptions.add(str);
                }
            }
        }

        notifyDataSetChanged();
    }
}
