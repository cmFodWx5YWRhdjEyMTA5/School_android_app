package com.microbotic.temperature.model;

import com.google.gson.annotations.SerializedName;
import com.microbotic.temperature.app.Config;

public class ClassModel {

    @SerializedName(Config.CLASS_ID)
    private String id;
    @SerializedName("class_name")
    private String name;
    @SerializedName("teacher_name")
    private String teacherName;


    public ClassModel(String id, String name, String teacherName) {
        this.id = id;
        this.name = name;
        this.teacherName = teacherName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTeacherName() {
        return teacherName;
    }
}
