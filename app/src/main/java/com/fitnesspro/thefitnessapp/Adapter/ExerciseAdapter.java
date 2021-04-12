package com.fitnesspro.thefitnessapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.ExerciseModel;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private Context context;
    private List<ExerciseModel> list;
    private OnCallBack onCallBack;

    public ExerciseAdapter(Context context, List<ExerciseModel> list){
        this.context = context;
        this.list = list;
    }

    public void setOnCallBack(OnCallBack onCallBack) {
        this.onCallBack = onCallBack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_exe_item,parent,false);
        return new ExerciseAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseModel model = getItem(position);
        if(model != null){
            Glide.with(context)
                    .load(model.getImage())
                    .into(holder.exe_image_view);
            holder.title_view.setText(model.getTitle());
            holder.exe_area.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //onCallBack.onButtonDeleteClick(list.get(position));
                    if(onCallBack != null){
                        onCallBack.onDetail(model);
                    }
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    private ExerciseModel getItem(int position){
        return list.get(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout exe_area;
        private ImageView exe_image_view;
        private TextView title_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exe_area = itemView.findViewById(R.id.exe_area);
            exe_image_view = itemView.findViewById(R.id.exe_image);
            title_view = itemView.findViewById(R.id.title_view);
        }

    }
    public interface OnCallBack{
        void onDetail(ExerciseModel exercise);
    }
}
