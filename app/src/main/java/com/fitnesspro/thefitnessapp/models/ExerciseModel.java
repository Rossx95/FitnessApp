package com.fitnesspro.thefitnessapp.models;

import java.io.Serializable;

public class ExerciseModel implements Serializable {
    private String id;
    private String title;
    private String image;
    private String exercise;

    public ExerciseModel(){

    }
    public ExerciseModel(String id, String title, String image, String exercise){
        this.id = id;
        this.title = title;
        this.image = image;
        this.exercise = exercise;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
    @Override
    public boolean equals(Object model){
        return this.id.equals(((ExerciseModel)model).getId());
    }
}

