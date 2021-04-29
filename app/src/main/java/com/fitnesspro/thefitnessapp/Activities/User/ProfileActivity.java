package com.fitnesspro.thefitnessapp.Activities.User;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.Params;
import com.fitnesspro.thefitnessapp.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {
    private DatabaseReference myRef;
    FirebaseAuth fauth;
    private static final String TAG = "ProfileActivity";
    ImageView back_btn;
    EditText fname, lname, age, height, weight;
    TextView txtemail;
    ImageView profileImage;
    Button updateButton, changeImage;
    ProgressBar progressBar;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the base view for the activity
        setContentView(R.layout.activity_profile);
        //call initView method
        initView();
        //setting an on click listener, if the user clicks the profile image
        //they are able to change it using their own devices gallery
        changeImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });
    }
    //Method to track the activity, if an image is selected, the image selected will be uploaded
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                uploadImage(imageUri);
            }
        }
    }
    //Method to allow the user to upload an image
    private void uploadImage(Uri imageUri) {
        //Storage Reference locating to Firebase Storage, users collections
        StorageReference fileRef = storageReference.child("users/"+fauth.getCurrentUser().getUid()+"/profile.jpg");
        //uploading users selected image
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            //if successful, load the uploaded image onto the screen for the user
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }//if it failed to upload, notify the user
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Image failed to upload", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initView() {
        //Initialising variables
        back_btn = findViewById(R.id.back);
        fname = findViewById(R.id.firstname);
        lname = findViewById(R.id.lastname);
        age = findViewById(R.id.age);
        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);
        profileImage = findViewById(R.id.profileImage);
        txtemail = findViewById(R.id.txtemail);
        progressBar = findViewById(R.id.progressBar);
        updateButton = findViewById(R.id.update_button);
        changeImage = findViewById(R.id.change_profile);
        storageReference = FirebaseStorage.getInstance().getReference();
        fauth = FirebaseAuth.getInstance();
        //Set on click listener for buttons to pick up onClick method
        back_btn.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        initValue();
        StorageReference profileRef = storageReference.child("users/"+fauth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });
    }
    //method to set an event on click of update/back button
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == back_btn) {
            finish();
        } else if (view == updateButton) {
            update();
        }
    }
    //Method to check for changes in the user profile
    private void initValue(){
        progressBar.setVisibility(View.VISIBLE);
        myRef = FirebaseDatabase.getInstance().getReference().child(Params.USER_KEY).child(auth.getCurrentUser().getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                UserModel user = dataSnapshot.getValue(UserModel.class);
                showUser(user);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                showMessage("Failed to load your information!");
            }
        });
    }
    //Method to update the user profile
    private void update(){
        String ufname = fname.getText().toString().trim();
        String ulname = lname.getText().toString().trim();
        String uage = age.getText().toString().trim();
        String uheight = height.getText().toString().trim();
        String uweight = weight.getText().toString().trim();
        //Validation to ensure users enter correct data
        if(TextUtils.isEmpty(ufname)){
            fname.setError("Please enter your first name!");
            return;
        }
        if(TextUtils.isEmpty(ulname)){
            lname.setError("Please enter your last name!");
            return;
        }
        if(TextUtils.isEmpty(uage)){
            age.setError("Please enter your age!");
            return;
        }
        if(TextUtils.isEmpty(uheight)){
            height.setError("Please enter your height!");
            return;
        }
        if(TextUtils.isEmpty(uweight)){
            weight.setError("Please enter your weight!");
            return;
        }
        myRef = FirebaseDatabase.getInstance().getReference().child(Params.USER_KEY).child(auth.getCurrentUser().getUid());
        Map<String, Object> update_map = new HashMap<>();
        //insert mapping reference of user profile fields into the hashmap
        update_map.put("firstname", ufname);
        update_map.put("lastname", ulname);
        update_map.put("age", uage);
        update_map.put("weight", uweight);
        update_map.put("height", uheight);
        progressBar.setVisibility(View.VISIBLE);
        //updating children of the user profile collection according to information provided in
        //the hashmap, if successful the user will be notified
        myRef.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                showMessage("Successfully updated user information!");
            }//if the process failed, the user will be notified
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                showMessage("Failed to update user information!");
            }
        });
    }
    //Method to display current user information
    private void showUser(UserModel user){
        fname.setText(user.getFirstname());
        lname.setText(user.getLastname());
        age.setText(user.getAge());
        weight.setText(user.getWeight());
        height.setText(user.getHeight());
        txtemail.setText(user.getEmail());
    }
    //Method to return to applications previous screen
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}