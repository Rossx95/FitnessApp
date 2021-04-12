package com.fitnesspro.thefitnessapp.Activities.CommunityWorkout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fitnesspro.thefitnessapp.Adapter.ReviewAdapter;
import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.Params;
import com.fitnesspro.thefitnessapp.models.ReviewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
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

public class ReviewView extends BaseActivity {

    ImageView back_btn;
    SwipeRefreshLayout refresh;
    RecyclerView review_list_view;
    DatabaseReference myRef;
    List<ReviewModel> data_list;
    ReviewAdapter adapter;
    ValueEventListener add_list_listener;

    String com_workout_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        initView();
    }
    private void initView(){
        com_workout_id = getIntent().getStringExtra("id");
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
        review_list_view = findViewById(R.id.review_list);
        review_list_view.setHasFixedSize(true);
        //set layout as LinearLayout
        review_list_view.setLayoutManager(mLayoutManager);
        back_btn.setOnClickListener(this);

        data_list = new ArrayList<>();
        adapter = new ReviewAdapter(context, data_list);
        review_list_view.setAdapter(adapter);
        fetch();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == back_btn){
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void fetch() {
        data_list.clear();
        if(myRef == null){
            myRef = FirebaseDatabase.getInstance().getReference().child(Params.REVIEW_KEY);
        }
        Query query = myRef.orderByChild("workout_id").equalTo(com_workout_id);

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
        query.addListenerForSingleValueEvent(add_list_listener);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try{
                    ReviewModel model = dataSnapshot.getValue(ReviewModel.class);
                    data_list.add(model);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ReviewModel model = dataSnapshot.getValue(ReviewModel.class);
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
                    ReviewModel model = dataSnapshot.getValue(ReviewModel.class);
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
}
