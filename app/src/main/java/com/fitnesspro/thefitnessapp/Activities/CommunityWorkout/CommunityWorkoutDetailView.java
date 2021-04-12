package com.fitnesspro.thefitnessapp.Activities.CommunityWorkout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fitnesspro.thefitnessapp.Activities.Workout.WorkoutExeActivity;
import com.fitnesspro.thefitnessapp.Adapter.CustomWorkoutDetailAdapter;
import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.Dialog.ReviewDialog;
import com.fitnesspro.thefitnessapp.Dialog.WorkoutAddDialog;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.CommunityWorkoutModel;
import com.fitnesspro.thefitnessapp.models.Params;
import com.fitnesspro.thefitnessapp.models.ReviewModel;
import com.fitnesspro.thefitnessapp.models.WorkoutDetailModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CommunityWorkoutDetailView extends BaseActivity {
    CommunityWorkoutModel model;

    ImageView back_btn;
    ImageView review_btn;
    SwipeRefreshLayout refresh;
    FloatingActionButton add_btn;
    RecyclerView detail_list_view;
    List<WorkoutDetailModel> data_list;
    CustomWorkoutDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_workout_detail);
        initView();
    }
    private void initView(){
        model = (CommunityWorkoutModel)getIntent().getSerializableExtra("model");

        back_btn = findViewById(R.id.back);
        review_btn = findViewById(R.id.review_btn);
        refresh = findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        //this will load the items from bottom means newest first
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        //RecyclerView
        detail_list_view = findViewById(R.id.detail_list);
        detail_list_view.setHasFixedSize(true);
        //set layout as LinearLayout
        detail_list_view.setLayoutManager(mLayoutManager);
        add_btn = findViewById(R.id.add_btn);

        review_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);

        data_list = new ArrayList<>();
        adapter = new CustomWorkoutDetailAdapter(context, data_list);
        adapter.setOnCallBack(new CustomWorkoutDetailAdapter.OnCallBack() {
            @Override
            public void onEdit(WorkoutDetailModel model) {
            }
            @Override
            public void onDelete(WorkoutDetailModel model) {
                removeWorkout(model);
            }
            @Override
            public void onGo(WorkoutDetailModel model) {
                Intent intent = new Intent(context, WorkoutExeActivity.class);
                intent.putExtra("model", model);
                startActivity(intent);
            }
        });
        detail_list_view.setAdapter(adapter);

        fetch();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == add_btn){
            showCreateDialog();
        }else if(view == back_btn){
            finish();
        }else if(view == review_btn){
            if(model.getUser_id().equals(auth.getCurrentUser().getUid())){
                showMessage("This is created by you !");
                return;
            }
            ReviewDialog dlg = new ReviewDialog();
            dlg.setListener(new ReviewDialog.Listener() {
                @Override
                public void onReview(float rate, String description) {
                    review(rate, description);
                }
            });
            dlg.show(getSupportFragmentManager(), "REVIEW");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void showCreateDialog(){
        WorkoutAddDialog dlg = new WorkoutAddDialog();
        dlg.setListener(new WorkoutAddDialog.Listener() {
            @Override
            public void onCreate(WorkoutDetailModel model) {
                addDetail(model);
            }
        });
        dlg.show(getSupportFragmentManager(), "WORK_OUT_ADD");
    }
    private void fetch() {
        data_list.clear();
        data_list.addAll(model.getDetail_list());
    }
    public void addDetail(WorkoutDetailModel detail_model){
        model.getDetail_list().add(detail_model);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.COMWORKOUT_KEY).child(model.getId());
        Map<String, Object> update_map = new HashMap<>();
        update_map.put("detail_list", model.getDetail_list());

        refresh.setRefreshing(true);

        ref.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                showMessage("Success to add !");
                data_list.add(detail_model);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Fail to add !");
            }
        });
    }
    public void removeWorkout(WorkoutDetailModel detail_model){
        model.getDetail_list().remove(detail_model);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.COMWORKOUT_KEY).child(model.getId());
        Map<String, Object> update_map = new HashMap<>();
        update_map.put("detail_list", model.getDetail_list());

        refresh.setRefreshing(true);

        ref.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                showMessage("Success to remove !");
                data_list.remove(detail_model);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Fail to remove !");
            }
        });
    }
    //review
    private void review(final float rate, final String description){
        String identify = auth.getCurrentUser().getUid() + "-" + model.getId();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Params.REVIEW_KEY);
        Query query = myRef.orderByChild("identify").equalTo(identify);
        refresh.setRefreshing(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refresh.setRefreshing(false);
                boolean exist = false;
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    exist = true;
                    break;
                }
                if(!exist){
                    //review now
                    post_review(rate, description);
                }else{
                    showMessage("You already reviewd to this workout !");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                refresh.setRefreshing(false);
                showMessage("Fail to review now !");
            }
        });
    }
    private void post_review(float rate, String description){
        ReviewModel review_model = new ReviewModel();
        String identify = auth.getCurrentUser().getUid() + "-" + model.getId();
        review_model.setIdentify(identify);
        review_model.setRate(rate);
        review_model.setWorkout_id(model.getId());
        review_model.setDescription(description);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Params.REVIEW_KEY).push();
        review_model.setId(myRef.getKey());
        myRef.setValue(review_model).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                update_review(rate);
                showMessage("Success to post review !");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Fail to post review !");
            }
        });
    }
    private void update_review(float rate){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Params.COMWORKOUT_KEY).child(model.getId());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refresh.setRefreshing(false);
                CommunityWorkoutModel com_model = dataSnapshot.getValue(CommunityWorkoutModel.class);
                int review_num = com_model.getReview_num() + 1;
                float review_rate = com_model.getRate() + rate;
                update_review(review_num, review_rate);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                refresh.setRefreshing(false);
                showMessage("Fail to review now !");
            }
        });
    }
    public void update_review(int review_num, float rate){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.COMWORKOUT_KEY).child(model.getId());
        Map<String, Object> update_map = new HashMap<>();
        update_map.put("review_num", review_num);
        update_map.put("rate", rate);

        ref.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                showMessage("Success to post review !");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Fail to post review !");
            }
        });
    }
}
