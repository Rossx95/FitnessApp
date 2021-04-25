package com.fitnesspro.thefitnessapp.Activities.CommunityWorkout;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fitnesspro.thefitnessapp.Adapter.ReviewAdapter;
import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.Dialog.WorkoutUpdateDialog;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.CommunityWorkoutModel;
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
    //Initialising Variables
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
        //set the base view for the activity
        setContentView(R.layout.activity_review);
        //call initView method
        initView();
    }
    private void initView(){
        //initialise variables to features on the page
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
        //Loads the items, newest first.
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        //Setting RecyclerView variable to RecyclerView in xml
        review_list_view = findViewById(R.id.review_list);
        review_list_view.setHasFixedSize(true);
        //set layout as LinearLayout
        review_list_view.setLayoutManager(mLayoutManager);
        //Set on click listener for buttons to pick up onClick method
        back_btn.setOnClickListener(this);
        //Instantiating a new array list
        data_list = new ArrayList<>();
        //Instantiating a new adapter using the new array list
        adapter = new ReviewAdapter(context, data_list);
        //Setting on callback methods for the adapter using each button
        adapter.setOnCallBack(new ReviewAdapter.OnCallBack() {
            //A method to allow an admin to edit the review comment and call the update method
            @Override
            public void onEdit(ReviewModel model) {
                refresh.setRefreshing(false);
                //Setting the new review comment
                String name = "Comment deemed inappropriate by admin";
                //Updating the comment by using the updateReview method
                updateReview(model, name);
                //updating the list displayed
                adapter.notifyDataSetChanged();
            }
            //method to delete the review
            @Override
            public void onDelete(ReviewModel model) {
                removeReview(model);
                //updating the list displayed
                adapter.notifyDataSetChanged();
            }
        });
        //setting the RecyclerView to use the adapter
        review_list_view.setAdapter(adapter);
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
    private void read() {
        //clearing the items in the array list
        data_list.clear();
        if(myRef == null){
            myRef = FirebaseDatabase.getInstance().getReference().child(Params.REVIEW_KEY);
        }
        //Creating new query to order by workout id and check if it is equal to the community
        //workouts id
        Query query = myRef.orderByChild("workout_id").equalTo(com_workout_id);
        //refresh the screen
        refresh.setRefreshing(true);
        //creating new listener
        add_list_listener = new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                //calls method to update the recycler view on change of an item
                adapter.notifyDataSetChanged();
                refresh.setRefreshing(false);
            }
            public void onCancelled(DatabaseError databaseError) {
                //calls method to update the recycler view on change of an item
                adapter.notifyDataSetChanged();
                refresh.setRefreshing(false);
            }
        };
        //adding the single event listener to the query
        query.addListenerForSingleValueEvent(add_list_listener);
        //addChildlistener to assess any changes to the child level of the referenced location
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
                }
                else{
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
    //A method to update the specified review
    public void updateReview(ReviewModel model, String description){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.REVIEW_KEY).child(model.getId());
        Map<String, Object> update_map = new HashMap<>();
        //insert mapping reference of description into the hashmap
        update_map.put("description", description);
        refresh.setRefreshing(true);
        //updating the description field of reviews with the users input name
        //and adding a success listener to tell the user if the process was completed
        ref.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                showMessage("Successfully updated review!");
            }//adding a failure listener to tell the user if the process has failed to complete
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Failed to update review!");
            }
        });
    }
    //A method to allow a review to be removed
    public void removeReview(ReviewModel model){
        //Database reference locating to the reviews collection > review id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.REVIEW_KEY).child(model.getId());
        //remove the review, if successful the addOnSuccess listener will notify the user
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                showMessage("Successfully removed review!");
                adapter.notifyDataSetChanged();
            }//remove the review, if the process failed the Failure listener will notify the user
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Failed to remove this review!");
            }
        });
        //updating the list for the user
        adapter.notifyDataSetChanged();
    }
}
