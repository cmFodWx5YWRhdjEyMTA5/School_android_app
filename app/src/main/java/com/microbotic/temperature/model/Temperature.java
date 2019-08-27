package com.microbotic.temperature.model;

import com.google.gson.annotations.SerializedName;
import com.microbotic.temperature.app.Config;

public class Temperature {

    @SerializedName(Config.ID)
    private String id;
    @SerializedName(Config.CLASS_ID)
    private String classId;
    @SerializedName(Config.STUDENT_ID)
    private String studentId;
    @SerializedName(Config.FIRST_NAME)
    private String firstName;
    @SerializedName(Config.LAST_NAME)
    private String lastName;
    @SerializedName(Config.GENDER)
    private String gender;
    @SerializedName(Config.PHOTO)
    private String photo;
    @SerializedName(Config.TEMPERATURE)
    private String temperature;
    @SerializedName(Config.DATE)
    private String date;

    public Temperature(String id, String classId, String studentId, String firstName, String lastName, String gender, String photo, String temperature, String date) {
        this.id = id;
        this.classId = classId;
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.photo = photo;
        this.temperature = temperature;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getClassId() {
        return classId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoto() {
        return photo;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getDate() {
        return date;
    }
}
