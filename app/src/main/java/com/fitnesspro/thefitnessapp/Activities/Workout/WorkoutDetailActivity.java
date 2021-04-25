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
    //Initialising Variables
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
        //set the base view for the activity
        setContentView(R.layout.activity_workout_detail);
        //call initView method
        initView();
    }
    private void initView(){
        //Setting model to an object from a different model to be able to access getters and setters
        model = (CustomWorkoutModel)getIntent().getSerializableExtra("model");
        //initialise variables to features on the page
        back_btn = findViewById(R.id.back);
        refresh = findViewById(R.id.refresh);
        add_btn = findViewById(R.id.add_btn);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        //Loads the items, newest first.
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        //Setting RecyclerView variable to RecyclerView in xml
        detail_list_view = findViewById(R.id.detail_list);
        detail_list_view.setHasFixedSize(true);
        //set layout as LinearLayout
        detail_list_view.setLayoutManager(mLayoutManager);
        //Set on click listener for buttons to pick up onClick method
        add_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        //Instantiating a new array list
        data_list = new ArrayList<>();
        //Instantiating a new adapter using the new array list
        adapter = new CustomWorkoutDetailAdapter(context, data_list);
        //Setting on callback methods for the adapter using each button
        adapter.setOnCallBack(new CustomWorkoutDetailAdapter.OnCallBack() {
            //onEdit unused method as users cannot edit exercises within a workout
            @Override
            public void onEdit(WorkoutDetailModel model) {
            }
            //method to remove exercises within workout
            @Override
            public void onDelete(WorkoutDetailModel model) {
                removeExercise(model);
            }
            //method to view the exercises within workout
            @Override
            public void onGo(WorkoutDetailModel model) {
                Intent intent = new Intent(context, WorkoutExeActivity.class);
                intent.putExtra("model", model);
                startActivity(intent);
            }
        });
        //setting the RecyclerView to use the adapter
        detail_list_view.setAdapter(adapter);
        //calling the read method to read the workout data from firebase
        read();
    }
    //method to set an event on click of add/back button
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
                addExercise(model);
            }
        });
        dlg.show(getSupportFragmentManager(), "WORK_OUT_ADD");
    }
    //method to read the database and fetch the exercises
    private void read() {
        //clear the array list
        data_list.clear();
        //add the exercises within the model workout to the array list
        data_list.addAll(model.getDetail_list());
    }
    //Method to add an exercise to the workout
    public void addExercise(WorkoutDetailModel detail_model){
        //get the current workout exercise list, and add the new exercise
        model.getDetail_list().add(detail_model);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.WORKOUT_KEY).child(auth.getCurrentUser().getUid())
                .child(model.getId());
        //Instantiating an object of type HashMap
        Map<String, Object> update_map = new HashMap<>();
        //updating the list of exercises with the users input exercise
        //and adding a success listener to tell the user if the process was completed
        update_map.put("detail_list", model.getDetail_list());
        refresh.setRefreshing(true);
        ref.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                showMessage("Successfully added exercise!");
                data_list.add(detail_model);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Failed to add exercise!");
            }
        });
    }
    //Method to remove an exercise from the workout
    public void removeExercise(WorkoutDetailModel detail_model){
        //get the current workout exercise list, and remove the selected exercise
        model.getDetail_list().remove(detail_model);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.WORKOUT_KEY).child(auth.getCurrentUser().getUid()).child(model.getId());
        //Instantiating an object of type HashMap
        Map<String, Object> update_map = new HashMap<>();
        //updating the list of exercises
        //and adding a success listener to tell the user if the process was completed
        update_map.put("detail_list", model.getDetail_list());
        refresh.setRefreshing(true);
        ref.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                showMessage("Successfully removed exercise!");
                data_list.remove(detail_model);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Failed to remove exercise!");
            }
        });
    }
}