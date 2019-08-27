package com.microbotic.temperature.model;

import com.google.gson.annotations.SerializedName;
import com.microbotic.temperature.app.Config;

public class Student {


    @SerializedName(Config.ACCOUNT_ID)
    private String accountId;
    @SerializedName(Config.STUDENT_ID)
    private String studentId;
    @SerializedName(Config.CLASS_ID)
    private String classId;
    @SerializedName(Config.STD_CLASS)
    private String stdClass;
    @SerializedName(Config.FIRST_NAME)
    private String firstName;
    @SerializedName(Config.LAST_NAME)
    private String lastName;
    @SerializedName(Config.PHOTO)
    private String photo;
    @SerializedName(Config.FACE_ID)
    private String faceId;

    public Student(String accountId, String studentId, String classId, String stdClass, String firstName, String lastName, String photo, String faceId) {
        this.accountId = accountId;
        this.studentId = studentId;
        this.classId = classId;
        this.stdClass = stdClass;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.faceId = faceId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getClassId() {
        return classId;
    }

    public String getStdClass() {
        return stdClass;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoto() {
        return photo;
    }

    public String getFaceId() {
        return faceId;
    }


}
