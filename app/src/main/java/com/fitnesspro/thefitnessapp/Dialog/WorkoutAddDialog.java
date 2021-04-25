package com.fitnesspro.thefitnessapp.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fitnesspro.thefitnessapp.Activities.Exe.ExerciseDetailView;
import com.fitnesspro.thefitnessapp.Adapter.ExerciseAdapter;
import com.fitnesspro.thefitnessapp.Base.BaseDialog;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.WorkoutDetailModel;
import com.fitnesspro.thefitnessapp.models.ExerciseModel;
import com.fitnesspro.thefitnessapp.models.Params;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*
 * A class to create a dialog box to allow users to add an exercise to a workout
 * */
public class WorkoutAddDialog extends BaseDialog {
    //Initialising Variables
    ImageView close_btn;
    ProgressBar progressBar;
    EditText weight_view;
    EditText reps_view;
    LinearLayout exe_list_view;
    LinearLayout cancel_btn;
    LinearLayout add_btn;
    Listener mListener;
    LayoutInflater inflater;
    ExerciseModel selected_exe;
    List<ExerciseModel> data_list;
    List<ExerciseModel> list;
    SearchView searchView;
    ExerciseAdapter adapter;

    ArrayList<HashMap<String, String>> exe_list;


    public interface Listener{
        void onCreate(WorkoutDetailModel model);
    }
    public void setListener(Listener listener){
        mListener = listener;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
        }
        catch (Exception e){
            e.getStackTrace();
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //set the base view for the activity
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_workout_add, null);
        initView(rootView);
        builder.setView(rootView);
        builder.setCancelable(false);
        return builder.create();
    }
    private void initView(View rootView) {
        //initialise variables to features on the page
        inflater = getActivity().getLayoutInflater();
        close_btn = rootView.findViewById(R.id.close);
        progressBar = rootView.findViewById(R.id.progressBar);
        weight_view = rootView.findViewById(R.id.weight);
        reps_view = rootView.findViewById(R.id.reps);
        exe_list_view = rootView.findViewById(R.id.exe_list);
        //Set on click listener for buttons to pick up onClick method
        add_btn = rootView.findViewById(R.id.add_btn);
        cancel_btn = rootView.findViewById(R.id.cancel_btn);
        //Instantiating a new adapter using the new array list
        adapter = new ExerciseAdapter(context, data_list);
        //Set on click listener for buttons to pick up onClick method
        close_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        //calling the read method to read the workout data from firebase
        read();
    }
    //method to set events of various buttons
    public void onClick(View view){
        if(view == close_btn){
            dismiss();
        }else if(view == add_btn){
            create();
        }else if(view == cancel_btn){
            dismiss();
        }
    }
    //A method to read the database and fetch relevant data from the required reference
    private void read(){
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child(Params.EXE_KEY);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                List<ExerciseModel> exe_list = new ArrayList<>();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    ExerciseModel item = d.getValue(ExerciseModel.class);
                    exe_list.add(item);
                }
                initExeList(exe_list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage("Failed to load exercises!");
                progressBar.setVisibility(View.GONE);
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
    //Method to add the exercise to the workout
    private void create(){
        String weight = weight_view.getText().toString().trim();
        String reps = reps_view.getText().toString().trim();
        if(TextUtils.isEmpty(weight)){
            weight_view.setError("Please enter weight !");
            return;
        }
        if(TextUtils.isEmpty(reps)){
            reps_view.setError("Please enter reps !");
            return;
        }
        if(selected_exe == null){
            showMessage("Please select exercise !");
            return;
        }
        WorkoutDetailModel model = new WorkoutDetailModel();
        model.setId(generateDate());
        model.setWeight(weight);
        model.setReps(reps);
        model.setExe(selected_exe);
        if(mListener != null){
            mListener.onCreate(model);
        }
        dismiss();
    }
    private void initExeList(List<ExerciseModel> model_list){
        if(model_list == null || model_list.isEmpty()){
            return;
        }
        for(ExerciseModel item : model_list){
            addExe(item);
        }
    }
    //Method to add exercise to the workout
    private void addExe(ExerciseModel model){
        View view = inflater.inflate(R.layout.layout_exe_selection_item, null);
        final LinearLayout exe_area = view.findViewById(R.id.exe_area);
        final ImageView exe_image = view.findViewById(R.id.image_item);
        final TextView title_view = view.findViewById(R.id.title_view);
        Glide.with(getContext())
                .load(model.getImage())
                .into(exe_image);
        title_view.setText(model.getTitle());
        exe_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_exe = model;
                initArea();
                exe_area.setBackground(getResources().getDrawable(R.drawable.area_selected));
            }
        });
        exe_list_view.addView(view);
    }
    //Method to highlight the selected exercise
    private void initArea(){
        for(int i = 0; i < exe_list_view.getChildCount(); i++){
            View view = exe_list_view.getChildAt(i);
            LinearLayout them_area = view.findViewById(R.id.exe_area);
            them_area.setBackground(getResources().getDrawable(R.drawable.area_regular));
        }
    }
    //Generate
    private String generateDate(){
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs", Locale.getDefault());
        return ft.format(dNow);
    }
    //method to search for an exercise using its name
    private void search (String str)
    {
        //new variable instantiated of type ArrayList
        List<ExerciseModel> exe_list = new ArrayList<>();
        //for loop create to cycle through each exercose and assess
        //if it contains the same characters as the users input
        for(ExerciseModel object: data_list){
            if(object.getTitle().toLowerCase().contains(str.toLowerCase()))
            {
                exe_list.add(object);
            }
            ExerciseAdapter adapter = new ExerciseAdapter(context, list);
            adapter.setOnCallBack(new ExerciseAdapter.OnCallBack() {
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
//             exe_list_view.setAdapter(adapter);
        }
    }
}
