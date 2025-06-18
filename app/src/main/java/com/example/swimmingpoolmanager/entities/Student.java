package com.example.swimmingpoolmanager.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Student {
    @SerializedName("_id")
    private String _id;
    private String firstName;
    private String lastName;
    private List<String> swimmingTypes;
    private String lessonPreference;

    public Student(String firstName, String lastName, List<String> swimmingTypes, String lessonPreference) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.swimmingTypes = swimmingTypes;
        this.lessonPreference = lessonPreference;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getSwimmingTypes() {
        return swimmingTypes;
    }

    public void setSwimmingTypes(List<String> swimmingTypes) {
        this.swimmingTypes = swimmingTypes;
    }

    public String getLessonPreference() {
        return lessonPreference;
    }

    public void setLessonPreference(String lessonPreference) {
        this.lessonPreference = lessonPreference;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}