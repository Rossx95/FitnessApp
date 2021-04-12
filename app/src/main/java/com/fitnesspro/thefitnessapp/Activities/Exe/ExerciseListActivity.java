package com.fitnesspro.thefitnessapp.Activities.Exe;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.Adapter.ExerciseAdapter;
import com.fitnesspro.thefitnessapp.models.ExerciseModel;
import com.fitnesspro.thefitnessapp.models.Params;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListActivity extends BaseActivity {
    ImageView back_btn;
    SwipeRefreshLayout refresh;
    RecyclerView exe_list_view;
    ExerciseAdapter adapter;
    List<ExerciseModel> data_list;

    private ValueEventListener add_list_listener;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        initView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void initView(){
        back_btn = findViewById(R.id.back);
        refresh = findViewById(R.id.refresh);
        exe_list_view = findViewById(R.id.exe_list);
        exe_list_view.setLayoutManager(new LinearLayoutManager(context));

        data_list = new ArrayList<>();
        adapter = new ExerciseAdapter(context, data_list);
        adapter.setOnCallBack(new ExerciseAdapter.OnCallBack() {
            @Override
            public void onDetail(ExerciseModel exercise) {
                Intent intent = new Intent(context, ExerciseDetailView.class);
                intent.putExtra("model", exercise);
                startActivity(intent);
            }
        });
        exe_list_view.setAdapter(adapter);

        back_btn.setOnClickListener(this);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
              @Override
              public void onRefresh() {
                  refresh.setRefreshing(false);
              }
        });
        load();
    }
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == back_btn){
            finish();
        }
    }
    private void load(){
        data_list.clear();
        if(myRef == null){
            myRef = FirebaseDatabase.getInstance().getReference(Params.EXE_KEY);
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
                    ExerciseModel model = dataSnapshot.getValue(ExerciseModel.class);
                    data_list.add(model);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ExerciseModel model = dataSnapshot.getValue(ExerciseModel.class);
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
                    ExerciseModel model = dataSnapshot.getValue(ExerciseModel.class);
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