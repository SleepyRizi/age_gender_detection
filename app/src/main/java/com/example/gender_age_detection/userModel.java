package com.example.gender_age_detection;

public class userModel {
    String age;
    String gender;
    String timeStamp;

    public userModel(String age, String gender, String timeStamp) {
        this.age = age;
        this.gender = gender;
        this.timeStamp = timeStamp;
    }
    public userModel() {

        // Empty constructor
    }


    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setAge(String age) {
        this.age = age;


    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    @Override
    public String toString() {
        return "userModel{" +
                "age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
