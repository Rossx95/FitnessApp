package com.fitnesspro.thefitnessapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.HistoryModel;
import com.fitnesspro.thefitnessapp.models.WorkoutDetailModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context context;
    private List<HistoryModel> list;
    private HistoryAdapter.OnCallBack onCallBack;

    public HistoryAdapter(Context context, List<HistoryModel> list){
        this.context = context;
        this.list = list;
    }

    public void setOnCallBack(HistoryAdapter.OnCallBack onCallBack) {
        this.onCallBack = onCallBack;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_history_item,parent,false);
        return new HistoryAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        HistoryModel model = getItem(position);
        if(model != null){
            holder.summary_view.setText(model.getSummary());
            holder.weight_view.setText(model.getWeight());
            holder.reps_view.setText(model.getReps());
            holder.start_time_view.setText(model.getStart_time());
            holder.history_area.setOnClickListener(new View.OnClickListener() {
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
    private HistoryModel getItem(int position){
        return list.get(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout history_area;
        private final TextView summary_view;
        private final TextView start_time_view;
        private final TextView weight_view;
        private final TextView reps_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            history_area = itemView.findViewById(R.id.history_area);
            summary_view = itemView.findViewById(R.id.summary);
            start_time_view = itemView.findViewById(R.id.start_time);
            weight_view = itemView.findViewById(R.id.history_weight);
            reps_view = itemView.findViewById(R.id.history_reps);
        }
    }
    public interface OnCallBack{
        void onGo(HistoryModel model);
    }
}