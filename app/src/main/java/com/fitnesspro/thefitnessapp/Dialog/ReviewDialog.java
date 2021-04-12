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

import com.fitnesspro.thefitnessapp.Base.BaseDialog;
import com.fitnesspro.thefitnessapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ReviewDialog extends BaseDialog {
    ImageView close_btn;
    RatingBar rating_bar;
    EditText description_view;
    LinearLayout cancel_btn;
    LinearLayout review_btn;
    ReviewDialog.Listener mListener;

    public interface Listener{
        void onReview(float rate, String description);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_review, null);
        initView(rootView);
        builder.setView(rootView);
        builder.setCancelable(false);
        return builder.create();
    }
    private void initView(View rootView) {
        close_btn = rootView.findViewById(R.id.close);
        rating_bar = rootView.findViewById(R.id.rating);

        description_view = rootView.findViewById(R.id.description);
        review_btn = rootView.findViewById(R.id.review_btn);
        cancel_btn = rootView.findViewById(R.id.cancel_btn);

        close_btn.setOnClickListener(this);
        review_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }

    public void onClick(View view){
        if(view == close_btn){
            dismiss();
        }else if(view == review_btn){
            review();
        }else if(view == cancel_btn){
            dismiss();
        }
    }
    private void review(){
        String description = description_view.getText().toString().trim();
        float rate = rating_bar.getRating();
        if(rate == 0.0f){
            showMessage("Please rate now !");
            return;
        }
        if(mListener != null){
            mListener.onReview(rate, description);
        }
        dismiss();
    }
}
