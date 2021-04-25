package com.fitnesspro.thefitnessapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.CommunityWorkoutModel;
import com.fitnesspro.thefitnessapp.models.Params;
import com.fitnesspro.thefitnessapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommunityWorkoutAdapter extends RecyclerView.Adapter<CommunityWorkoutAdapter.ViewHolder> {
    private Context context;
    private List<CommunityWorkoutModel> list;
    private CommunityWorkoutAdapter.OnCallBack onCallBack;
    private String user_id;
    FirebaseAuth fauth;
    private String test;

    public CommunityWorkoutAdapter(Context context, List<CommunityWorkoutModel> list){
        this.context = context;
        this.list = list;
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void setOnCallBack(CommunityWorkoutAdapter.OnCallBack onCallBack) {
        this.onCallBack = onCallBack;
    }

    @NonNull
    @Override
    public CommunityWorkoutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_community_workout,parent,false);
        return new CommunityWorkoutAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CommunityWorkoutAdapter.ViewHolder holder, int position) {
        CommunityWorkoutModel model = getItem(position);
        if(model != null){
            holder.name_view.setText(model.getName());
            if(!Params.ADMIN){
                if(!model.getUser_id().equals(user_id)){
                    holder.delete_btn.setVisibility(View.INVISIBLE);
                    holder.edit_btn.setVisibility(View.INVISIBLE);
                }else{
                    holder.delete_btn.setVisibility(View.VISIBLE);
                    holder.edit_btn.setVisibility(View.VISIBLE);
                }
            }
            else{
                holder.delete_btn.setVisibility(View.VISIBLE);
                holder.edit_btn.setVisibility(View.VISIBLE);
            }

            float rating = model.getRate();
            int review_num = model.getReview_num();
            holder.review_num.setText(review_num + " review(s)");
            holder.userID.setText("Posted by: "+ model.getEmail());
            if(review_num > 0){
                float rating_val = rating / review_num;
                holder.rating_bar.setRating(rating_val);
                holder.review.setText(String.format(Locale.getDefault(), "%.1f", rating_val));
            }else{
                holder.review.setText("0.0");
            }
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

            holder.review_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //onCallBack.onButtonDeleteClick(list.get(position));
                    if(onCallBack != null){
                        onCallBack.onReview(model);
                    }
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    private CommunityWorkoutModel getItem(int position){
        return list.get(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView edit_btn;
        private final ImageView delete_btn;
        private final ImageView go_btn;
        private final ImageView review_btn;
        private final TextView name_view;
        private final TextView review_num;
        private final TextView review;
        private final RatingBar rating_bar;
        private final TextView userID;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edit_btn = itemView.findViewById(R.id.edit_btn);
            delete_btn = itemView.findViewById(R.id.delete_btn);
            go_btn = itemView.findViewById(R.id.go_btn);
            review_btn = itemView.findViewById(R.id.review_btn);
            name_view = itemView.findViewById(R.id.name);
            review_num = itemView.findViewById(R.id.review_num);
            review = itemView.findViewById(R.id.review);
            rating_bar = itemView.findViewById(R.id.rating);
            userID = itemView.findViewById(R.id.WorkoutuserID);
        }
    }
    public interface OnCallBack{
        void onEdit(CommunityWorkoutModel model);
        void onDelete(CommunityWorkoutModel model);
        void onGo(CommunityWorkoutModel model);
        void onReview(CommunityWorkoutModel model);
    }
}