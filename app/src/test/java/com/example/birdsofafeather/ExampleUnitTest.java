package com.example.birdsofafeather;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;


import android.util.Log;

import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Course;

import java.util.ArrayList;
import java.util.Collection;

import com.example.birdsofafeather.db.Course;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    List<Course> courses  = new ArrayList<>();
    DummyCourse course1 = new DummyCourse(1,
            2020, "Fall", "110", "CSE");
    DummyCourse course2 = new DummyCourse(2,
            2020, "Fall", "110", "CSE");
    DummyCourse course3 = new DummyCourse(1,
            2021, "Winter", "152A", "CSE");
    DummyCourse course4 = new DummyCourse(2,
            2022, "Fall", "140L", "ECE");
    DummyCourse course5 = new DummyCourse(1,
            2020, "Summer II", "11", "MMW");
    DummyCourse course6 = new DummyCourse(1,
            2021, "Summer II", "11", "MMW");


    List<Course> courses1 = new ArrayList<>();
    List<Course> courses2 = new ArrayList<>();

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testCourseSet() {
        courses.add(course1);
        assertEquals(true, courses.contains(course2));
        assertEquals(true, course1.equals(course2));
        assertEquals(true, courses.add(course3));
        assertEquals(true, courses.add(course4));
        assertEquals(3, courses.size());

        assertEquals(false, course1.equals(course3));
        assertEquals(false, course6.equals(course5));
        assertEquals(false, course1.equals(null));
    }

    @Test
    public void testBoFInteraction() {
        courses1.add(course1);
        courses1.add(course3);
        courses1.add(course5);
        courses2.add(course2);
        courses2.add(course4);
        // HashSet<BoF> dummyDatabase = new HashSet<>();
        DummyBoF john = new DummyBoF(courses1, 0, "John", "DummyURL");
        DummyBoF eric = new DummyBoF(courses2, 1, "Eric", "DummyURL");

        //Test mutual coexistence
        boolean join = john.getCourses().contains(course2) && eric.getCourses().contains(course1);

        //Test mutual disjoints
        boolean disjoint1 = john.getCourses().contains(course4) && eric.getCourses().contains(course4);
        boolean disjoint2 = john.getCourses().contains(course4) && eric.getCourses().contains(course5);

        assertEquals(true, join);
        assertEquals(false, disjoint1);
        assertEquals(false, disjoint2);
        assertEquals(false, john.toString().equals(eric.toString()));

        List<Course> common = john.findBoF(eric);
        List<Course> expected = new ArrayList<>();
        expected.add(course1);

        assertEquals(true, john.findBoF(eric).equals(expected));

        assertEquals(false, john.addCourse(course1));
        assertEquals(true, eric.addCourse(course3));
        assertEquals(false, eric.addCourse(null));
    }

}