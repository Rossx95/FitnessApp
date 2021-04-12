package com.fitnesspro.thefitnessapp.Activities.History;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.Adapter.HistoryAdapter;
import com.fitnesspro.thefitnessapp.models.HistoryModel;
import com.fitnesspro.thefitnessapp.models.Params;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {

    ImageView back_btn;
    SwipeRefreshLayout refresh;
    RecyclerView history_list_view;
    List<HistoryModel> data_list;
    HistoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
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
        history_list_view = findViewById(R.id.history_list);
        history_list_view.setHasFixedSize(true);
        //set layout as LinearLayout
        history_list_view.setLayoutManager(mLayoutManager);

        back_btn.setOnClickListener(this);

        data_list = new ArrayList<>();
        adapter = new HistoryAdapter(context, data_list);
        adapter.setOnCallBack(new HistoryAdapter.OnCallBack() {
            @Override
            public void onGo(HistoryModel model) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(model.getLink())));
            }
        });
        history_list_view.setAdapter(adapter);

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
    private void fetch(){
        data_list.clear();
        refresh.setRefreshing(true);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child(Params.HISTORY_KEY).child(auth.getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refresh.setRefreshing(false);
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    HistoryModel item = d.getValue(HistoryModel.class);
                    data_list.add(item);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage("Fail to load history !");
                refresh.setRefreshing(false);
            }
        });
    }
}