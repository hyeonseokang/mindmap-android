package com.example.mindmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TemplateAdapter  extends RecyclerView.Adapter<TemplateAdapter.ViewHolder> {
    ArrayList<String> templates = null;

    private TemplateAdapter.OnItemClickListener listener = null;

    public interface OnItemClickListener{
        void OnItemCLick(View v, int pos);
    }

    public void setOnItemClickListener(TemplateAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView templateName ;

        ViewHolder(View itemView) {
            super(itemView) ;

            templateName = itemView.findViewById(R.id.templateName) ;

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
        }
    }

    TemplateAdapter(ArrayList<String> list) {
        templates = list;
    }

    @NonNull
    @Override
    public TemplateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_template, parent, false) ;
        TemplateAdapter.ViewHolder vh = new TemplateAdapter.ViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateAdapter.ViewHolder holder, int position) {
        String text = templates.get(position) ;
        holder.templateName.setText(text) ;
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }
}
