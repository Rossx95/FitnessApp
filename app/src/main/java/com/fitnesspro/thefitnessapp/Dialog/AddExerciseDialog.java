package com.fitnesspro.thefitnessapp.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fitnesspro.thefitnessapp.Activities.Exe.ExerciseListActivity;
import com.fitnesspro.thefitnessapp.Base.BaseDialog;
import com.fitnesspro.thefitnessapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.fitnesspro.thefitnessapp.models.Params.EXE_KEY;
//Number of methods 10
public class AddExerciseDialog extends BaseDialog {
    //Initialising Variables
    ImageView close_btn;
    ProgressBar progressBar;
    EditText description, exercise, primaryCategory, secondaryCategory, equipment;
    ImageView exerciseImage;
    LinearLayout cancel_btn;
    LinearLayout create_btn;
    AddExerciseDialog.Listener mListener;
    StorageReference storageReference;

    public interface Listener {
        void onCreate(String name, String description, String primary, String secondary, String equipment);
    }

    public void setListener(AddExerciseDialog.Listener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //set the base view for the activity
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_exercise_create, null);
        initView(rootView);
        builder.setView(rootView);
        builder.setCancelable(false);
        return builder.create();
    }

    private void initView(View rootView) {
        //initialise variables to features on the page
        close_btn = rootView.findViewById(R.id.close);
        exercise = rootView.findViewById(R.id.exe_name);
        description = rootView.findViewById(R.id.exe_description);
        primaryCategory = rootView.findViewById(R.id.enter_primary_category);
        secondaryCategory = rootView.findViewById(R.id.enter_secondary_category);
        equipment = rootView.findViewById(R.id.enter_equipment);

        create_btn = rootView.findViewById(R.id.create_exe_btn);
        cancel_btn = rootView.findViewById(R.id.cancel_btn);
        storageReference = FirebaseStorage.getInstance().getReference();
        //Set on click listener for buttons to pick up onClick method
        close_btn.setOnClickListener(this);
        create_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }

    private void uploadImage(Uri imageUri) {
        //Storage Reference locating to Firebase Storage, users collections
        StorageReference fileRef = storageReference.child("Exercises/" + EXE_KEY + "/Exercise.jpg");
        //uploading users selected image
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            //if successful, load the uploaded image onto the screen for the user
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(exerciseImage);
                    }
                });
            }//if it failed to upload, notify the user
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(ExerciseListActivity.this, "Image failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method to track the activity, if an image is selected, the image selected will be uploaded
    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                uploadImage(imageUri);
            }
        }
    }

    //method to set events of various buttons
    public void onClick(View view) {
        if (view == close_btn) {
            dismiss();
        } else if (view == create_btn) {
            addExercise();
        } else if (view == cancel_btn) {
            dismiss();
        }
    }
    private void addExercise() {
        //grabbing text input by user
        String name = exercise.getText().toString().trim();
        String exerciseDescription = description.getText().toString().trim();
        String primary = primaryCategory.getText().toString().trim();
        String secondary = secondaryCategory.getText().toString().trim();
        String equip = equipment.getText().toString().trim();
        int length = description.length();
        //Validation to ensure text field isnt empty
        if (TextUtils.isEmpty(name)) {
            exercise.setError("Please enter an exercise name!");
            return;
        }
        if (TextUtils.isEmpty(primary)) {
            primaryCategory.setError("Please enter a primary muscle type!");
            return;
        }
        if (!primary.contains("Chest".toLowerCase()) && (!primary.contains("Shoulders".toLowerCase()) && (!primary.contains("Triceps".toLowerCase()))
                && (!primary.contains("Biceps".toLowerCase())) && (!primary.contains("Traps".toLowerCase())) && (!primary.contains("Back".toLowerCase())) &&
                (!primary.contains("Calves".toLowerCase())) && (!primary.contains("Glutes".toLowerCase())) && (!primary.contains("Quads".toLowerCase())))) {
            primaryCategory.setError("Please enter a valid muscle type:\n\nChest,\n\nShoulders,\n\nQuads..etc");
            return;
        }
        if (TextUtils.isEmpty(secondary)) {
            secondaryCategory.setError("Please enter a secondary muscle type!");
        }
        if (!secondary.contains("Chest".toLowerCase()) && (!secondary.contains("Shoulders".toLowerCase()) && (!secondary.contains("Triceps".toLowerCase()))
                && (!secondary.contains("Biceps".toLowerCase())) && (!secondary.contains("Traps".toLowerCase())) && (!secondary.contains("Back".toLowerCase())) &&
                (!secondary.contains("Calves".toLowerCase())) && (!secondary.contains("Glutes".toLowerCase())) && (!secondary.contains("Quads".toLowerCase())))) {
            secondaryCategory.setError("Please enter a valid muscle type:\n\nChest,\n\nShoulders,\n\nQuads..etc");
            return;
        }
        if (TextUtils.isEmpty(equip)) {
            description.setError("Please enter equipment required!");
            return;
        }
        if (TextUtils.isEmpty(exerciseDescription)) {
            description.setError("Please enter a description for the exercise!");
            return;
        }
        if(length < 40){
            description.setError("The description must contain more than 40 characters");
            return;
        }
        if (mListener != null) {
            mListener.onCreate(exerciseDescription, name, primary, secondary, equip);
        }
        //close dialog
        dismiss();
    }
}

