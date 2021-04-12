package com.fitnesspro.thefitnessapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommunityWorkoutModel implements Serializable {

    private String id;
    private String user_id;
    private String name;
    private int review_num;
    private float rate;
    private List<WorkoutDetailModel> detail_list;

    public CommunityWorkoutModel() {
        review_num = 0;
        rate = 0.0f;
        detail_list = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDetail_list(List<WorkoutDetailModel> exe_list) {
        this.detail_list = exe_list;
    }

    public void setReview_num(int review_num) {
        this.review_num = review_num;
    }

    public int getReview_num() {
        return review_num;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getRate() {
        return rate;
    }

    public List<WorkoutDetailModel> getDetail_list() {
        return detail_list;
    }
    @Override
    public boolean equals(Object model){
        return this.id.equals(((CommunityWorkoutModel)model).getId());
    }
}