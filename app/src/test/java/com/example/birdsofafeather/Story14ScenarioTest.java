package com.example.birdsofafeather;

import android.content.Context;

import com.example.birdsofafeather.db.AppDatabase;

import com.example.birdsofafeather.db.BoFDao;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.CourseDao;
import com.example.birdsofafeather.db.Session;
import com.example.birdsofafeather.sorting.PrioritizeMatchNumbers;
import com.example.birdsofafeather.sorting.PrioritizeRecentCourses;
import com.example.birdsofafeather.sorting.PrioritizeSmallCourses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.internal.runner.junit4.AndroidAnnotatedBuilder;
//import androidx.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class Story14ScenarioTest {
    private AppDatabase db;
    private BoFDao bofDao;
    private CourseDao courseDao;
    private PrioritizeMatchNumbers prioritizeMatchNumbers;
    private PrioritizeSmallCourses prioritizeSmallCourses;
    private PrioritizeRecentCourses prioritizeRecentCourses;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.useTestSingleton(context);
        db = AppDatabase.singleton(context);
        bofDao = db.boFDao();
        courseDao = db.courseDao();

        // Creating all BoF, including the user (TJ)
        BoF tj = new BoF("TJ", "bollywood_star.png");
        BoF ah = new BoF("AH", "home_cook.png");
        BoF jd = new BoF("JD", "wizard_wisdom.png");
        BoF jw = new BoF("JW", "carbonated_drink.png");
        BoF ty = new BoF("TY", "heart_sunglasses.png");
        BoF zs = new BoF("ZS", "elden_ring.png");

        // Getting all ids of BoFs to insert courses associated with them
        long tj_uid = db.boFDao().addBoF(tj); // Should get 1
        long ah_uid = db.boFDao().addBoF(ah); // Should get 2
        long jd_uid = db.boFDao().addBoF(jd);
        long jw_uid = db.boFDao().addBoF(jw);
        long ty_uid = db.boFDao().addBoF(ty);
        long zs_uid = db.boFDao().addBoF(zs); // Should get 6

        // Creating all courses objects with the right BoF is associated with them
        Course tj_course1 = new Course(tj_uid, 2021, "FA", "CSE", "9A", "Huge");
        Course tj_course2 = new Course(tj_uid, 2021, "FA", "CSE", "8A", "Large");
        Course tj_course4 = new Course(tj_uid, 2021, "WI", "CSE", "8A", "Small");
        Course tj_course3 = new Course(tj_uid, 2021, "SP", "CSE", "8A", "Medium");
        Course tj_course5 = new Course(tj_uid, 2020, "FA", "CSE", "8A", "Tiny");

        Course ah_course1 = new Course(ah_uid, 2021, "FA", "CSE", "9A", "Huge");
        Course ah_course2 = new Course(ah_uid, 2021, "FA", "CSE", "8A", "Large");
        Course ah_course3 = new Course(ah_uid, 2021, "SP", "CSE", "8A", "Medium");
        Course ah_course4 = new Course(ah_uid, 2021, "WI", "CSE", "8A", "Small");
        Course ah_course5 = new Course(ah_uid, 2020, "FA", "CSE", "8A", "Tiny");

        Course jd_course1 = new Course(jd_uid, 2021, "FA", "CSE", "9A", "Huge");
        Course jd_course2 = new Course(jd_uid, 2021, "FA", "CSE", "8A", "Large");
        Course jd_course3 = new Course(jd_uid, 2021, "SP", "CSE", "8A", "Medium");
        Course jd_course4 = new Course(jd_uid, 2021, "WI", "CSE", "8A", "Small");

        Course jw_course1 = new Course(jw_uid, 2021, "FA", "CSE", "9A", "Huge");
        Course jw_course2 = new Course(jw_uid, 2021, "FA", "CSE", "8A", "Large");
        Course jw_course3 = new Course(jw_uid, 2021, "SP", "CSE", "8A", "Medium");

        Course ty_course1 = new Course(ty_uid, 2021, "FA", "CSE", "9A", "Huge");
        Course ty_course2 = new Course(ty_uid, 2021, "FA", "CSE", "8A", "Large");

        Course zs_course1 = new Course(zs_uid, 2021, "FA", "CSE", "9A", "Huge");

        // Add all the courses into the data base
        db.courseDao().addCourse(tj_course1);
        db.courseDao().addCourse(tj_course2);
        db.courseDao().addCourse(tj_course3);
        db.courseDao().addCourse(tj_course4);
        db.courseDao().addCourse(tj_course5);

        db.courseDao().addCourse(ah_course1);
        db.courseDao().addCourse(ah_course2);
        db.courseDao().addCourse(ah_course3);
        db.courseDao().addCourse(ah_course4);
        db.courseDao().addCourse(ah_course5);

        db.courseDao().addCourse(jd_course1);
        db.courseDao().addCourse(jd_course2);
        db.courseDao().addCourse(jd_course3);
        db.courseDao().addCourse(jd_course4);

        db.courseDao().addCourse(jw_course1);
        db.courseDao().addCourse(jw_course2);
        db.courseDao().addCourse(jw_course3);

        db.courseDao().addCourse(ty_course1);
        db.courseDao().addCourse(ty_course2);

        db.courseDao().addCourse(zs_course1);
    }

    /*
        1. Create the user and add 5 courses to that user
        2. Create 5 bofs, with each having a different number of courses matched with the user.
           Have them be added to the database and in a local List<BoF>
        4. Create an instance of PrioritizeMatchNumbers
        5. Sort with the passed in local List<BoF>
        6. Assert by descending order
     */
    @Test
    public void testPrioritizeMatchNumbers() {
        prioritizeMatchNumbers = new PrioritizeMatchNumbers(db);
        List<BoF> listOfBofs;

        // Get the list of BoFs from database
        listOfBofs = db.boFDao().getAllExceptUser(1);

        // Sort the BoFs with the local list
        prioritizeMatchNumbers.sort(listOfBofs);

        assertEquals("AH", listOfBofs.get(0).getName());
        // Should get Tyler with 2 courses shared
        assertEquals(2, listOfBofs.get(3).getNumCoursesShared());
        assertEquals("wizard_wisdom.png", listOfBofs.get(1).getProfileImgURL());

    }

    @Test
    public void testPrioritizeSmallCourses() {
        prioritizeSmallCourses = new PrioritizeSmallCourses(db);
        List<BoF> listOfBofs;

        // Get the list of BoFs from database
        listOfBofs = db.boFDao().getAllExceptUser(1);

        // Sort the BoFs with the local list
        prioritizeSmallCourses.sort(listOfBofs);

        assertEquals("AH", listOfBofs.get(0).getName());
        // Should get Tyler with 2 courses shared
        assertEquals("carbonated_drink.png", listOfBofs.get(2).getProfileImgURL());
        assertEquals("TY", listOfBofs.get(3).getName());

    }

    @Test
    public void testPrioritizeRecentCourses() {
        prioritizeRecentCourses = new PrioritizeRecentCourses(db);
        List<BoF> listOfBofs;

        // Get the list of BoFs from database
        listOfBofs = db.boFDao().getAllExceptUser(1);

        // Sort the BoFs with the local list
        prioritizeRecentCourses.sort(listOfBofs);

        assertEquals("AH", listOfBofs.get(0).getName());
        // Should get Tyler with 2 courses shared
        assertEquals("elden_ring.png", listOfBofs.get(4).getProfileImgURL());
        // Should be AH before JD because both have the same recent scores from 4/4+ quarters
        assertEquals("TY", listOfBofs.get(3).getName());
        assertEquals("ZS", listOfBofs.get(4).getName());

    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

}
