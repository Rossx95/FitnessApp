package com.fitnesspro.thefitnessapp.Activities.User;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.Params;
import com.fitnesspro.thefitnessapp.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {
    private DatabaseReference myRef;
    private static final String TAG = "ProfileActivity";
    ImageView back_btn;
    EditText fname, lname, email, age, height, weight;
    Button updateButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();

    }

    private void initView() {
        back_btn = findViewById(R.id.back);
        fname = findViewById(R.id.firstname);
        lname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        age = findViewById(R.id.age);
        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);
        progressBar = findViewById(R.id.progressBar);
        updateButton = findViewById(R.id.update_button);

        back_btn.setOnClickListener(this);
        updateButton.setOnClickListener(this);

        initValue();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == back_btn) {
            finish();
        } else if (view == updateButton) {
            update();
        }
    }
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
                showMessage("Fail to load user data !");
            }
        });
    }
    private void update(){
        String ufname = fname.getText().toString().trim();
        String ulname = lname.getText().toString().trim();
        String uemail = email.getText().toString().trim();
        String uage = age.getText().toString().trim();
        String uheight = height.getText().toString().trim();
        String uweight = weight.getText().toString().trim();

        if(TextUtils.isEmpty(ufname)){
            fname.setError("Please enter first name !");
            return;
        }
        if(TextUtils.isEmpty(ulname)){
            lname.setError("Please enter last name !");
            return;
        }
        if(TextUtils.isEmpty(uemail)){
            email.setError("Please enter email !");
            return;
        }
        if(TextUtils.isEmpty(uage)){
            age.setError("Please enter age !");
            return;
        }
        if(TextUtils.isEmpty(uheight)){
            height.setError("Please enter height !");
            return;
        }
        if(TextUtils.isEmpty(uweight)){
            weight.setError("Please enter weight !");
            return;
        }

        myRef = FirebaseDatabase.getInstance().getReference().child(Params.USER_KEY).child(auth.getCurrentUser().getUid());

        Map<String, Object> update_map = new HashMap<>();
        update_map.put("firstname", ufname);
        update_map.put("lastname", ulname);
        update_map.put("email", uemail);
        update_map.put("age", uage);
        update_map.put("weight", uweight);
        update_map.put("height", uheight);

        progressBar.setVisibility(View.VISIBLE);

        myRef.updateChildren(update_map).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                showMessage("Success to update user !");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                showMessage("Fail to update !");
            }
        });
    }
    private void showUser(UserModel user){
        fname.setText(user.getFirstname());
        lname.setText(user.getLastname());
        email.setText(user.getEmail());
        age.setText(user.getAge());
        weight.setText(user.getWeight());
        height.setText(user.getHeight());
    }
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