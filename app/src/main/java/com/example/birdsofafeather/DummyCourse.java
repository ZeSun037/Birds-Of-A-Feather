package com.example.birdsofafeather;


import com.example.birdsofafeather.db.Course;

public class DummyCourse extends Course {

    public DummyCourse(int personId, int year, String quarter, String classNumber, String department) {
        super(personId, year, quarter, classNumber, department, "Large (150-250)");
    }

    public int getCourseId() {
        return this.getCourseId();
    }

    public long getPersonId() {
        return this.getPersonId();
    }

    public int getCourse_year() {
        return this.getYear();
    }

    public String getQuarter() {
        return this.getQuarter();
    }

    public String getClass_number() {
        return this.getClassNumber();
    }

    public String getDepartment() {
        return this.getDepartment();
    }

    public void setCourse_year(int course_year) {
        this.setYear(course_year);
    }

    public void setQuarter(String quarter) {
        this.setQuarter(quarter);
    }

    public void setClass_number(String class_number) {
        this.setClassNumber(class_number);
    }

    public void setDepartment(String department) {
        this.setDepartment(department);
    }

    public String toString() {
        return getQuarter() + " " + getYear() + " " + getDepartment() + " " + getClass_number();
    }

}
