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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends BaseActivity {
    //Initialising Variables
    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnLogin;
    TextView btnSignup, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the base view for the activity
        setContentView(R.layout.activity_login);
        //call initView method
        initView();
    }

    private void initView() {
        //initialise variables to features on the page
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnReset = findViewById(R.id.reset_password);
        //Set on click listener for buttons to pick up onClick method
        btnSignup.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }
    //if user clicks button, call the appropriate method
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view == btnSignup) {
            signUp();
        } else if (view == btnLogin) {
            login();
        } else if(view == btnReset){
            reset();
        }
    }
    //Method to redirect to the register activity
    private void signUp() {
        startIntentLiveMode(RegisterActivity.class);
    }
    //Login method
    private void login() {
        String email = inputEmail.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();
        //if user did not enter email address show message
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Enter email address!");
            return;
        }
        //if user did not enter password show message
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Enter password!");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        //authenticate users email and password
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                progressBar.setVisibility(View.GONE);
                if (!task.isSuccessful()) {
                    if (password.length() < 6) {
                        inputPassword.setError(getString(R.string.minimum_password));
                    } else {
                        showMessage(getString(R.string.auth_failed));
                    }
                } else {
                    //if user logs in as the admin account, enable admin permissions
                    if(email.equals("admin@gmail.com")){
                        Params.ADMIN = true;
                    }
                    startIntentAsCleanMode(MainActivity.class);
                }
            }
        });
    }
    //method to reset users password
    private void reset() {
        EditText resetMail = new EditText(context);
        //Initialising dialog
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(context);
        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setMessage("Enter your email to reset your password");
        passwordResetDialog.setView(resetMail);
        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //extract email and send reset link
                String mail = resetMail.getText().toString().trim();
                //if the user does not enter an email, it will deny the user the reset
                if(mail.isEmpty() || !mail.contains("@")){
                    showMessage("You did not enter a valid email");
                    return;
                }
                //verifies that the email input is a user in the database
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
        //create and show the dialog
        passwordResetDialog.create().show();
    }
}