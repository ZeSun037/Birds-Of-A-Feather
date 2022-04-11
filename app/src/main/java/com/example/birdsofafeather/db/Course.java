package com.example.birdsofafeather.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.birdsofafeather.DummyCourse;

import java.io.Serializable;

@Entity(tableName = "courses")
public class Course implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cid")
    private int courseId = 0;

    @ColumnInfo(name = "person_id")
    private long personId;

    @ColumnInfo(name = "department")
    private String department;

    @ColumnInfo(name = "class_number")
    private String classNumber;

    @ColumnInfo(name = "quarter")
    private String quarter;

    @ColumnInfo(name = "size")
    private String size;  // Should it be in hashcode and toString methods?

    @ColumnInfo(name = "year")
    private int year;

    // personId is a foreign key - relates to primary key person_id in the BoF table
//    public Course(long personId, int year, String quarter, String department, String classNumber) {
//        this.personId = personId;
//        this.year = year;
//        this.quarter = quarter;
//        this.classNumber = classNumber;
//        this.department = department;
//    }

    // when class size is given (only case that is not given is in MockNearbyActivity)
    public Course(long personId, int year, String quarter, String department, String classNumber, String size) {
        this.personId = personId;
        this.year = year;
        this.quarter = quarter;
        this.classNumber = classNumber;
        this.department = department;
        this.size = size;
    }

    public int getCourseId() {
        return this.courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getSize() { return size; }

    public void setSize(String size) { this.size = size; }

    @Override
    public int hashCode() {
        String courseName = year + quarter + department + classNumber + size;
        return courseName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        Course course = (Course) o;
        if (quarter.equals(course.quarter)) {
            if (year == course.year) {
                if (department.equals(course.department) &&
                        classNumber.equals(course.classNumber)) {
                    if (size.equals(course.size)) {
                        return  true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return quarter + " " + year + " " + department + " " + classNumber + " (" + size + ")";
    }
}
