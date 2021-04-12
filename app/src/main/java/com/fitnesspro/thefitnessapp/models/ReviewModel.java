package com.fitnesspro.thefitnessapp.models;

import java.io.Serializable;

public class ReviewModel implements Serializable {
    private String id;
    private String identify;
    private String workout_id;
    private float rate;
    private String description;

    public ReviewModel(){
        rate = 0;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public String getIdentify() {
        return identify;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getRate() {
        return rate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setWorkout_id(String workout_id) {
        this.workout_id = workout_id;
    }

    public String getWorkout_id() {
        return workout_id;
    }
}
