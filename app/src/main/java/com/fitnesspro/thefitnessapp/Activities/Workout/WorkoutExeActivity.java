package com.fitnesspro.thefitnessapp.Activities.Workout;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.WorkoutDetailModel;
import com.fitnesspro.thefitnessapp.models.HistoryModel;
import com.fitnesspro.thefitnessapp.models.Params;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
//Number of methods 9
/*
Notes on how to set up this class were taken from
https://www.youtube.com/watch?v=zPsSUEGDfVY
https://www.youtube.com/watch?v=NK_-phxyIAM
https://www.youtube.com/watch?v=0ZSg58AV8r8
 */
public class WorkoutExeActivity extends BaseActivity {
    //Initialising Variables
    WorkoutDetailModel model;
    ImageView back_btn;
    FrameLayout exe_area;
    ImageView exe_image;
    TextView exe_title;
    TextView weight_view;
    TextView reps_view;
    LinearLayout log_btn;
    ProgressBar progressBar;

    //Initialising variables required to set up google calendar API
    final int REQUEST_ACCOUNT_PICKER = 1000;
    final int REQUEST_AUTHORIZATION = 1001;
    final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    GoogleAccountCredential credential;
    final String[] SCOPES = { CalendarScopes.CALENDAR };
    final String PREF_ACCOUNT_NAME = "accountName";
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    Calendar mService;
    DateTime startDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the base view for the activity
        setContentView(R.layout.activity_workout_exe);
        //call initView method
        initView();
    }
    private void initView(){
        //Setting model to an object from a different model to be able to access getters and setters
        model = (WorkoutDetailModel) getIntent().getSerializableExtra("model");
        //initialise variables to features on the page
        back_btn = findViewById(R.id.back);
        exe_area = findViewById(R.id.exe_area);
        exe_image = findViewById(R.id.exe_image);
        exe_title = findViewById(R.id.title_view);
        weight_view = findViewById(R.id.weight);
        reps_view = findViewById(R.id.reps);
        log_btn = findViewById(R.id.log_btn);
        progressBar = findViewById(R.id.progressBar);
        //Set on click listener for buttons to pick up onClick method
        back_btn.setOnClickListener(this);
        log_btn.setOnClickListener(this);
        //calling method to load exercise logging page
        loadExerciseToLog();
    }
    private void loadExerciseToLog(){
        Glide.with(context)
                .load(model.getExe().getImage())
                .into(exe_image);
        exe_title.setText(model.getExe().getTitle());
        weight_view.setText(model.getWeight());
        reps_view.setText(model.getReps());
        //get start time
        startDateTime = new DateTime(System.currentTimeMillis());
        SharedPreferences settings = getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        //setting google calendar credentials
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        mService = new Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("FitnessApp Android")
                .build();
        //if the user has not logged into a google account, call the method to ask the user
        //to log into an account
        if(credential.getSelectedAccountName() == null){
            chooseAccount();
        }
    }
    //method to set an event on click of back and log button
    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == back_btn){
            finish();
        }else if(view == log_btn){
            new LogCaller().execute();
        }
    }
    //Method to log the exercise into the users history
    private class LogCaller extends AsyncTask<Void, Void, HistoryModel> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected HistoryModel doInBackground(Void... params) {
            return log();
        }
        @Override
        protected void onPostExecute(HistoryModel result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            if (result != null) {
                //save into the users history
                saveHistory(result);
            } else {
                showMessage("Failed to log to calendar!");
            }
        }
    }
    //Method to log the exercise into the users history
    private HistoryModel log(){
        try{
            //customize event parameters
            //Set summary, description, weights, reps and time
            String summary = model.getExe().getTitle();
            //added
            String reps = model.getReps();
            String weight = model.getWeight();
            String description = "Complete exercise " + model.getExe().getTitle();
            Event event = new Event().setSummary(summary).setDescription(description);
            EventDateTime start = new EventDateTime().setDateTime(startDateTime);
            event.setStart(start);
            DateTime endDateTime = new DateTime(System.currentTimeMillis());
            EventDateTime end = new EventDateTime().setDateTime(endDateTime);
            event.setEnd(end);
            String calendarId = "primary";
            event = mService.events().insert(calendarId, event).execute();
            String link = event.getHtmlLink();
            //Instantiating new object of HistoryModel class
            HistoryModel model = new HistoryModel();
            //Setting the model to contain the following inputs
            model.setLink(link);
            model.setSummary(summary);
            model.setReps(reps);
            model.setWeight(weight);
            model.setStart_time(getTime(startDateTime));
            model.setEnd_time(getTime(endDateTime));
            return model;
        }catch (UserRecoverableAuthIOException userRecoverableException) {
            startActivityForResult(userRecoverableException.getIntent(), REQUEST_AUTHORIZATION);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    //Choose google account to log into
    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    //Code used for the following from stackoverflow
    // https://stackoverflow.com/questions/31759949/android-google-drive-api-token-error
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                break;
            case REQUEST_ACCOUNT_PICKER:

                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings = getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                }
                break;
            case REQUEST_AUTHORIZATION:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Method used to save the history item to the database
    private void saveHistory(HistoryModel model){
        if(model == null) return;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.HISTORY_KEY).child(auth.getCurrentUser().getUid()).push();
        String key = ref.getKey();
        model.setId(key);
        progressBar.setVisibility(View.VISIBLE);
        ref.setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                showMessage("Successfully logged exercise to your history!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                showMessage("Failed to log exercise !");
            }
        });
    }
    private String getTime(DateTime time){
        Date date = new Date(time.getValue());
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }
}
