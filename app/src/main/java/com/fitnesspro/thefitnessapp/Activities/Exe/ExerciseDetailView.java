package com.fitnesspro.thefitnessapp.Activities.Exe;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.ExerciseModel;
import com.fitnesspro.thefitnessapp.models.Params;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static com.fitnesspro.thefitnessapp.models.Params.EXE_KEY;

public class ExerciseDetailView extends BaseActivity {
    //Initialising Variables
    ExerciseModel model;
    ImageView back_btn;
    TextView title_view;
    ImageView image_view;
    TextView desc_view, primarycateory, secondarycategory, equipment;
    Button changeImage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the base view for the activity
        setContentView(R.layout.activity_exe_detail);
        //call initView method
        initView();

        if(Params.ADMIN){
            changeImage.setVisibility(View.VISIBLE);
        }else{
            changeImage.setVisibility(View.INVISIBLE);
        }

        changeImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });
    }
    private void initView(){
        //initialise variables to features on the page
        model = (ExerciseModel)getIntent().getSerializableExtra("model");
        back_btn = findViewById(R.id.back);
        title_view = findViewById(R.id.title_view);
        image_view = findViewById(R.id.image);
        desc_view = findViewById(R.id.desc_view);
        primarycateory = findViewById(R.id.desc_primary_category);
        secondarycategory = findViewById(R.id.desc_secondary_category);
        equipment = findViewById(R.id.desc_equipment);
        changeImage = findViewById(R.id.uploadexerciseimage);
        storageReference = FirebaseStorage.getInstance().getReference();
        //Set on click listener for buttons to pick up onClick method
        back_btn.setOnClickListener(this);
        load_Exercise();
    }
    private void uploadImage(Uri imageUri) {
        //Storage Reference locating to Firebase Storage, users collections
        StorageReference fileRef = storageReference.child("Exercises/"+model.getTitle()+"/Exercise.jpg");
        //uploading users selected image
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            //if successful, load the uploaded image onto the screen for the user
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference imageStore = FirebaseDatabase.getInstance().getReference().child("Exercise").child(model.getTitle());
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("exercise", model.getExercise());
                        hashMap.put("title", model.getTitle());
                        hashMap.put("primaryMuscle", model.getPrimaryMuscle());
                        hashMap.put("secondaryMuscle", model.getSecondaryMuscle());
                        hashMap.put("equipment", model.getEquipment());
                        hashMap.put("id", model.getId());
                        hashMap.put("image", String.valueOf(uri));

                        imageStore.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ExerciseDetailView.this, "Uploaded image to exercise successfully",Toast.LENGTH_SHORT).show();
                            }
                        });
                        Picasso.get().load(uri).into(image_view);
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
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                uploadImage(imageUri);
            }
        }
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
    //method to load details of the exercise selected
    private void load_Exercise(){
        //load title
        title_view.setText(model.getTitle());
        //load exercise information
        desc_view.setText(model.getExercise());
        primarycateory.setText(model.getPrimaryMuscle());
        secondarycategory.setText(model.getSecondaryMuscle());
        equipment.setText(model.getEquipment());
        //load image into appropriate view
        Glide.with(context)
                .load(model.getImage())
                .into(image_view);
    }
}
