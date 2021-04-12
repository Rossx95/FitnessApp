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
import com.fitnesspro.thefitnessapp.Dialog.WorkoutCreateDialog;
import com.fitnesspro.thefitnessapp.Dialog.WorkoutUpdateDialog;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.Adapter.CustomWorkoutAdapter;
import com.fitnesspro.thefitnessapp.models.CustomWorkoutModel;
import com.fitnesspro.thefitnessapp.models.Params;
import com.fitnesspro.thefitnessapp.models.WorkoutDetailModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutActivity extends BaseActivity {
    ImageView back_btn;
    SwipeRefreshLayout refresh;
    FloatingActionButton add_btn;
    RecyclerView workout_list_view;
    DatabaseReference myRef;
    List<CustomWorkoutModel> data_list;
    CustomWorkoutAdapter adapter;
    ValueEventListener add_list_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        initView();
    }
    private void initView(){
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
        workout_list_view = findViewById(R.id.workout_list);
        workout_list_view.setHasFixedSize(true);
        //set layout as LinearLayout
        workout_list_view.setLayoutManager(mLayoutManager);
        add_btn = findViewById(R.id.add_btn);
        add_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);

        data_list = new ArrayList<>();
        adapter = new CustomWorkoutAdapter(context, data_list);
        adapter.setOnCallBack(new CustomWorkoutAdapter.OnCallBack() {
            @Override
            public void onEdit(CustomWorkoutModel model) {
                WorkoutUpdateDialog dlg = new WorkoutUpdateDialog();
                dlg.setListener(new WorkoutUpdateDialog.Listener() {
                    @Override
                    public void onUpdate(String name) {
                        updateWorkout(model, name);
                    }
                });
                dlg.show(getSupportFragmentManager(), "WORK_OUT_UPDATE");
            }
            @Override
            public void onDelete(CustomWorkoutModel model) {
                removeWorkout(model);
            }
            @Override
            public void onGo(CustomWorkoutModel model) {
                Intent intent = new Intent(context, WorkoutDetailActivity.class);
                intent.putExtra("model", model);
                startActivity(intent);
            }
        });
        workout_list_view.setAdapter(adapter);
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
        WorkoutCreateDialog dlg = new WorkoutCreateDialog();
        dlg.setListener(new WorkoutCreateDialog.Listener() {
            @Override
            public void onCreate(String name) {
                createCustomWorkout(name);
            }
        });
        dlg.show(getSupportFragmentManager(), "WORK_OUT");
    }
    private void fetch() {
        data_list.clear();
        if(myRef == null){
            myRef = FirebaseDatabase.getInstance().getReference().child(Params.WORKOUT_KEY).child(auth.getCurrentUser().getUid());
        }
        refresh.setRefreshing(true);
        add_list_listener = new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
                refresh.setRefreshing(false);
            }
            public void onCancelled(DatabaseError databaseError) {
                adapter.notifyDataSetChanged();
                refresh.setRefreshing(false);
            }
        };
        myRef.addListenerForSingleValueEvent(add_list_listener);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try{
                    CustomWorkoutModel model = dataSnapshot.getValue(CustomWorkoutModel.class);
                    data_list.add(model);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                CustomWorkoutModel model = dataSnapshot.getValue(CustomWorkoutModel.class);
                int index = data_list.indexOf(model);
                if(index != -1){
                    data_list.set(index, model);
                }else{
                    data_list.add(model);
                }
                adapter.notifyDataSetChanged();
            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try{
                    CustomWorkoutModel model = dataSnapshot.getValue(CustomWorkoutModel.class);
                    data_list.remove(model);
                    adapter.notifyDataSetChanged();
                }catch (Exception e){

                }
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {
                refresh.setRefreshing(false);
            }
        });
    }
    public void createCustomWorkout(String name){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.WORKOUT_KEY).child(auth.getCurrentUser().getUid()).push();
        String key = ref.getKey();
        CustomWorkoutModel model = new CustomWorkoutModel();
        model.setName(name);
        model.setId(key);
        refresh.setRefreshing(true);
        ref.setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                adapter.notifyDataSetChanged();
                showMessage("Success to create workout !");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Fail to create workout !");
            }
        });
    }
    public void removeWorkout(CustomWorkoutModel model){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.WORKOUT_KEY).child(auth.getCurrentUser().getUid()).child(model.getId());
        refresh.setRefreshing(true);
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                showMessage("Success to delete workout !");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Fail to delete workout !");
            }
        });
    }
    public void updateWorkout(CustomWorkoutModel model, String name){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.WORKOUT_KEY).child(auth.getCurrentUser().getUid())
                .child(model.getId());
        Map<String, Object> update_map = new HashMap<>();
        update_map.put("name", name);

        refresh.setRefreshing(true);

        ref.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                showMessage("Success to update !");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Fail to update !");
            }
        });
    }
}