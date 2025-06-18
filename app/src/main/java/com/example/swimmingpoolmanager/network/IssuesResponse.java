package com.example.swimmingpoolmanager.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IssuesResponse {
    @SerializedName("issues")
    private List<String> issues;
    @SerializedName("unscheduledStudents")
    private List<String> unscheduledStudents;

    public IssuesResponse(List<String> issues, List<String> unscheduledStudents) {
        this.issues = issues;
        this.unscheduledStudents = unscheduledStudents;
    }

    public List<String> getIssues() { return issues; }
    public List<String> getUnscheduledStudents() { return unscheduledStudents; }
}

