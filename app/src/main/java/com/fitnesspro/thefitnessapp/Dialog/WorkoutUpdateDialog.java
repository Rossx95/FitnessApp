package com.fitnesspro.thefitnessapp.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fitnesspro.thefitnessapp.Base.BaseDialog;
import com.fitnesspro.thefitnessapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*
* A class to create a dialog box to allow users to update the name of a workout
* */
public class WorkoutUpdateDialog extends BaseDialog {
    //Initialising Variables
    ImageView close_btn;
    EditText name_view;
    LinearLayout cancel_btn;
    LinearLayout update_btn;
    WorkoutUpdateDialog.Listener mListener;

    public interface Listener{
        void onUpdate(String name);
    }
    public void setListener(WorkoutUpdateDialog.Listener listener){
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
        }catch (Exception e){
            e.getStackTrace();
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //set the base view for the activity
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_workout_update, null);
        initView(rootView);
        builder.setView(rootView);
        builder.setCancelable(false);
        return builder.create();
    }
    private void initView(View rootView) {
        //initialise variables to features on the page
        close_btn = rootView.findViewById(R.id.close);
        name_view = rootView.findViewById(R.id.name);
        update_btn = rootView.findViewById(R.id.update_btn);
        cancel_btn = rootView.findViewById(R.id.cancel_btn);
        //Set on click listener for buttons to pick up onClick method
        close_btn.setOnClickListener(this);
        update_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }
    //method to set events of various buttons
    public void onClick(View view){
        if(view == close_btn){
            dismiss();
        }else if(view == update_btn){
            create();
        }else if(view == cancel_btn){
            dismiss();
        }
    }
    private void create(){
        //grabbing text input by user
        String name = name_view.getText().toString().trim();
        //Validation to ensure text field isnt empty
        if(TextUtils.isEmpty(name)){
            name_view.setError("Please enter name !");
            return;
        }
        if(mListener != null){
            mListener.onUpdate(name);
        }
        //close dialog
        dismiss();
    }
}
