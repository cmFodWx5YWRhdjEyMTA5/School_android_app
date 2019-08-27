package com.microbotic.temperature.model;

import com.google.gson.annotations.SerializedName;

public class Face {

    @SerializedName("face_detect")
    private Detect detect;
    @SerializedName("face_recg")
    private Recog recog;

    public Face(Detect detect, Recog recog) {
        this.detect = detect;
        this.recog = recog;
    }

    public String getName() {
        return recog.getName();
    }

    public String personId() {
        return recog.getPersonId();
    }

    public int getSmile() {
        return detect.getSmile();
    }

    public int getAge() {
        return detect.getAge();
    }

    public String getGender() {

        if (detect.getGender() == 0) {
            return "Male";
        }

        return "Female";

    }

    public int getConfidence() {
        return recog.getConfidence();
    }


    class Detect {

        @SerializedName("age")
        private int age;
        @SerializedName("gender")
        private int gender;
        @SerializedName("smile")
        private int smile;

        public Detect(int age, int gender, int smile) {
            this.age = age;
            this.gender = gender;
            this.smile = smile;
        }

        private int getAge() {
            return age;
        }

        private int getGender() {
            return gender;
        }

        private int getSmile() {
            return smile;
        }
    }

    class Recog {


        @SerializedName("name")
        private String name;
        @SerializedName("confidence")
        private int confidence;
        @SerializedName("“person_id")
        private String personId;

        public Recog(String name, int confidence, String personId) {
            this.name = name;
            this.confidence = confidence;
            this.personId = personId;
        }


        private String getName() {
            return name;
        }

        private int getConfidence() {
            return confidence;
        }

        private String getPersonId() {
            return personId;
        }
    }


}
