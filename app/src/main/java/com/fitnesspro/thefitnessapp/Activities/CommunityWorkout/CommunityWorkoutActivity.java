package com.fitnesspro.thefitnessapp.Activities.CommunityWorkout;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fitnesspro.thefitnessapp.Activities.Exe.ExerciseDetailView;
import com.fitnesspro.thefitnessapp.Adapter.CommunityWorkoutAdapter;
import com.fitnesspro.thefitnessapp.Adapter.ExerciseAdapter;
import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.Dialog.WorkoutCreateDialog;
import com.fitnesspro.thefitnessapp.Dialog.WorkoutUpdateDialog;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.CommunityWorkoutModel;
import com.fitnesspro.thefitnessapp.models.ExerciseModel;
import com.fitnesspro.thefitnessapp.models.Params;
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
public class CommunityWorkoutActivity extends BaseActivity {
    //Initialising Variables
    ImageView back_btn;
    SwipeRefreshLayout refresh;
    FloatingActionButton add_btn;
    RecyclerView workout_list_view;
    DatabaseReference myRef;
    List<CommunityWorkoutModel> data_list;
    List<CommunityWorkoutModel> list;
    SearchView searchView;
    CommunityWorkoutAdapter adapter;
    ValueEventListener add_list_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the base view for the activity
        setContentView(R.layout.activity_community_workout);
        //call initView method
        initView();
    }
    private void initView(){
        //initialise variables to features on the page
        back_btn = findViewById(R.id.back);
        refresh = findViewById(R.id.refresh);
        searchView = findViewById(R.id.communitySearchView);
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
        workout_list_view = findViewById(R.id.workout_list);
        workout_list_view.setHasFixedSize(true);
        //set layout as LinearLayout
        workout_list_view.setLayoutManager(mLayoutManager);
        //Set on click listener for buttons to pick up onClick method
        add_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        //Instantiating a new array list
        data_list = new ArrayList<>();
        //Instantiating a new adapter using the new array list
        adapter = new CommunityWorkoutAdapter(context, data_list);
        //Setting on callback methods for the adapter using each button
        adapter.setOnCallBack(new CommunityWorkoutAdapter.OnCallBack() {
            //A method to allow the user to edit the workout name and call the update method
            @Override
            public void onEdit(CommunityWorkoutModel model) {
                //Instantiating an object of type WorkoutUpdateDialog
                WorkoutUpdateDialog dlg = new WorkoutUpdateDialog();
                //setting a listener to update the workout name when selected
                dlg.setListener(new WorkoutUpdateDialog.Listener() {
                    @Override
                    public void onUpdate(String name) {
                        //calling the update method to update the workout
                        updateWorkout(model, name);
                    }
                });
                //show the workout update dialog screen to the user
                dlg.show(getSupportFragmentManager(), "WORK_OUT_UPDATE");
            }
            //method to delete the workout
            @Override
            public void onDelete(CommunityWorkoutModel model) {
                removeWorkout(model);
            }
            //method to view the workout selected
            @Override
            public void onGo(CommunityWorkoutModel model) {
                Intent intent = new Intent(context, CommunityWorkoutDetailView.class);
                intent.putExtra("model", model);
                startActivity(intent);
            }
            //method to view the reviews of the workout selected
            @Override
            public void onReview(CommunityWorkoutModel model) {
                Intent intent = new Intent(context, ReviewView.class);
                intent.putExtra("id", model.getId());
                startActivity(intent);
            }
        });
        //setting the RecyclerView to use the adapter
        workout_list_view.setAdapter(adapter);
        //calling the read method to read the workout data from firebase
        read();
    }
    //method to set an event on click of add/back button
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
    }
    //Method to return to applications previous screen
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    //Method to display create workout dialog box
    public void showCreateDialog(){
        //Initialising an object of type WorkoutCreateDialog
        WorkoutCreateDialog dlg = new WorkoutCreateDialog();
        //setting a listener to call the on create community workout method
        dlg.setListener(new WorkoutCreateDialog.Listener() {
            @Override
            public void onCreate(String name) {
                //calling the create community workout method to create the workout
                createCommunityWorkout(name);
            }
        });
        //show dialog view to the user
        dlg.show(getSupportFragmentManager(), "WORK_OUT");
    }
    //A method to read the database and fetch relevant data from the required reference
    private void read() {
        //clearing the items in the array list
        data_list.clear();
        if(myRef == null){
            myRef = FirebaseDatabase.getInstance().getReference().child(Params.COMWORKOUT_KEY);
        }
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
        //adding the listener to the reference
        myRef.addListenerForSingleValueEvent(add_list_listener);
        //addChildlistener to assess any changes to the child level of the referenced location
        myRef.addChildEventListener(new ChildEventListener() {
            //method to add a child to the referenced location
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try{
                    CommunityWorkoutModel model = dataSnapshot.getValue(CommunityWorkoutModel.class);
                    data_list.add(model);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //method to update a child in the referenced location
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                CommunityWorkoutModel model = dataSnapshot.getValue(CommunityWorkoutModel.class);
                int index = data_list.indexOf(model);
                if(index != -1){
                    data_list.set(index, model);
                }else{
                    data_list.add(model);
                }
                //calls method to update the recycler view on change of an item
                adapter.notifyDataSetChanged();
            }
            //method to remove a child in the referenced location
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                try{
                    CommunityWorkoutModel model = dataSnapshot.getValue(CommunityWorkoutModel.class);
                    data_list.remove(model);
                    //calls method to update the recycler view on change of an item
                    adapter.notifyDataSetChanged();
                }catch (Exception e){

                }
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {
                refresh.setRefreshing(false);
            }
        });
        //checking if the searchbar is empty
        if(searchView != null)
        {
            //setting a listener to wait for user input
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }
                //once user has input a value, run the search method using that value
                @Override
                public boolean onQueryTextChange(String s) {
                    search(s);
                    return true;
                }
            });
        }
    }
    //method to create the community workout while taking user input for the name
    public void createCommunityWorkout(String name){
        //database reference with a randomly generated key
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.COMWORKOUT_KEY).push();
        //storing the randomly generated key
        String key = ref.getKey();
        //Instantiating an object of type CommunityWorkoutModel
        CommunityWorkoutModel model = new CommunityWorkoutModel();
        //setting values to user input, randomly generated key and user id
        model.setName(name);
        model.setId(key);
        model.setUser_id(auth.getCurrentUser().getUid());
        model.setEmail(auth.getCurrentUser().getEmail());
        refresh.setRefreshing(true);
        //adding the workout and adding a success listener
        //to tell the user if the workout was added successfully
        ref.setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                //calls method to update the recycler view on change of an item
                adapter.notifyDataSetChanged();
                //display message if successful
                showMessage("Successfully created workout!");
            }
            //adding a failure listener to tell the user if the workout failed to be added
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                //display message if it failed to complete the process
                showMessage("Failed to create workout!");
            }
        });
    }
    //method to remove the community workout
    public void removeWorkout(CommunityWorkoutModel model){
        //database reference to grab the current workout id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.COMWORKOUT_KEY).child(model.getId());
        refresh.setRefreshing(true);
        //removing the workout and adding a success listener
        //to tell the user if the workout was removed successfully
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                //display message if successful
                showMessage("Successfully deleted workout!");
            }
            //adding a failure listener to tell the user if the workout failed to be removed
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                //display message if it failed to complete the process
                showMessage("Failed to delete the workout!");
            }
        });
    }
    //method to update the name of the community workout
    public void updateWorkout(CommunityWorkoutModel model, String name){
        //database reference to grab the current workout id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.COMWORKOUT_KEY).child(model.getId());
        //Instantiating an object of type HashMap
        Map<String, Object> update_map = new HashMap<>();
        //insert mapping reference of name into the hashmap
        update_map.put("name", name);
        refresh.setRefreshing(true);
        //updating the name field of workouts with the users input name
        //and adding a success listener to tell the user if the process was completed
        ref.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                //display message if successful
                showMessage("Successfully updated the workout!");
            }//adding a failure listener to tell the user if the process has failed to complete
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                //display message if it failed to complete the process
                showMessage("Failed to update the workout!");
            }
        });
    }
    //method to search for a community workout using its name, it must repeat the methods
    //seen in initView for the buttons to function as it displays a new list of data
    private void search (String str)
    {
        //new variable instantiated of type ArrayList
        list = new ArrayList<>();
        //for loop create to cycle through each community workout and assess
        //if it contains the same characters as the users input
        for(CommunityWorkoutModel object: data_list){
            //if the characters of the search match, add the item to the list
            if(object.getName().toLowerCase().contains(str.toLowerCase()))
            {
                list.add(object);
            }
            //Instantiating new object of type CommunityWorkoutAdapter
            CommunityWorkoutAdapter adapter = new CommunityWorkoutAdapter(context, list);
            //Setting on callback methods for the new adapter using each button
            adapter.setOnCallBack(new CommunityWorkoutAdapter.OnCallBack() {
                //A method to allow the user to edit the workout name and call the update method
                @Override
                public void onEdit(CommunityWorkoutModel model) {
                    WorkoutUpdateDialog dlg = new WorkoutUpdateDialog();
                    dlg.setListener(new WorkoutUpdateDialog.Listener() {
                        //setting a listener to update the workout name when selected
                        @Override
                        public void onUpdate(String name) {
                            updateWorkout(model, name);
                        }
                    });
                    //show the workout update dialog screen to the user
                    dlg.show(getSupportFragmentManager(), "WORK_OUT_UPDATE");
                }
                //method to delete the workout
                @Override
                public void onDelete(CommunityWorkoutModel model) {
                    removeWorkout(model);
                }
                //method to view the workout selected
                @Override
                public void onGo(CommunityWorkoutModel model) {
                    Intent intent = new Intent(context, CommunityWorkoutDetailView.class);
                    intent.putExtra("model", model);
                    startActivity(intent);
                }
                //method to view the reviews of the workout selected
                @Override
                public void onReview(CommunityWorkoutModel model) {
                    Intent intent = new Intent(context, ReviewView.class);
                    intent.putExtra("id", model.getId());
                    startActivity(intent);
                }
            });
            //setting the RecyclerView to use the adapter
            workout_list_view.setAdapter(adapter);
        }
    }
}
