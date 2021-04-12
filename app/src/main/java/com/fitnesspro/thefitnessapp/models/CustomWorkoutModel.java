package com.fitnesspro.thefitnessapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomWorkoutModel implements Serializable {

    private String id;
    private String name;
    private List<WorkoutDetailModel> detail_list;

    public CustomWorkoutModel() {
        detail_list = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<WorkoutDetailModel> getDetail_list() {
        return detail_list;
    }
    @Override
    public boolean equals(Object model){
        return this.id.equals(((CustomWorkoutModel)model).getId());
    }
}
