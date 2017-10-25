package com.c2s.batterychargingstatus.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by satya on 26-Sep-17.
 */

public class User {
    // [{"Name":"ksreddy","Age":"48","Time":"9/26/2017 6:06:09 PM"}]

    @SerializedName("Name")
    String name;
    @SerializedName("Age")
    String age;
    @SerializedName("Time")
    String time;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
