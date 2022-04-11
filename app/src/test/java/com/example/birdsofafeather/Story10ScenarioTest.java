package com.example.birdsofafeather;


import static android.content.Context.MODE_PRIVATE;
import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.birdsofafeather.db.AppDatabase;
import com.example.birdsofafeather.db.BoF;
import com.example.birdsofafeather.db.Course;
import com.example.birdsofafeather.db.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class Story10ScenarioTest {

    private AppDatabase db;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.useTestSingleton(context);
        db = AppDatabase.singleton(context);

        BoF tj = new BoF("TJ", "bollywood_star.png");
        BoF ah = new BoF("AH", "home_cook.png");
        BoF jw = new BoF("JW", "bollywood_star.png");
        long tj_uid = db.boFDao().addBoF(tj);
        long ah_uid = db.boFDao().addBoF(ah);
        long jw_uid = db.boFDao().addBoF(jw);

        Course tj_course1 = new Course(tj_uid, 2022, "WI", "CSE", "8A", "Large");
        Course tj_course2 = new Course(tj_uid, 2021, "FA", "CSE", "8A", "Large");

        Course ah_course1 = new Course(ah_uid, 2022, "WI", "CSE", "8A", "Large");
        Course ah_course2 = new Course(ah_uid, 2021, "FA", "CSE", "8A", "Large");

        Course jw_course1 = new Course(jw_uid, 2022, "WI", "CSE", "8A", "Large");

        db.courseDao().addCourse(tj_course1);
        db.courseDao().addCourse(tj_course2);

        db.courseDao().addCourse(ah_course1);
        db.courseDao().addCourse(ah_course2);

        db.courseDao().addCourse(jw_course1);

        BoF newB1 = db.boFDao().get(tj_uid);
        BoF newB2 = db.boFDao().get(ah_uid);

        newB1.setHasWaved(1);
        newB2.setHasWaved(1);

        db.boFDao().updateBoF(newB1);
        db.boFDao().updateBoF(newB2);
    }

    @Test
    public void testWaves() {
        // Context of the app under test.
        List<BoF> waves = db.boFDao().getWaved();
        assertEquals(2, waves.size());
        assertEquals("TJ", waves.get(0).getName());
        assertEquals("AH", waves.get(1).getName());
    }

    @Test
    public void testUnwave() {
        List<BoF> waves = db.boFDao().getWaved();
        BoF b1 = waves.get(0);
        b1.setHasWaved(0);
        db.boFDao().updateBoF(b1);
        assertEquals(1, db.boFDao().getWaved().size());
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

}
