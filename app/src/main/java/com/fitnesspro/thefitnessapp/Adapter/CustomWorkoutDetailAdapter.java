package com.fitnesspro.thefitnessapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.WorkoutDetailModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomWorkoutDetailAdapter extends RecyclerView.Adapter<CustomWorkoutDetailAdapter.ViewHolder> {
    private Context context;
    private List<WorkoutDetailModel> list;
    private CustomWorkoutDetailAdapter.OnCallBack onCallBack;
    private String user_id;

    public CustomWorkoutDetailAdapter(Context context, List<WorkoutDetailModel> list){
        this.context = context;
        this.list = list;
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void setOnCallBack(CustomWorkoutDetailAdapter.OnCallBack onCallBack) {
        this.onCallBack = onCallBack;
    }

    @NonNull
    @Override
    public CustomWorkoutDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_custom_workout_detail_item,parent,false);
        return new CustomWorkoutDetailAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CustomWorkoutDetailAdapter.ViewHolder holder, int position) {
        WorkoutDetailModel model = getItem(position);
        if(model != null){
            Glide.with(context)
                    .load(model.getExe().getImage())
                    .into(holder.exe_image);
            holder.exe_name.setText(model.getExe().getTitle());
            holder.weight_view.setText(model.getWeight());
            holder.reps_view.setText(model.getReps());

            holder.remove_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onCallBack != null){
                        onCallBack.onDelete(model);
                    }
                }
            });
            holder.workout_area.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
    private WorkoutDetailModel getItem(int position){
        return list.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout workout_area;
        private final ImageView remove_btn;
        private final ImageView exe_image;
        private final TextView exe_name;
        private final TextView weight_view;
        private final TextView reps_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            workout_area = itemView.findViewById(R.id.workout_area);
            remove_btn = itemView.findViewById(R.id.remove_option);
            exe_image = itemView.findViewById(R.id.exe_image);
            exe_name = itemView.findViewById(R.id.exe_name);
            weight_view = itemView.findViewById(R.id.weight);
            reps_view = itemView.findViewById(R.id.reps);
        }
    }
    public interface OnCallBack{
        void onEdit(WorkoutDetailModel model);
        void onDelete(WorkoutDetailModel model);
        void onGo(WorkoutDetailModel model);
    }
}