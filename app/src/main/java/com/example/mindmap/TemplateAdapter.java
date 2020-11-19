package com.example.mindmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TemplateAdapter  extends RecyclerView.Adapter<TemplateAdapter.ViewHolder> {
    ArrayList<IdeaTemplate> templates = null;

    private TemplateAdapter.OnItemClickListener listener = null;
    private int selected = -1;

    public interface OnItemClickListener{
        void OnItemCLick(View v, int pos);
    }

    public void setOnItemClickListener(TemplateAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView templateName ;
        TextView templateDescription ;
        ImageView check;

        ViewHolder(View itemView) {
            super(itemView) ;

            templateName = itemView.findViewById(R.id.templateNameText) ;
            templateDescription = itemView.findViewById(R.id.templateDescriptionText) ;
            check = itemView.findViewById(R.id.checkImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        if(listener != null){
                            if(selected == pos){
                                selected = -1;
                                v.setBackgroundResource(0);
                                check.setVisibility(View.GONE);
                            }
                            else{
                                selected = pos;
                                v.setBackgroundResource(R.drawable.style_select_item);
                                check.setVisibility(View.VISIBLE);
                            }
                            listener.OnItemCLick(v, pos);
                        }
                    }
                }
            });
        }
    }

    TemplateAdapter(ArrayList<IdeaTemplate> list) {
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
        String name = templates.get(position).name;
        String description = templates.get(position).description;

        holder.templateName.setText(name) ;
        holder.templateDescription.setText(description);
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    public int getSelected() {return selected;}
}
