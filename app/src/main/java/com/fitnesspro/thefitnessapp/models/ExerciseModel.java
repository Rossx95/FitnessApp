package com.fitnesspro.thefitnessapp.models;

import java.io.Serializable;

public class ExerciseModel implements Serializable {
    //Initialising variables
    private String id;
    private String title;
    private String image;
    private String exercise;
    private String primaryMuscle;
    private String secondaryMuscle;
    private String equipment;
    //Empty Constructor
    public ExerciseModel(){
    }
    //Constructor requiring various parameters be entered
    public ExerciseModel(String id, String title, String image, String exercise, String primaryMuscle, String secondaryMuscle, String equipment){
        this.id = id;
        this.title = title;
        this.image = image;
        this.exercise = exercise;
        this.primaryMuscle = primaryMuscle;
        this.secondaryMuscle = secondaryMuscle;
        this.equipment = equipment;
    }
    //Creating getters and setters
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

    public String getPrimaryMuscle() {
        return primaryMuscle;
    }

    public void setPrimaryMuscle(String primaryMuscle) {
        this.primaryMuscle = primaryMuscle;
    }

    public String getSecondaryMuscle() {
        return secondaryMuscle;
    }

    public void setSecondaryMuscle(String secondaryMuscle) {
        this.secondaryMuscle = secondaryMuscle;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }
}

