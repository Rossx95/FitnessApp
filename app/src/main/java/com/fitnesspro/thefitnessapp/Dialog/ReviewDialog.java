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
import android.widget.RatingBar;
import android.widget.TextView;

import com.fitnesspro.thefitnessapp.Base.BaseDialog;
import com.fitnesspro.thefitnessapp.R;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/*
 * A class to create a dialog box to allow users to review a workout
 * */
public class ReviewDialog extends BaseDialog {
    //Initialising Variables
    ImageView close_btn;
    RatingBar rating_bar;
    EditText description_view;
    TextView usersID;
    LinearLayout cancel_btn;
    LinearLayout review_btn;
    ReviewDialog.Listener mListener;

    public interface Listener{
        void onReview(float rate, String description, String userID);
    }
    public void setListener(ReviewDialog.Listener listener){
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
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_review, null);
        initView(rootView);
        builder.setView(rootView);
        builder.setCancelable(false);
        return builder.create();
    }
    private void initView(View rootView) {
        //initialise variables to features on the page
        close_btn = rootView.findViewById(R.id.close);
        rating_bar = rootView.findViewById(R.id.rating);
        description_view = rootView.findViewById(R.id.description);
        review_btn = rootView.findViewById(R.id.review_btn);
        cancel_btn = rootView.findViewById(R.id.cancel_btn);
        usersID = rootView.findViewById(R.id.reviewuserID);
        //Set on click listener for buttons to pick up onClick method
        close_btn.setOnClickListener(this);
        review_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }
    //method to set events of various buttons
    public void onClick(View view){
        if(view == close_btn){
            dismiss();
        }else if(view == review_btn){
            review();
        }else if(view == cancel_btn){
            dismiss();
        }
    }
    //A method to grab the users review
    private void review(){
        //grabbing the users description
        String description = description_view.getText().toString().trim();
        int length = description.length();
        //grabbing the users rating
        float rate = rating_bar.getRating();
        //grabbing the current user id
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Validation to ensure a rating is entered
        if(rate == 0.0f){
            showMessage("Please rate now!");
            return;
        }
        //Validation to ensure a description above 10 characters is entered
        if(length < 10){
            showMessage("Message must have more than 10 characters");
            return;
        }
        if(mListener != null){
            mListener.onReview(rate, description,userID);
        }
        dismiss();
    }
}
