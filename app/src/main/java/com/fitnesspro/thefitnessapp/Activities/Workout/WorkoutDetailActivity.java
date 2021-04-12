package com.fitnesspro.thefitnessapp.Activities.Workout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.Dialog.WorkoutAddDialog;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.Adapter.CustomWorkoutDetailAdapter;
import com.fitnesspro.thefitnessapp.models.WorkoutDetailModel;
import com.fitnesspro.thefitnessapp.models.CustomWorkoutModel;
import com.fitnesspro.thefitnessapp.models.Params;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutDetailActivity extends BaseActivity {
    CustomWorkoutModel model;

    ImageView back_btn;
    SwipeRefreshLayout refresh;
    FloatingActionButton add_btn;
    RecyclerView detail_list_view;
    List<WorkoutDetailModel> data_list;
    CustomWorkoutDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);
        initView();
    }
    private void initView(){
        model = (CustomWorkoutModel)getIntent().getSerializableExtra("model");

        back_btn = findViewById(R.id.back);
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.WORKOUT_KEY).child(auth.getCurrentUser().getUid())
                .child(model.getId());
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.WORKOUT_KEY).child(auth.getCurrentUser().getUid())
                .child(model.getId());
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
}