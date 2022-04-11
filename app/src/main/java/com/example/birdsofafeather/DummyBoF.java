package com.example.birdsofafeather;

import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Course;

import java.util.ArrayList;
import java.util.List;

//Generally a POJO, storing and extracting fields from the container
public class DummyBoF extends BoF {
    private List<Course> courses;

    public DummyBoF(List<Course> courses, int id, String name, String img) {
        super(name, img);
        this.courses = courses;
    }

    public String getName() {
        return this.getName();
    }

    public List<Course> getCourses() {
        return this.courses;
    }

    public boolean addCourse(Course course) {
        if (!(this.courses.contains(course))) {
            if (course != null) {
                courses.add(course);
                return true;
            }
        }
        return false;
    }

    public List<Course> findBoF(DummyBoF bof) {
        List<Course> common = new ArrayList<>(courses);
        common.retainAll(bof.courses);
        if (common.size() != 0) {
            return common;
        }
        return null;
    }
}
