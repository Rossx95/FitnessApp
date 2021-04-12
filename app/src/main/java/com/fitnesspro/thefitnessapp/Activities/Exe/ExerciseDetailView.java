package com.fitnesspro.thefitnessapp.Activities.Exe;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fitnesspro.thefitnessapp.Base.BaseActivity;
import com.fitnesspro.thefitnessapp.R;
import com.fitnesspro.thefitnessapp.models.ExerciseModel;

public class ExerciseDetailView extends BaseActivity {
    ExerciseModel model;

    ImageView back_btn;
    TextView title_view;
    ImageView image_view;
    TextView desc_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exe_detail);
        initView();
    }
    private void initView(){
        model = (ExerciseModel)getIntent().getSerializableExtra("model");
        back_btn = findViewById(R.id.back);
        title_view = findViewById(R.id.title_view);
        image_view = findViewById(R.id.image);
        desc_view = findViewById(R.id.desc_view);

        back_btn.setOnClickListener(this);
        initValue();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == back_btn){
            finish();
        }
    }
    private void initValue(){
        title_view.setText(model.getTitle());
        desc_view.setText(model.getExercise());
        Glide.with(context)
                .load(model.getImage())
                .into(image_view);
    }
}
