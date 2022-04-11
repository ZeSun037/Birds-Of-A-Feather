package com.example.birdsofafeather;

import android.content.Context;

import com.example.birdsofafeather.db.AppDatabase;

import com.example.birdsofafeather.db.BoFDao;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.CourseDao;
import com.example.birdsofafeather.db.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseUnitTest {
    private AppDatabase db;
    private BoFDao bofDao;
    private CourseDao courseDao;

    // Before newly created tests, make sure to clear the database otherwise --> constraint error
    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.useTestSingleton(context);
        db = AppDatabase.singleton(context);
        bofDao = db.boFDao();
        courseDao = db.courseDao();
    }

    @Test
    public void testCourseIdIncrement() {
        Course course8A = new Course(0, 2022, "Winter", "CSE", "8A", "Large");
        Course course12 = new Course(0, 2022, "Winter", "CSE", "12", "Large");

        assertEquals(1, db.courseDao().addCourse(course8A));
        assertEquals(2, db.courseDao().addCourse(course12));

        assertEquals("Winter 2022 CSE 8A (Large)", db.courseDao().getCourse(1).toString());
        assertEquals("Winter 2022 CSE 12 (Large)", db.courseDao().getCourse(2).toString());

    }

    @Test
    public void UserIdIncrement() {
        BoF terrile = new BoF("Terrile John", "donkey_kong.png");
        BoF joydeep = new BoF("Joydeep Dutta", "pancakes.png");

        assertEquals(1, db.boFDao().addBoF(terrile));
        assertEquals(2, db.boFDao().addBoF(joydeep));

        assertEquals("donkey_kong.png", db.boFDao().get(1).getProfileImgURL());
        assertEquals("pancakes.png", db.boFDao().get(2).getProfileImgURL());
    }

    // Inserting one course
    @Test
    public void testInsertOne() {
        Course course = new Course(0, 2022, "Winter", "CSE", "110", "Large (150-250)");
        db.courseDao().addCourse(course);
        assertEquals(1, db.courseDao().count());
    }

    // Access a course after inserting one into the database
    @Test
    public void testAccessGivenOneEntry() {
        Course course = new Course(0, 2022, "Winter", "CSE", "110","Large (150-250)");
        db.courseDao().addCourse(course);

        Course course1 = db.courseDao().getAllCourses(0).get(0);

        assertEquals(2022, course1.getYear());
        assertEquals("Winter", course1.getQuarter());
        assertEquals("110", course1.getClassNumber());
        assertEquals("CSE", course1.getDepartment());
        assertEquals("Large (150-250)", course1.getSize());
    }

    // Checking count of entries without adding any courses
    @Test
    public void testAccessGivenZeroEntries() {
        assertEquals(0, db.courseDao().count());
        assertEquals(0, db.boFDao().count());
    }

    // Access a course / BoF by id that has not been entered yet
    @Test
    public void testInvalidIdAccess() {
        assertEquals(null, db.courseDao().getCourse(8));
        assertEquals(null, db.boFDao().get(8));
    }

    // Getting name of BoF with valid BoF and empty courses
    @Test
    public void testValidBoFWithEmptyCourses() {
        BoF bof = new BoF("Andrew", "donkey_kong.png");
        long first_bof_id = db.boFDao().addBoF(bof);

        assertEquals("Andrew", db.boFDao().get(first_bof_id).getName());
    }

    // Testing BoF size
    @Test
    public void testBoFWithNoCourses() {
        BoF bof = new BoF("Andrew", "donkey_kong.png");
        db.boFDao().addBoF(bof);

        assertEquals(0, db.courseDao().getAllCourses(bof.getUserId()).size());
    }

    @Test
    public void testBoFWithMultipleCourses() {
        BoF bof = new BoF("Terrile", "basketball_player.png");
        db.boFDao().addBoF(bof);

        Course course1 = new Course(bof.getUserId(), 2021, "Fall", "CSE", "8A", "Large");
        Course course2 = new Course(bof.getUserId(), 2022, "Winter", "CSE", "8A", "Large");

        db.courseDao().addCourse(course1);
        db.courseDao().addCourse(course2);

        List<Course> listOfExpectedCourses = new ArrayList<>();
        listOfExpectedCourses.add(course1);
        listOfExpectedCourses.add(course2);

        assertEquals(2, db.courseDao().getAllCourses(bof.getUserId()).size());
        assertEquals("Fall 2021 CSE 8A (Large)", db.courseDao().getAllCourses(bof.getUserId()).get(0).toString());

        System.err.println("Test ends here");
    }

    @Test
    public void testTwoBoFsMatchingCourses() {
        BoF john = new BoF("Terrile", "basketball_player.png");
        BoF dutta = new BoF("Joydeep", "pancakes.png");

    }

    @Test
    public void testAddSessions() {
        Session cse110 = new Session("CSE 110", "10,1,3");
        Session cse120 = new Session("CSE 120", "");

        db.sessionDao().addSession(cse110);
        db.sessionDao().addSession(cse120);

        assertEquals(2, db.sessionDao().count());
        assertEquals(2, db.sessionDao().getAll().size());
        assertEquals("CSE 110", db.sessionDao().getSessionByName("CSE 110").getName());
    }

    @Test
    public void testAddSessionExtractName() {
        Session cse110 = new Session("CSE 110", "10,1,3");
        db.sessionDao().addSession(cse110);

        Session cse110Db = db.sessionDao().getSessionByName("CSE 110");
        String concatenated = cse110Db.getConcatIds() + ",24536363,102";
        cse110Db.setConcatIds(concatenated);
        db.sessionDao().updateSession(cse110Db);

        assertEquals("10,1,3,24536363,102", db.sessionDao().getSessionByName("CSE 110").getConcatIds());
        assertEquals("10,1,3,24536363,102", db.sessionDao().getAllIdsInSession("CSE 110"));
    }

    @Test
    public void testGetUuid() {
        BoF bof = new BoF("Terrile", "donkey_king_kong");
        String uuid = UUID.randomUUID().toString();
        bof.setUuid(uuid);

        assertEquals(uuid, bof.getUuid());
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

}
