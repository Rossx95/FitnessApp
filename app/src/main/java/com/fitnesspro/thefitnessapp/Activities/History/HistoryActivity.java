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
    //Initialising Variables
    ImageView back_btn;
    SwipeRefreshLayout refresh;
    RecyclerView history_list_view;
    List<HistoryModel> data_list;
    HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the base view for the activity
        setContentView(R.layout.activity_history);
        //call initView method
        initView();
    }
    private void initView(){
        //initialise variables to features on the page
        back_btn = findViewById(R.id.back);
        refresh = findViewById(R.id.refresh);
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
        history_list_view = findViewById(R.id.history_list);
        history_list_view.setHasFixedSize(true);
        //set layout as LinearLayout
        history_list_view.setLayoutManager(mLayoutManager);
        //Set on click listener for buttons to pick up onClick method
        back_btn.setOnClickListener(this);
        //Instantiating a new array list
        data_list = new ArrayList<>();
        //Instantiating a new adapter using the new array list
        adapter = new HistoryAdapter(context, data_list);
        //Setting on callback methods for the adapter using each button
        adapter.setOnCallBack(new HistoryAdapter.OnCallBack() {
            //Method to open google calendar based on user logged in
            @Override
            public void onGo(HistoryModel model) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(model.getLink())));
            }
        });
        //setting the RecyclerView to use the adapter
        history_list_view.setAdapter(adapter);
        //calling the read method to read the review data from firebase
        read();
    }
    //method to set an event on click of back button
    @Override
    public void onClick(View view) {
        super.onClick(view);
        //if user clicks back, finish the current activity and return to previous screen
        if(view == back_btn){
            finish();
        }
    }
    //Method to return to applications previous screen
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    //A method to read the database and fetch relevant data from the required reference
    private void read(){
        //clearing the items in the array list
        data_list.clear();
        refresh.setRefreshing(true);
        //Database reference to the history collection, finding the current folder with the
        //current users id
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child(Params.HISTORY_KEY).child(auth.getCurrentUser().getUid());
        //getting all children found within the history collection of the users id
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refresh.setRefreshing(false);
                //for loop snapshot to cycle through each item in the collection for the users id
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    HistoryModel item = d.getValue(HistoryModel.class);
                    //adding history item to the list as its found
                    data_list.add(item);
                }
                //updating the list for the user
                adapter.notifyDataSetChanged();
            }//if the process failed, display the following message
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage("Failed to load your history!");
                refresh.setRefreshing(false);
            }
        });
    }
}