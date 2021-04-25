package com.fitnesspro.thefitnessapp.models;

import java.io.Serializable;

//To save the state of an object in order to be able to recreate it when needed.
public class ReviewModel implements Serializable {
    //Initialising variables
    private String userid;
    private String id;
    private String review_check;
    private String workout_id;
    private float rate;
    private String description;
    //Constructor, setting the rating to 0
    public ReviewModel(){
        rate = 0;
    }
    //Creating getters and setters
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setReview_check(String identify) {
        this.review_check = identify;
    }
    public String getReview_check() {
        return review_check;
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
    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
}
