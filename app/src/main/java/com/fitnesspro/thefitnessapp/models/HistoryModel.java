package com.fitnesspro.thefitnessapp.models;

import java.io.Serializable;

public class HistoryModel implements Serializable {
    private String id;
    private String summary;
    private String start_time;
    private String end_time;
    private String weight;
    private String reps;
    private String link;

    public HistoryModel(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
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
}
