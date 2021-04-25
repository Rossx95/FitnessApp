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
import com.fitnesspro.thefitnessapp.models.ReviewModel;
import com.fitnesspro.thefitnessapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private Context context;
    private List<ReviewModel> list;
    private ReviewAdapter.OnCallBack onCallBack;
    private String user_id;

    public ReviewAdapter(Context context, List<ReviewModel> list){
        this.context = context;
        this.list = list;
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void setOnCallBack(ReviewAdapter.OnCallBack onCallBack) {
        this.onCallBack = onCallBack;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_review_item,parent,false);
        return new ReviewAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        ReviewModel model = getItem(position);
        if(model != null){
            if(!Params.ADMIN){
                if(!model.getUserid().equals(user_id)){
                    holder.delete_btn.setVisibility(View.INVISIBLE);
                   // holder.edit_btn.setVisibility(View.INVISIBLE);
                }else{
                    holder.delete_btn.setVisibility(View.INVISIBLE);
                   // holder.edit_btn.setVisibility(View.VISIBLE);
                }
            }
            else{
                holder.delete_btn.setVisibility(View.VISIBLE);
                //holder.edit_btn.setVisibility(View.VISIBLE);
            }
            if(!Params.ADMIN){
                if(!model.getUserid().equals(user_id)){
                    holder.edit_btn.setVisibility(View.INVISIBLE);
                    // holder.edit_btn.setVisibility(View.INVISIBLE);
                }else{
                    holder.edit_btn.setVisibility(View.INVISIBLE);
                    // holder.edit_btn.setVisibility(View.VISIBLE);
                }
            }
            else{
                holder.edit_btn.setVisibility(View.VISIBLE);
                //holder.edit_btn.setVisibility(View.VISIBLE);
            }
            holder.description_view.setText(model.getDescription());
            holder.rating_bar.setRating(model.getRate());
            holder.review.setText(String.format(Locale.getDefault(), "%.1f", model.getRate()));
            holder.userID.setText("Posted by: " + model.getUserid());

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
        }

    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    private ReviewModel getItem(int position){
        return list.get(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView description_view;
        private final TextView review;
        private final RatingBar rating_bar;
        private final ImageView delete_btn;
        private final ImageView edit_btn;
        private final TextView userID;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description_view = itemView.findViewById(R.id.description);
            delete_btn = itemView.findViewById(R.id.delete_btn);
            edit_btn = itemView.findViewById(R.id.edit_btn);
            review = itemView.findViewById(R.id.review);
            rating_bar = itemView.findViewById(R.id.rating);
            userID = itemView.findViewById(R.id.reviewuserID);
        }
    }
    public interface OnCallBack{
        void onDelete(ReviewModel model);
        void onEdit(ReviewModel model);
    }
}
