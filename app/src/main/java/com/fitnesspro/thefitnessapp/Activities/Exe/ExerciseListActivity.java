package com.fitnesspro.thefitnessapp.Activities.Exe;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.fitnesspro.thefitnessapp.Activities.User.ProfileActivity;
import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.Dialog.AddExerciseDialog;
import com.fitnesspro.thefitnessapp.Dialog.WorkoutCreateDialog;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.Adapter.ExerciseAdapter;
import com.fitnesspro.thefitnessapp.models.CustomWorkoutModel;
import com.fitnesspro.thefitnessapp.models.ExerciseModel;
import com.fitnesspro.thefitnessapp.models.Params;
import com.fitnesspro.thefitnessapp.models.WorkoutDetailModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fitnesspro.thefitnessapp.models.Params.EXE_KEY;

public class ExerciseListActivity extends BaseActivity {
    //Initialising Variables
    ExerciseModel model;
    ImageView back_btn;
    SwipeRefreshLayout refresh;
    RecyclerView exe_list_view;
    FloatingActionButton add_btn;
    ExerciseAdapter adapter;
    List<ExerciseModel> data_list;
    List<ExerciseModel> list;
    SearchView searchView;
    private ValueEventListener add_list_listener;
    private DatabaseReference myRef;
    ImageView exerciseImage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the base view for the activity
        setContentView(R.layout.activity_exercise);
        //call initView method
        initView();
        if (Params.ADMIN) {
            add_btn.setVisibility(View.VISIBLE);
        } else {
            add_btn.setVisibility(View.INVISIBLE);
        }
    }
    //Method to return to applications previous screen
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void initView(){
        //initialise variables to features on the page
        model = (ExerciseModel)getIntent().getSerializableExtra("model");
        back_btn = findViewById(R.id.back);
        refresh = findViewById(R.id.refresh);
        exe_list_view = findViewById(R.id.exe_list);
        add_btn = findViewById(R.id.add_btn);
        searchView = findViewById(R.id.searchView);
//        changeImage = findViewById(R.id.change_profile);
        exe_list_view.setLayoutManager(new LinearLayoutManager(context));
        //Instantiating a new array list
        data_list = new ArrayList<>();
        //Instantiating a new adapter using the new array list
        adapter = new ExerciseAdapter(context, data_list);
        //Setting on callback methods for the adapter using each button
        adapter.setOnCallBack(new ExerciseAdapter.OnCallBack() {
            //A method to allow the user to go the the exercise page upon selecting
            @Override
            public void onDetail(ExerciseModel exercise) {
                Intent intent = new Intent(context, ExerciseDetailView.class);
                intent.putExtra("model", exercise);
                startActivity(intent);
            }

            @Override
            public void onDelete(ExerciseModel exercise) {
                removeExercise(model);
            }
        });
        //setting the RecyclerView to use the adapter
        exe_list_view.setAdapter(adapter);
        //Set on click listener for buttons to pick up onClick method
        back_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
//        changeImage.setOnClickListener(this);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });
        //calling the read method to read the workout data from firebase
        read();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        //if user clicks add button, show the add exercise dialog
        if(view == add_btn){
            showAddExerciseDialog();
        }
        //if user clicks back, finish the current activity and return to previous screen
        if(view == back_btn){
            finish();
        }

    }
    //Method to display add exercise dialog box
    public void showAddExerciseDialog(){
        //Initialising an object of type WorkoutCreateDialog
        AddExerciseDialog dlg = new AddExerciseDialog();
        //setting a listener to call the on create workout method
        dlg.setListener(new AddExerciseDialog.Listener() {
            @Override
            public void onCreate(String name, String description, String primary, String secondary, String equipment) {
                createExercise(name, description, primary, secondary, equipment);
            }

        });
        //show dialog view to the user
        dlg.show(getSupportFragmentManager(), "WORK_OUT");
    }


    public void createExercise(String name, String description, String primary, String secondary, String equipment){
        //database reference with a randomly generated key
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Exercise").child(description);
        DatabaseReference refKey = FirebaseDatabase.getInstance().getReference(EXE_KEY).push();
        //storing the randomly generated key
        String key = refKey.getKey();
        //Instantiating an object of type CustomerWorkoutModel
        ExerciseModel model = new ExerciseModel();
        //setting values to user input, randomly generated key and user id
        model.setExercise(name);
        model.setTitle(description);
        model.setId(key);
        model.setPrimaryMuscle(primary);
        model.setSecondaryMuscle(secondary);
        model.setEquipment(equipment);
//        model.setImage(image);
        refresh.setRefreshing(true);
        //adding the exercise and adding a success listener
        //to tell the user if the workout was added successfully
        ref.setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                //calls method to update the recycler view on change of an item
                adapter.notifyDataSetChanged();
                //display message if successful
                showMessage("Successfully created exercise!");
            }
            //adding a failure listener to tell the user if the workout failed to be added
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                showMessage("Failed to create the exercise!");
            }
        });
    }

    //A method to read the database and fetch relevant data from the required reference
    private void read(){
        //clearing the items in the array list
        data_list.clear();
        if(myRef == null){
            myRef = FirebaseDatabase.getInstance().getReference(EXE_KEY);
        }
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
        myRef.addListenerForSingleValueEvent(add_list_listener);
        //addChildlistener to assess any changes to the child level of the referenced location
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
        //checking if the searchbar is empty
        if(searchView != null)
        {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
                //setting a listener to wait for user input
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String s) {
                    //once user has input a value, run the search method using that value
                    search(s);
                    return true;
                }
            });
        }
    }
    public void removeExercise(ExerciseModel model){
        //database reference to grab the current workout id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(model.getTitle()).child(model.getId());
        refresh.setRefreshing(true);
        //removing the workout and adding a success listener
        //to tell the user if the workout was removed successfully
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                refresh.setRefreshing(false);
                //display message if successful
                showMessage("Successfully deleted the exercise!");
            }
            //adding a failure listener to tell the user if the workout failed to be removed
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                refresh.setRefreshing(false);
                //display message if it failed to complete the process
                showMessage("Failed to delete the exercise!");
            }
        });
    }
    //method to search for an exercise using its name
    private void search (String str)
    {
        list = new ArrayList<>();
        //for loop create to cycle through each exercise and assess
        //if it contains the same characters as the users input
        for(ExerciseModel object: data_list){
            //if the characters of the search match, add the item to the list
            if(object.getTitle().toLowerCase().contains(str.toLowerCase()) || (object.getPrimaryMuscle().toLowerCase().contains(str.toLowerCase())) || (object.getSecondaryMuscle().toLowerCase().contains(str.toLowerCase())))
            {
                list.add(object);
            }
            //Instantiating new object of type ExerciseAdapter
            ExerciseAdapter adapter = new ExerciseAdapter(context, list);
            //Setting on callback methods for the new adapter
            adapter.setOnCallBack(new ExerciseAdapter.OnCallBack() {
                //method to view the exercise page of the exercise selected
                @Override
                public void onDetail(ExerciseModel exercise) {
                    Intent intent = new Intent(context, ExerciseDetailView.class);
                    intent.putExtra("model", exercise);
                    startActivity(intent);
                }

                @Override
                public void onDelete(ExerciseModel exercise) {

                }
            });
            //setting the RecyclerView to use the adapter
            exe_list_view.setAdapter(adapter);
        }
    }
}