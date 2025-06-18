package com.example.swimmingpoolmanager.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    @SerializedName("_id")
    private String _id;
    private String day;
    private String startTime;
    private String endTime;
    private String type;
    private String swimmingType;
    private String instructorName;
    private List<String> studentNames;

    public Schedule(String _id, String day, String startTime, String endTime, String type, String swimmingType, String instructorName, List<String> studentNames) {
        this._id = _id;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.swimmingType = swimmingType;
        this.instructorName = instructorName;
        this.studentNames = studentNames;
    }

    public List<String> getStudentNames() {
        return studentNames != null ? studentNames : new ArrayList<>();
    }
    public void setStudentNames(List<String> studentNames) { this.studentNames = studentNames; }

    public int getStartHour() {
        return Integer.parseInt(startTime.split(":")[0]);
    }
    public int getStartMinute() {
        return Integer.parseInt(startTime.split(":")[1]);
    }
    public int getDurationMinutes() {
        return type.equals("Private") ? 45 : 60;
    }

    public String getId() { return _id; }
    public void setId(String _id) { this._id = _id; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSwimmingType() { return swimmingType; }
    public void setSwimmingType(String swimmingType) { this.swimmingType = swimmingType; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

}