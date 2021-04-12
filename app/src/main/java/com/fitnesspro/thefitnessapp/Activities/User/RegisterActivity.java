package com.fitnesspro.thefitnessapp.Activities.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.Activities.Main.MainActivity;
import com.fitnesspro.thefitnessapp.models.Params;
import com.fitnesspro.thefitnessapp.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends BaseActivity {

    EditText inputFirstName, inputLastName, inputEmail, inputPassword;
    Button btnSignUp;
    ProgressBar progressBar;
    DatabaseReference mDatabase;
    TextView btnSignIn, btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private void initView(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Params.USER_KEY);

        btnSignIn = findViewById(R.id.sign_in_button);
        btnSignUp = findViewById(R.id.sign_up_button);
        inputFirstName = findViewById(R.id.firstname);
        inputLastName = findViewById(R.id.lastname);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == btnResetPassword){
            reset();
        }else if(view == btnSignIn){
            finish();
        }else if(view == btnSignUp){
            signUp();
        }
    }
    private void signUp(){
        String firstname = inputFirstName.getText().toString().trim();
        String lastname = inputLastName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if (TextUtils.isEmpty(firstname)) {
            inputFirstName.setError("Enter first name!");
            return;
        }
        if (TextUtils.isEmpty(lastname)) {
            inputLastName.setError("Enter last name!");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Enter email address!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Enter password!");
            return;
        }
        if (password.length() < 6) {
            showMessageShort("Password too short, enter minimum 6 characters!");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        //create user
        register_user(firstname, lastname, email, password);
    }
    //-----REGISTERING THE NEW USER------
    private void register_user(final String firstname, String lastname, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //------IF USER IS SUCCESSFULLY REGISTERED-----
                if (task.isSuccessful()) {
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    final String uid = current_user.getUid();
                    UserModel user = new UserModel();
                    user.setFirstname(firstname);
                    user.setLastname(lastname);
                    user.setEmail(email);
                    user.setPassword(password);
                    mDatabase.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            progressBar.setVisibility(View.GONE);
                            if (task1.isSuccessful()) {
                                if(email.equals("admin@gmail.com")){
                                    Params.ADMIN = true;
                                }
                                showMessage("Account Created successfully!");
                                startIntentAsCleanMode(MainActivity.class);
                            } else {
                                showMessage("Error Creating Account.Please try again!");
                            }
                        }
                    });
                }
                //---ERROR IN ACCOUNT CREATING OF NEW USER---
                else {
                    progressBar.setVisibility(View.GONE);
                    showMessage("Error Creating Account.Please try again!");
                }
            }
        });
    }
    private void reset(){
        EditText resetMail = new EditText(context);
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(context);
        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setMessage("Enter your email to reset your password");
        passwordResetDialog.setView(resetMail);
        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //extract email and send reset link
                String mail = resetMail.getText().toString().trim();
                auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("Reset link has been sent to your email");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Reset link has not been sent!\n" + e.getMessage());
                    }
                });
            }
        });
        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        passwordResetDialog.create().show();
    }
}