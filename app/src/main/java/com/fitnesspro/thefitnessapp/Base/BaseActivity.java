package com.fitnesspro.thefitnessapp.Base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    public FirebaseAuth auth;
    public Context context;
    public String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public final int PERMISSION_CODE = 567;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        auth = FirebaseAuth.getInstance();
    }
    public void invalidate(){
        finish();
        startActivity(getIntent());
    }
    @Override
    public void onClick(View view){

    }
    @Override
    public void onBackPressed(){
        finish();
    }

    public void showMessage(String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    public void showMessageShort(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public void startIntentAsFinishMode(Class<?> cls){
        Intent intent = new Intent(context, cls);
        startActivity(intent);
        finish();
    }
    public void startIntentAsCleanMode(Class<?> cls){
        Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    public void startIntentLiveMode(Class<?> cls){
        Intent intent = new Intent(context, cls);
        startActivity(intent);
    }
}
