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

    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnLogin;
    TextView btnSignup, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnReset = findViewById(R.id.reset_password);

        btnSignup.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

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
    private void signUp() {
        startIntentLiveMode(RegisterActivity.class);
    }
    private void login() {
        String email = inputEmail.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Enter email address!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Enter password!");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        //authenticate user
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
                    if(email.equals("admin@gmail.com")){
                        Params.ADMIN = true;
                    }
                    startIntentAsCleanMode(MainActivity.class);
                }
            }
        });
    }

    private void reset() {
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