package com.fitnesspro.thefitnessapp.Activities.CommunityWorkout;

import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.fitnesspro.thefitnessapp.Activities.Workout.WorkoutExeActivity;
import com.fitnesspro.thefitnessapp.Adapter.CustomWorkoutDetailAdapter;
import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.Dialog.ReviewDialog;
import com.fitnesspro.thefitnessapp.Dialog.WorkoutAddDialog;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.CommunityWorkoutModel;
import com.fitnesspro.thefitnessapp.models.ExerciseModel;
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
    //Initialising Variables
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
        //set the base view for the activity
        setContentView(R.layout.activity_community_workout_detail);
        //call initView method
        initView();
    }
    private void initView(){
        //Passing an object from one class to another
        model = (CommunityWorkoutModel)getIntent().getSerializableExtra("model");
        //initialise variables to features on the page
        back_btn = findViewById(R.id.back);
        review_btn = findViewById(R.id.review_btn);
        add_btn = findViewById(R.id.add_btn);
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
        detail_list_view = findViewById(R.id.detail_list);
        detail_list_view.setHasFixedSize(true);
        //set layout as LinearLayout
        detail_list_view.setLayoutManager(mLayoutManager);
        //validation to ensure that users can only add to a workout if they created it
        if(model.getUser_id().equals(auth.getCurrentUser().getUid()))
        {
            add_btn.setVisibility(View.VISIBLE);
        }
        else
        {
            add_btn.setVisibility(View.INVISIBLE);
        }
        //Set on click listener for buttons to pick up onClick method
        review_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        //Instantiating a new array list
        data_list = new ArrayList<>();
        //Instantiating a new adapter using the new array list
        adapter = new CustomWorkoutDetailAdapter(context, data_list);
        //Setting on callback methods for the adapter using each button
        adapter.setOnCallBack(new CustomWorkoutDetailAdapter.OnCallBack() {
            //method not required as the exercises cannot be edited within the workout
            @Override
            public void onEdit(WorkoutDetailModel model) {
            }
            //method to delete the exercise
            @Override
            public void onDelete(WorkoutDetailModel model) {
                removeExercise(model);
            }
            //method to view exercises selected to log it
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
    //method to set an event on click of add/back and review buttons
    @Override
    public void onClick(View view) {
        super.onClick(view);
        //if user clicks add, show the create dialog window
        if(view == add_btn){
            showCreateDialog();
        }
        //if user clicks back, finish the current activity and return to previous screen
        else if(view == back_btn){
            finish();
        }
        //if user clicks review button, check user ID, if they did not create it allow
        //them to enter a review through the review dialog window
        else if(view == review_btn){
            //check if current user is the user that created the current workout
            if(model.getUser_id().equals(auth.getCurrentUser().getUid())){
                //if current user is the same as the user who made the workout, show this message
                showMessage("This workout was created by you, you cannot review it!");
                return;
            }
            //if current user is not the creator of the workout, create a new dialog and allow
            //the user to enter their review information
            ReviewDialog dlg = new ReviewDialog();
            dlg.setListener(new ReviewDialog.Listener() {
                @Override
                public void onReview(float rate, String description, String userID) {
                    //call review method taking in the users input for the following variables
                    check_Review_Exists(rate, description, userID);
                }
            });
            //show review dialog window to the user
            dlg.show(getSupportFragmentManager(), "REVIEW");
        }
    }
    //Method to return to applications previous screen
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    //Method to display dialog box to add an exercise
    public void showCreateDialog(){
        WorkoutAddDialog dlg = new WorkoutAddDialog();
        dlg.setListener(new WorkoutAddDialog.Listener() {
            @Override
            public void onCreate(WorkoutDetailModel model) {
                //calling the add exercise method
                addExercise(model);
            }
        });
        //show add exercise dialog window to the user
        dlg.show(getSupportFragmentManager(), "WORK_OUT_ADD");
    }
    //A method to read the database and fetch relevant data from the required reference
    private void read() {
        data_list.clear();
        //get the list of exercises contained within the current workout and add them to data_list
        data_list.addAll(model.getExercises_list());
    }
    //A method to add an exercise to a workout
    public void addExercise(WorkoutDetailModel detail_model){
        //get the list of exercises contained within the current model
        //and add a new exercise to the workout selected
        model.getExercises_list().add(detail_model);
        //Database reference to the current community workout id > exercise id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.COMWORKOUT_KEY).child(model.getId());
        //Instantiating an object of type HashMap
        Map<String, Object> update_map = new HashMap<>();
        //insert mapping reference of Exercises_list into the hashmap
        update_map.put("Exercises_list", model.getExercises_list());
        refresh.setRefreshing(true);
        //updating the children of the workout with the users input exercise
        //and adding a success listener to tell the user if the process was completed
        ref.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                showMessage("Successfully added exercise!");
                data_list.add(detail_model);
                adapter.notifyDataSetChanged();
            }//adding a failure listener to tell the user if the process has failed to complete
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Failed to add exercise to your workout!");
            }
        });
    }
    //A method to remove an exercise from a workout
    public void removeExercise(WorkoutDetailModel detail_model){
        //checking if the current user is the same as the user who created the workout
        if(model.getUser_id().equals(auth.getCurrentUser().getUid())) {
            //getting the exercise list and removing the current exercise selected
            model.getExercises_list().remove(detail_model);
            //Database reference to the current community workout id
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.COMWORKOUT_KEY).child(model.getId());
            //Instantiating an object of type HashMap
            Map<String, Object> update_map = new HashMap<>();
            //insert mapping reference of Exercises_list into the hashmap
            update_map.put("Exercises_list", model.getExercises_list());
            refresh.setRefreshing(true);
            //removing the children of the workout using the users selected exercise
            //and adding a success listener to tell the user if the process was completed
            ref.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                public void onSuccess(Void aVoid) {
                    refresh.setRefreshing(false);
                    showMessage("Successfully removed exercise!");
                    data_list.remove(detail_model);
                    adapter.notifyDataSetChanged();
                }//adding a failure listener to tell the user if the process has failed to complete
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    refresh.setRefreshing(false);
                    showMessage("Failed to remove exercise from your workout!");
                }
            });
        }
        //if the user did not create the workout, this message is displayed
        else{
            showMessage("You didnt create this workout, you cannot modify it!");
        }
    }
    //Method to check if a user already has a review
    private void check_Review_Exists(final float rate, final String description, final String userID){
        //creating a variable set to the users id + the review id
        String review_check = auth.getCurrentUser().getUid() + "-" + model.getId();
        //Database reference locating to the reviews collection
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Params.REVIEW_KEY);
        //Setting a query to check the review_check field of each review
        Query query = myRef.orderByChild("review_check").equalTo(review_check);
        refresh.setRefreshing(true);
        //adding a single event listener and a data snapshot, if the query is found, the user will
        //not be able to post their review, if it does not exist, then the review will be posted
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refresh.setRefreshing(false);
                boolean exist = false;
                //if query ("review_check") does exist in the review collection, the review
                //is not posted and the query stops
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    exist = true;
                    break;
                }
                //if query ("review_check") does not exist in the review collection, the review
                //is posted
                if(!exist){
                    //post the users review
                    post_review(rate, description, userID);
                }else{
                    //display this message if the query was found in the review collection
                    showMessage("You already reviewed this workout before!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                refresh.setRefreshing(false);
                showMessage("Failed to add review to workout!");
            }
        });
    }
    //Method to post the users review taking in their inputs
    private void post_review(float rate, String description, String currentUser){
        //Instantiating an object of type ReviewModel
        ReviewModel review_model = new ReviewModel();
        //creating a variable set to the users id + the review id
        String review_check = auth.getCurrentUser().getUid() + "-" + model.getId();
        //assigning the current users email to a variable
        currentUser = auth.getCurrentUser().getEmail();
        //assigning various variables within review model values
        review_model.setReview_check(review_check);
        review_model.setRate(rate);
        review_model.setWorkout_id(model.getId());
        review_model.setDescription(description);
        review_model.setUserid(currentUser);
        //Database reference to create a new review with a unique ID (key)
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Params.REVIEW_KEY).push();
        //assigning the review model id to the new reference generated key
        review_model.setId(myRef.getKey());
        //inserting the review with a success listener to inform the user if
        // the process was successful
        myRef.setValue(review_model).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                //calling the update_review method to change the overall workout rating
                update_review_rating(rate);
                showMessage("Successfully posted your review!");
            }//if the process fails, on failure listener informs the user
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Failed to post your review!");
            }
        });
    }
    //Method to post the users review taking in their inputs
    private void update_review_rating(float rate){
        //Database reference locating to the current community workout id
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Params.COMWORKOUT_KEY).child(model.getId());
        //single event listener for the database reference
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
                showMessage("Failed to post your review!");
            }
        });
    }
    //Method to post the users review taking in their inputs
    public void update_review(int review_num, float rate){
        //Database reference locating to the current community workout id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.COMWORKOUT_KEY).child(model.getId());
        Map<String, Object> update_map = new HashMap<>();
        //insert mapping reference of review num into the hashmap
        update_map.put("review_num", review_num);
        //insert mapping reference of rate into the hashmap
        update_map.put("rate", rate);
        //updating the rate and review count of the workout, if successful, the user will
        //be notified with the success listener
        ref.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                showMessage("Successfully posted your rating!");
            }//if the process fails to update, the user will be notified by the failure listener
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Failed to post your rating!");
            }
        });
    }
}
