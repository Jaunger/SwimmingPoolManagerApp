package com.example.swimmingpoolmanager.network;

import java.util.List;

public class GenerateScheduleResponse {
    private int scheduled;
    private int studentsScheduled;
    private int studentsUnscheduled;
    private List<String> unscheduledStudentIds;
    private List<String> issues;


    public List<String> getIssues() {
        return issues;
    }

    public void setIssues(List<String> issues) {
        this.issues = issues;
    }

    public int getScheduled() {
        return scheduled;
    }

    public void setScheduled(int scheduled) {
        this.scheduled = scheduled;
    }

    public int getStudentsScheduled() {
        return studentsScheduled;
    }

    public void setStudentsScheduled(int studentsScheduled) {
        this.studentsScheduled = studentsScheduled;
    }

    public int getStudentsUnscheduled() {
        return studentsUnscheduled;
    }

    public void setStudentsUnscheduled(int studentsUnscheduled) {
        this.studentsUnscheduled = studentsUnscheduled;
    }

    public List<String> getUnscheduledStudentIds() {
        return unscheduledStudentIds;
    }

    public void setUnscheduledStudentIds(List<String> unscheduledStudentIds) {
        this.unscheduledStudentIds = unscheduledStudentIds;
    }
}
