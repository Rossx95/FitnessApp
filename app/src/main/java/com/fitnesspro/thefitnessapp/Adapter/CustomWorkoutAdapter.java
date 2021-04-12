package com.fitnesspro.thefitnessapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.CustomWorkoutModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomWorkoutAdapter extends RecyclerView.Adapter<CustomWorkoutAdapter.ViewHolder> {
    private Context context;
    private List<CustomWorkoutModel> list;
    private CustomWorkoutAdapter.OnCallBack onCallBack;

    public CustomWorkoutAdapter(Context context, List<CustomWorkoutModel> list){
        this.context = context;
        this.list = list;
    }

    public void setOnCallBack(CustomWorkoutAdapter.OnCallBack onCallBack) {
        this.onCallBack = onCallBack;
    }

    @NonNull
    @Override
    public CustomWorkoutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_custom_workout,parent,false);
        return new CustomWorkoutAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CustomWorkoutAdapter.ViewHolder holder, int position) {
        CustomWorkoutModel model = getItem(position);
        if(model != null){
            holder.name_view.setText(model.getName());
            holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //onCallBack.onButtonDeleteClick(list.get(position));
                    if(onCallBack != null){
                        onCallBack.onDelete(model);
                    }
                }
            });
            holder.edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //onCallBack.onButtonDeleteClick(list.get(position));
                    if(onCallBack != null){
                        onCallBack.onEdit(model);
                    }
                }
            });
            holder.go_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //onCallBack.onButtonDeleteClick(list.get(position));
                    if(onCallBack != null){
                        onCallBack.onGo(model);
                    }
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    private CustomWorkoutModel getItem(int position){
        return list.get(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView edit_btn;
        private final ImageView delete_btn;
        private final ImageView go_btn;
        private final TextView name_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edit_btn = itemView.findViewById(R.id.edit_btn);
            delete_btn = itemView.findViewById(R.id.delete_btn);
            go_btn = itemView.findViewById(R.id.go_btn);
            name_view = itemView.findViewById(R.id.name);
        }
    }
    public interface OnCallBack{
        void onEdit(CustomWorkoutModel model);
        void onDelete(CustomWorkoutModel model);
        void onGo(CustomWorkoutModel model);
    }
}