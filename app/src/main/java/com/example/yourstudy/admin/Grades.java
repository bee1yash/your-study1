package com.example.yourstudy.admin;

public class Grades {
    private String firstModuleGrade;
    private String secondModuleGrade;

    public Grades() {
    }

    public Grades(String firstModuleGrade, String secondModuleGrade) {
        this.firstModuleGrade = firstModuleGrade;
        this.secondModuleGrade = secondModuleGrade;
    }

    public String getFirstModuleGrade() {
        return firstModuleGrade;
    }

    public void setFirstModuleGrade(String firstModuleGrade) {
        this.firstModuleGrade = firstModuleGrade;
    }

    public String getSecondModuleGrade() {
        return secondModuleGrade;
    }

    public void setSecondModuleGrade(String secondModuleGrade) {
        this.secondModuleGrade = secondModuleGrade;
    }
}
