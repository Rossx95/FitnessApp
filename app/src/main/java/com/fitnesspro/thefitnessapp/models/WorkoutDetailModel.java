package com.fitnesspro.thefitnessapp.models;

import java.io.Serializable;

public class WorkoutDetailModel implements Serializable {
    private String id;
    private String weight;
    private String reps;
    private ExerciseModel exe;

    public WorkoutDetailModel(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeight() {
        return weight;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getReps() {
        return reps;
    }

    public void setExe(ExerciseModel exe) {
        this.exe = exe;
    }

    public ExerciseModel getExe() {
        return exe;
    }
    @Override
    public boolean equals(Object model){
        return this.id.equals(((WorkoutDetailModel)model).getId());
    }
}