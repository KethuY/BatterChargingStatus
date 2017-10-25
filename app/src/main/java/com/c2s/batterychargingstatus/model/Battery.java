package com.c2s.batterychargingstatus.model;

/**
 * Created by satya on 07-Oct-17.
 */

public class Battery {
    int id;
    String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    String json;

}
